package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Paint

class Apple(val cell: Cell, val game: Game) : Edible {
    companion object {
        private val image: Image = Image(getResource("/apple.png"))
    }

    override fun eat() {
        game.increaseScore(1)
        game.increaseSnake()
        game.spawnApple()
        game.increaseSnake()
    }

    override fun draw(graphicsContext: GraphicsContext) {
        graphicsContext.drawImage(image, cell.posX.toDouble(), cell.posY.toDouble())
    }
}