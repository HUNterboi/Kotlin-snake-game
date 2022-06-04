package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint

class Cell {
    var content: Edible? = null
    val neighbours: Neighbours = Neighbours()
    var paint: Paint? = null
    var posX: Int = 0
    var posY: Int = 0

     companion object {
        const val SIZE = 32
    }

    inner class Neighbours {
        var up: Cell? = null
        var right: Cell? = null
        var down: Cell? = null
        var left: Cell? = null
    }

    fun getNeighbourInDirection (direction: Direction) : Cell? {
        return when (direction) {
            Direction.UP -> neighbours.up
            Direction.RIGHT -> neighbours.right
            Direction.DOWN -> neighbours.down
            Direction.LEFT -> neighbours.left
        }
    }

    fun draw (graphicsContext: GraphicsContext) {
        graphicsContext.fill = paint
        graphicsContext.fillRect(posX.toDouble(), posY.toDouble(), SIZE.toDouble(), SIZE.toDouble())
        content?.draw(graphicsContext)
    }

    fun eatContent() {
        content?.eat()
    }
}