package com.orbits2d.rootactivity

import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.orbits2d.Engine
import com.orbits2d.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity(), EngineCondition {
    private var _binding: ActivityRootBinding? = null
    private val binding: ActivityRootBinding
        get() = _binding ?: throw Exception("NPE for _binding RootActivity")

    private val engine = Engine(this)

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpTabs()
        setUpSurface()
    }

    private fun setUpTabs() {
        val tabArray = arrayListOf("a_page", "b_page")

        tabArray.forEach {
            binding.rootTab.addTab(binding.rootTab.newTab().apply {
                text = it
            })
        }

        binding.rootTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSurface() {
        val holder = binding.renderSurface.holder

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                engine.startEngine(newHolder = holder)
            }

            override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                handler.removeCallbacksAndMessages(null)
                engine.stopEngine()
            }
        })

        binding.renderSurface.setOnTouchListener { view, event ->
            val touchPos = PointF(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> engine.fingerDown(touchPos)
                MotionEvent.ACTION_UP -> engine.fingerUp()
                MotionEvent.ACTION_MOVE->engine.setFingerTouchPosition(touchPos)
            }
            true
        }
    }

    override fun setTitle(titleStr: String) {
        handler.post {
            binding.rootBar.title = titleStr
        }
    }

    override fun setSubTitle(subTitleStr: String) {
        handler.post {
            binding.rootBar.subtitle = subTitleStr
        }
    }

    override fun showInfo(messageInfo: String) {
        Snackbar.make(binding.root, messageInfo, Snackbar.LENGTH_INDEFINITE)
            .setTextMaxLines(20)
            .setAction("OK") { }
            .show()
    }
}