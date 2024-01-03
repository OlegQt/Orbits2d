package com.orbits2d.entities

import android.graphics.Path

interface Renderable {
    fun toPath():Path
}