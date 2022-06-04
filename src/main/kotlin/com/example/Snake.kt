package com.example

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Paint
import java.security.InvalidParameterException

class Snake (length: Int, startingCell: Cell, private val game: Game) {
    private val bodyparts: MutableList<Bodypart> = mutableListOf()
    private var growInNextTick: Boolean = false
    private var destinationChangedSinceLastTick: Boolean = false

    companion object {
        private enum class SnakeImageType { HEAD, BASE, CORNER, TAIL }

        private val imgHeadUp: Image = Image(getResource("/head_up.png"))
        private val imgHeadRight: Image = Image(getResource("/head_right.png"))
        private val imgHeadDown: Image = Image(getResource("/head_down.png"))
        private val imgHeadLeft: Image = Image(getResource("/head_left.png"))

        private val imgBaseVertical: Image = Image(getResource("/base_vertical.png"))
        private val imgBaseHorizontal: Image = Image(getResource("/base_horizontal.png"))

        private val imgCornerUpRight: Image = Image(getResource("/corner_up_right.png"))
        private val imgCornerRightDown: Image = Image(getResource("/corner_right_down.png"))
        private val imgCornerDownLeft: Image = Image(getResource("/corner_down_left.png"))
        private val imgCornerLeftUp: Image = Image(getResource("/corner_left_up.png"))

        private val imgTailUp: Image = Image(getResource("/tail_up.png"))
        private val imgTailRight: Image = Image(getResource("/tail_right.png"))
        private val imgTailDown: Image = Image(getResource("/tail_down.png"))
        private val imgTailLeft: Image = Image(getResource("/tail_left.png"))

        private fun getSnakeImage(snakeImageType: SnakeImageType, vararg directions: Direction) : Image {
            val directionSet : MutableSet<Direction> = mutableSetOf()
            for (item in directions) {
                directionSet.add(item)
            }

            when (snakeImageType) {
                SnakeImageType.HEAD -> {
                    if (directionSet.isNotEmpty()) {
                        return when (directionSet.first()) {
                            Direction.UP -> { imgHeadUp }
                            Direction.RIGHT -> { imgHeadRight }
                            Direction.DOWN -> { imgHeadDown }
                            Direction.LEFT -> { imgHeadLeft }
                        }
                    }
                }

                SnakeImageType.BASE -> {
                    if (directionSet.isNotEmpty()) {
                        return when (directionSet.first()) {
                            Direction.UP -> { imgBaseVertical }
                            Direction.RIGHT -> { imgBaseHorizontal }
                            Direction.DOWN -> { imgBaseVertical }
                            Direction.LEFT -> { imgBaseHorizontal }
                        }
                    }
                }

                SnakeImageType.CORNER -> {
                    if (directionSet.size >= 2) {
                        return when {
                            directionSet.contains(Direction.UP) && directionSet.contains(Direction.RIGHT) -> { imgCornerUpRight }
                            directionSet.contains(Direction.RIGHT) && directionSet.contains(Direction.DOWN) -> { imgCornerRightDown }
                            directionSet.contains(Direction.DOWN) && directionSet.contains(Direction.LEFT) -> { imgCornerDownLeft }
                            directionSet.contains(Direction.LEFT) && directionSet.contains(Direction.UP) -> { imgCornerLeftUp }
                            else -> { throw InvalidParameterException("Bad directions for corner snake piece") }
                        }
                    }
                    else
                        throw InvalidParameterException("Bad directions for corner snake piece")
                }

                SnakeImageType.TAIL -> {
                    if (directionSet.isNotEmpty()) {
                        return when (directionSet.first()) {
                            Direction.UP -> { imgTailUp }
                            Direction.RIGHT -> { imgTailRight }
                            Direction.DOWN -> { imgTailDown }
                            Direction.LEFT -> { imgTailLeft }
                        }
                    }
                }
            }
            throw InvalidParameterException("SnakeImageType not specified")
        }
    }

    init {
        var currentCell: Cell? = startingCell
        bodyparts.add(Bodypart(currentCell!!, Direction.RIGHT, this, imgHeadRight))
        currentCell = currentCell.neighbours.left
        if (currentCell == null || currentCell?.content != null) {
            throw InvalidParameterException("There is not enough room for the Snake.")
        }
        for (i in 1 until length - 1) {
            bodyparts.add(Bodypart(currentCell!!, Direction.RIGHT,this, imgBaseVertical))

            currentCell = currentCell.neighbours.left
            if (currentCell == null || currentCell?.content != null) {
                throw InvalidParameterException("There is not enough room for the Snake.")
            }
        }
        bodyparts.add(Bodypart(currentCell!!, Direction.RIGHT, this, imgTailRight))
    }

    fun changeDirection(pressedKeys: MutableSet<KeyCode>) {
        if (destinationChangedSinceLastTick)
            return
        if (pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP)) {
            if (bodyparts[0].direction != Direction.DOWN) {
                bodyparts[0].direction = Direction.UP
                destinationChangedSinceLastTick = true
            }
        }
        else if (pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT)) {
            if (bodyparts[0].direction != Direction.LEFT) {
                bodyparts[0].direction = Direction.RIGHT
                destinationChangedSinceLastTick = true
            }
        }
        else if (pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN)) {
            if (bodyparts[0].direction != Direction.UP) {
                bodyparts[0].direction = Direction.DOWN
                destinationChangedSinceLastTick = true
            }
        }
        else if (pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT)) {
            if (bodyparts[0].direction != Direction.RIGHT) {
                bodyparts[0].direction = Direction.LEFT
                destinationChangedSinceLastTick = true
            }
        }
    }

//    private fun grow () {
//        if (growInNextTick) {
//            val lastPart = bodyparts.last()
//            val newPart = Bodypart(lastPart.cell.getNeighbourInDirection(Direction.inverse(lastPart.direction))!!, lastPart.direction, this)
//            bodyparts.add(newPart)
//            growInNextTick = false
//        }
//    }

    fun tick () {
        destinationChangedSinceLastTick = false
        var newPart: Bodypart? = null
        for (part in bodyparts) {
            val cellBeforeMove = part.cell
            part.move()
            if (growInNextTick && part == bodyparts.last()) {
                newPart = Bodypart(cellBeforeMove, part.direction, this)
            }
        }

//        grow()

        if (growInNextTick) {
            if (newPart != null)
                bodyparts.add(newPart)
            growInNextTick = false
        }

        //Setting head image
        bodyparts.first().image = getSnakeImage(SnakeImageType.HEAD, bodyparts.first().direction)

        for (iterator in bodyparts.size - 1 downTo  1) {
            val current = bodyparts[iterator]
            val ahead = bodyparts[iterator - 1]
            changePartImages(current, ahead)
            current.direction = ahead.direction
        }
    }

    private fun changePartImages (current: Bodypart, ahead: Bodypart) {
        if (current == bodyparts.last())
            current.image = getSnakeImage(SnakeImageType.TAIL, ahead.direction)
        else {
            if (ahead.direction != current.direction)
                current.image = getSnakeImage(SnakeImageType.CORNER, Direction.inverse(current.direction), ahead.direction)
            else
                current.image = getSnakeImage(SnakeImageType.BASE, current.direction)
        }
    }

    fun die () {
        game.snakeDied()
    }

    fun increaseSize() {
        growInNextTick = true
    }
}