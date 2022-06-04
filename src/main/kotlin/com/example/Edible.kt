package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

interface Edible {
    fun eat()
    fun draw(graphicsContext: GraphicsContext)
}