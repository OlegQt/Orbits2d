package com.orbits2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import com.orbits2d.entities.Renderable
import com.orbits2d.entities.RoundObject
import kotlin.random.Random

class RenderThread : Thread() {
    private var renderTarget: SurfaceHolder? = null
    var running = true

    private var paint = Paint()

    private val dxList = mutableListOf<Renderable>()

    init {
        for (i in 0 until 100) {
            val xPos = Random.nextInt(1000).toFloat()
            val yPos = Random.nextInt(1000).toFloat()
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
                    canvas?.let { render(it) }
                } catch (e: Exception) {
                    //Snackbar.make(binding.rootTab,e.message.toString(), Snackbar.LENGTH_SHORT).show()
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
        val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.RGB_565)
        val bitmapSurface = Canvas(bitmap)

        drawBackground(canvasBitmap = bitmapSurface)
        drawPlanets(canvasBitmap = bitmapSurface)

        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
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
        canvasBitmap.drawText("text $time", 10.0f, 100.0f, paint)

    }
}