package com.orbits2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import com.orbits2d.entities.Renderable
import com.orbits2d.entities.RoundObject
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class RenderThread : Thread() {
    private var renderTarget: SurfaceHolder? = null
    var running = true

    private var paint = Paint()

    private var _bitmap: Bitmap? = null
    private val bitmap: Bitmap get() = _bitmap ?: throw Exception("NPE bitmap")

    private var _zBuf: Bitmap? = null
    private val zBuf: Bitmap get() = _zBuf ?: throw Exception("NPE bitmap")

    private var bufferCondition = Buffer.CALC


    private val dxList = mutableListOf<Renderable>()

    init {
        for (i in 0 until 2000) {
            val xPos = Random.nextInt(1000).toFloat()
            val yPos = Random.nextInt(1000).toFloat() + 50.0f
            val planet = RoundObject(x = xPos, y = yPos)

            dxList.add(planet)
        }
    }

    override fun run() {
        var canvas: Canvas?

        renderTarget?.let { holder ->
            while (running) {
                canvas = null
                try {
                    canvas = holder.lockCanvas()
                    canvas?.let {
                        updateMap()
                        render(it)
                    }
                } finally {
                    canvas?.let { holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    fun setHolder(newHolder: SurfaceHolder) {
        renderTarget = newHolder
    }

    private fun render(canvas: Canvas) {
        if (_bitmap == null) _bitmap =
            Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.RGB_565)
        if (_zBuf == null) _zBuf =
            Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.RGB_565)

        when (bufferCondition) {
            Buffer.CALC -> showPreviousFrame(canvas)
            Buffer.READY -> transmitBuffer(canvas)
        }
    }

    private fun showPreviousFrame(surfaceCanvas: Canvas) {
        drawBackground(surfaceCanvas)
        surfaceCanvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
    }

    private fun drawOnBuffer() {
        _zBuf?.let {
            val canvas = Canvas(it)
            drawBackground(canvas)
            drawPlanets(canvas)
        }
    }

    private fun transmitBuffer(surfaceCanvas: Canvas) {
        bitmap.eraseColor(Color.GRAY)

        val bitmapCanvas = Canvas(bitmap)

        bitmapCanvas.drawBitmap(zBuf, 0.0f, 0.0f, paint)

        drawBackground(surfaceCanvas)
        surfaceCanvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
    }

    private fun drawPlanets(canvasBitmap: Canvas) {
        this.dxList.forEach {
            paint.color = Color.GREEN
            canvasBitmap.drawPath(it.toPath(), paint)
        }
    }

    private fun drawBackground(canvasBitmap: Canvas) {
        paint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.FILL
            textSize = 40.0f
        }

        canvasBitmap.drawRect(canvasBitmap.clipBounds, paint)

        val time = System.currentTimeMillis()

        paint.color = Color.YELLOW
        canvasBitmap.drawText("text $time", 10.0f, 40.0f, paint)

    }

    private fun updateMap() {
        bufferCondition = Buffer.CALC
        dxList.forEach {
            runBlocking {
                if (it is RoundObject) it.updatePosition(0.1)
            }
        }
        drawOnBuffer()
        bufferCondition = Buffer.READY
    }

    enum class Buffer {
        READY, CALC
    }
}