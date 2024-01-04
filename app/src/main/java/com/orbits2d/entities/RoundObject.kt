package com.orbits2d.entities

import android.graphics.Path

class RoundObject(x: Float, y: Float) : D3dObject(xPos = x, yPos = y), Renderable {
    private val radius = 12.0f
    override fun toPath(): Path {
        return Path().apply {
            addCircle(x, y, radius, Path.Direction.CCW)
        }
    }
}