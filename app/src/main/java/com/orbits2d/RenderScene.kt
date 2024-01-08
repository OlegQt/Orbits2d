package com.orbits2d

import android.graphics.Path
import android.graphics.Rect
import com.orbits2d.entities.Renderable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class RenderScene {
    private var sceneBounds = Rect()

    private val renderObjects = mutableListOf<Renderable>()

    fun addRenderObject(newRenderObject: Renderable) {
        renderObjects.add(newRenderObject)
    }

    fun setSceneBounds(newBounds: Rect) {
        sceneBounds = newBounds
    }

    fun getFullPathList(): List<Path> {
        return renderObjects.map {
            it.toPath()
        }
    }


    suspend fun updateSceneAsync() {
        withContext(Dispatchers.Default) {
            renderObjects.forEach {
                it.updatePosition(0.1)
            }
            //delay(300)
        }
    }
}