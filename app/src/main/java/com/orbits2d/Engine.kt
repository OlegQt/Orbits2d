package com.orbits2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.SurfaceHolder
import com.orbits2d.entities.RoundObject
import com.orbits2d.rootactivity.EngineCondition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Random


class Engine(private val engineCondition: EngineCondition) : Thread() {
    private var _renderSurface: SurfaceHolder? = null
    private val renderSurface: SurfaceHolder
        get() = _renderSurface ?: throw Exception("NPE surfaceHolder = null")

    private var isRunning = true
    private var fps: Int = 0
    private var touchPos = PointF()

    private var paint = Paint()
    private var canvas: Canvas? = null

    // Double buffering
    private var _bitmapBuffer: Bitmap? = null
    private val bitmapBuffer: Bitmap get() = _bitmapBuffer ?: throw Exception("NPE bitmap")

    private var bufferCondition = Buffer.UPDATE

    private var touchTime: Long = 0
    private var isTouched = false

    private val scene = RenderScene()

    override fun run() {
        canvas = null
        var startTime = System.currentTimeMillis()
        var frameCount = 0
        bufferCondition = Buffer.READY

        while (isRunning) {
            try {
                canvas = renderSurface.lockCanvas()

                // Обновляем сцену
                updateScene()

                // Прорисовка сцены с буфером
                drawObjects(canvas!!)


            } finally {
                if (canvas != null) renderSurface.unlockCanvasAndPost(canvas)
            }

            // Функции, которые непрерывно проверяются

            frameCount = fpsCounter(startTime, frameCount)
            if (frameCount == 0) startTime = System.currentTimeMillis()

            fingerTouchListener()
        }
    }

    private fun drawObjects(c: Canvas) {
        with(paint) {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 20.0f
        }
        c.drawRect(0.0f, 0.0f, 100.0f, 24.0f, paint)

        paint.color = Color.BLACK
        c.drawText("FPS $fps", 10.0f, 20.0f, paint)

        when (bufferCondition) {
            Buffer.READY -> paint.color = Color.BLACK
            Buffer.UPDATE -> paint.color = Color.RED
        }

        c.drawRect(120.0f, 0.0f, 220.0f, 24.0f, paint)

        paint.color = Color.BLACK
        c.drawBitmap(bitmapBuffer, 0.0f, 25.0f, null)

    }

    private fun fpsCounter(startTime: Long, frameCount: Int): Int {
        val elapsedTime = System.currentTimeMillis() - startTime
        return if (elapsedTime < 1000) {
            frameCount + 1
        } else {
            fps = frameCount
            engineCondition.setSubTitle("FPS $fps")
            0
        }
    }

    private fun fingerTouchListener() {
        if (isTouched) {
            val elapsedTime = System.currentTimeMillis() - touchTime

            val str = StringBuilder().apply {
                append("touch time $elapsedTime")
                append("   pos( ${touchPos.x.toInt()}, ${touchPos.y.toInt()})")
            }.toString()

            engineCondition.setTitle(str)
        }
    }

    private fun updateScene() {
        if (bufferCondition == Buffer.READY) {
            GlobalScope.launch {
                bufferCondition = Buffer.UPDATE
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        scene.updateSceneAsync()
                        drawSceneOnBuffer()
                    } finally {
                        bufferCondition = Buffer.READY
                    }
                }
            }
        }
    }

    private fun drawSceneOnBuffer() {
        val bufferCanvas = Canvas(bitmapBuffer)
        paint.color = Color.GRAY
        paint.style = Paint.Style.FILL
        bufferCanvas.drawRect(renderSurface.surfaceFrame, paint)

        with(paint) {
            color = Color.CYAN
        }

        scene.getFullPathList().forEach {
            bufferCanvas.drawPath(it, paint)
        }
    }

    fun startEngine(newHolder: SurfaceHolder) {
        _renderSurface = newHolder
        scene.setSceneBounds(renderSurface.surfaceFrame)

        val holderRect = renderSurface.surfaceFrame
        _bitmapBuffer = Bitmap.createBitmap(
            holderRect.width(),
            holderRect.height(),
            Bitmap.Config.RGB_565
        )

        multiplication()

        isRunning = true
        start()
    }

    private fun multiplication() {
        val w = renderSurface.surfaceFrame.width()
        val h = renderSurface.surfaceFrame.height()

        repeat(5000) {
            val pos = PointF(
                Random().nextFloat() * w,
                Random().nextFloat() * h
            )

            scene.addRenderObject(RoundObject(pos.x, pos.y))
        }
    }


    fun setFingerTouchPosition(point: PointF) {
        touchPos = point
    }

    fun stopEngine() {
        isRunning = false
        join()
    }

    fun fingerDown(fingerTouchPos: PointF) {
        touchTime = System.currentTimeMillis()
        isTouched = true
        setFingerTouchPosition(fingerTouchPos)

        val op = RoundObject(fingerTouchPos.x, fingerTouchPos.y)
        scene.addRenderObject(op)
    }

    fun fingerUp() {
        isTouched = false
    }

    enum class Buffer {
        READY, UPDATE
    }
}