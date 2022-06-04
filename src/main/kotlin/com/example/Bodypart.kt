package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Paint

class Bodypart(
    var cell: Cell,
    var direction: Direction,
    private val snake: Snake,
    var image: Image? = null,
) : Edible {
    init {
        cell.content = this
    }

    fun move () {
        val nextCell = cell.getNeighbourInDirection(direction)
        if (nextCell == null) {
            snake.die()
            return
        }

        nextCell.eatContent()
        cell.content = null
        nextCell.content = this
        cell = nextCell
    }

    override fun draw (graphicsContext: GraphicsContext) {
        graphicsContext.drawImage(image, cell.posX.toDouble(), cell.posY.toDouble())
    }

    override fun eat() {
        snake.die()
    }


}