package com.orbits2d.entities

open class D3dObject(var x: Float, var y: Float, val z: Float) {
    constructor(xPos: Float, yPos: Float) : this(x = xPos, y = yPos, z = 0.0f)
}