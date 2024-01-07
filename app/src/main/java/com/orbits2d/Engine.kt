package com.orbits2d

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import com.orbits2d.rootactivity.EngineCondition


class Engine(private val engineCondition: EngineCondition) : Thread() {
    private var _renderSurface: SurfaceHolder? = null
    private val renderSurface: SurfaceHolder
        get() = _renderSurface ?: throw Exception("NPE surfaceHolder = null")

    private var isRunning = true
    private var fps: Int = 0

    private var paint = Paint()
    private var canvas: Canvas? = null

    // Double buffering
    private var _bitmapBuffer: Bitmap? = null
    private val bitmapBuffer: Bitmap get() = _bitmapBuffer ?: throw Exception("NPE bitmap")

    private var bufferCondition = Buffer.CALC

    override fun run() {
        canvas = null
        var startTime = System.currentTimeMillis()
        var frameCount = 0

        while (isRunning) {
            try {
                canvas = renderSurface.lockCanvas()

                // Очищаем canvas
                canvas?.drawColor(0)

                // Отрисовка всех объектов на canvas
                drawObjects(canvas!!)

            } finally {
                if (canvas != null) renderSurface.unlockCanvasAndPost(canvas)
            }

            frameCount = fpsCounter(startTime, frameCount)
            if (frameCount == 0) startTime = System.currentTimeMillis()
        }
    }

    private fun drawObjects(c: Canvas) {
        with(paint) {
            color = Color.YELLOW
            style = Paint.Style.FILL
            textSize=24.0f
        }
        c.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paint)

        paint.color=Color.BLACK
        c.drawText(fps.toString(),10.0f,40.0f,paint)

    }

    private fun fpsCounter(startTime: Long, frameCount: Int): Int {
        val elapsedTime = System.currentTimeMillis() - startTime
        return if (elapsedTime < 1000) {
            frameCount + 1
        } else {
            fps = frameCount
            engineCondition.setTitle("FPS $fps")
            0
        }
    }

    fun startEngine(newHolder: SurfaceHolder) {
        _renderSurface = newHolder
        isRunning = true
        start()

        engineCondition.setTitle("GameStart")
    }

    fun stopEngine() {
        isRunning = false
        join()
    }

    enum class Buffer {
        READY, CALC
    }
}