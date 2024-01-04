package com.orbits2d

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.orbits2d.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {
    private var _binding: ActivityRootBinding? = null
    private val binding: ActivityRootBinding
        get() = _binding ?: throw Exception("NPE for _binding RootActivity")

    private val renderThread = RenderThread()

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
                Snackbar.make(binding.rootTab, tab?.text.toString(), Snackbar.LENGTH_SHORT).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun setUpSurface() {
        val holder = binding.renderSurface.holder

        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                startRenderThread(renderTarget = holder)
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
                renderThread.running = false
                renderThread.join()
            }
        })
    }

    private fun startRenderThread(renderTarget: SurfaceHolder) {
        renderThread.setHolder(renderTarget)
        renderThread.start()
    }
}