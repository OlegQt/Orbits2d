package com.orbits2d.entities

open class D3dObject(val x: Float, val y: Float, val z: Float) {
    constructor(xPos: Float, yPos: Float) : this(x = xPos, y = yPos, z = 0.0f)
}