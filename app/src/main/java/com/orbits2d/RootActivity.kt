package com.orbits2d

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.orbits2d.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var _binding:ActivityRootBinding? = null
    private val binding:ActivityRootBinding get() =  _binding ?: throw Exception("NPE for _binding RootActivity")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTabs()
        setUpSurface()
    }

    private fun setUpTabs(){
        binding.rootTab.addTab(binding.rootTab.newTab().apply {
            text = "newTab"
        })
    }

    private fun setUpSurface(){
        val holder = binding.renderSurface.holder

        holder.addCallback(object :SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                startRenderThread(renderTarget = holder)
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun startRenderThread(renderTarget: SurfaceHolder){
        val canvas = renderTarget.lockCanvas()
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            textSize = 40.0f
        }

        canvas.drawText("text",10.0f,50.0f,paint)

        renderTarget.unlockCanvasAndPost(canvas)
    }
}