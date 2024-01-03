package com.orbits2d

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder

class RenderThread:Thread() {
    private var renderTarget:SurfaceHolder? = null
    var isRendering = true

    override fun run() {
        renderTarget?.let {
            while (isRendering){
                val canvas:Canvas = it.lockCanvas()
                render(canvas)
                it.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun setHolder(newHolder: SurfaceHolder){
        renderTarget = newHolder
    }

    private fun render(canvas: Canvas){

        val paint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.FILL
            textSize = 40.0f
        }

        canvas.drawRect(canvas.clipBounds,paint)

        val time = System.currentTimeMillis()

        paint.color = Color.YELLOW
        canvas.drawText("text $time",10.0f,100.0f,paint)

    }
}