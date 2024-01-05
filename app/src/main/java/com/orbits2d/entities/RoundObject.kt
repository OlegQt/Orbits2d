package com.orbits2d.entities

import android.graphics.Path
import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin

class RoundObject(x: Float, y: Float) : D3dObject(xPos = x, yPos = y), Renderable {
    private val radius = 6.0f
    private val isSelected = false

    private var arrowAngle = 0.0
    private var spinSpeedRad: Double = 200.0

    override fun toPath(): Path {
        val aPoint = rotateVector(PointF(x, y), PointF(x + radius*2, y), arrowAngle)

        return Path().apply {
            addCircle(x, y, radius, Path.Direction.CCW)
            addCircle(aPoint.x, aPoint.y, 2.0f, Path.Direction.CCW)

        }
    }

    private fun rotateVector(zeroPoint: PointF, vectorPoint: PointF, angle: Double): PointF {
        val radians = Math.toRadians(angle)
        val cos = cos(radians)
        val sin = sin(radians)
        val dx = vectorPoint.x - zeroPoint.x
        val dy = vectorPoint.y - zeroPoint.y
        val x = zeroPoint.x + dx * cos - dy * sin
        val y = zeroPoint.y + dx * sin + dy * cos
        return PointF(x.toFloat(), y.toFloat())
    }

    suspend fun updatePosition(deltaTime: Double) {
       arrowAngle += spinSpeedRad * deltaTime
    }
}