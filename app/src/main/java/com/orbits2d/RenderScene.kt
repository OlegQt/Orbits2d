package com.orbits2d

import android.graphics.Rect
import com.orbits2d.entities.Renderable

class RenderScene {
    private var sceneBounds = Rect()

    private val renderObjects = mutableListOf<Renderable>()

    fun addRenderObject(newRenderObject: Renderable) {
        renderObjects.add(newRenderObject)
    }

    fun setSceneBounds(newBounds: Rect) {
        sceneBounds = newBounds
    }
}