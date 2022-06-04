package com.example

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Paint
import kotlin.random.Random

class Map (columns: Int, rows: Int, val game: Game) {
    private val cells : Array<Array<Cell>> = Array(columns) { Array(rows) { Cell() } }

    //Initialize cells
    init {
        for (columnIndex in 0 until columns) {
            for (rowIndex in 0 until rows) {
                val currentCell = cells[columnIndex][rowIndex]

                //Cell neighbours LEFT and RIGHT
                when (columnIndex) {
                    0 -> {
                        currentCell.neighbours.left = cells[columns - 1][rowIndex]
                        currentCell.neighbours.right = cells[columnIndex + 1][rowIndex]
                    }
                    columns - 1 -> {
                        currentCell.neighbours.left = cells[columnIndex - 1][rowIndex]
                        currentCell.neighbours.right = cells[0][rowIndex]
                    }
                    else -> {
                        currentCell.neighbours.left = cells[columnIndex - 1][rowIndex]
                        currentCell.neighbours.right = cells[columnIndex + 1][rowIndex]
                    }
                }

                //Cell neighbours UP and DOWN
                when (rowIndex) {
                    0 -> {
                        currentCell.neighbours.down = cells[columnIndex][rowIndex + 1]
                        currentCell.neighbours.up = cells[columnIndex][rows - 1]
                    }
                    rows - 1 ->  {
                        currentCell.neighbours.down = cells[columnIndex][0]
                        currentCell.neighbours.up = cells[columnIndex][rowIndex - 1]
                    }
                    else -> {
                        currentCell.neighbours.down = cells[columnIndex][rowIndex + 1]
                        currentCell.neighbours.up = cells[columnIndex][rowIndex - 1]
                    }
                }

                //Cell colors
                if ((columnIndex + rowIndex) % 2 == 0)
                    currentCell.paint = Paint.valueOf("rgb(0, 120, 0)")
                else
                    currentCell.paint = Paint.valueOf("rgb(0, 140, 0)")

                currentCell.posX = columnIndex * Cell.SIZE
                currentCell.posY = rowIndex * Cell.SIZE
            }
        }
    }

    fun draw (graphicsContext: GraphicsContext) {
        for (rowItem in cells) {
            for (columnItem in rowItem) {
                columnItem.draw(graphicsContext)
            }
        }
    }

    fun getCell (x: Int, y: Int) : Cell {
        return cells[x][y]
    }

    fun spawnApple() {
        var x = Random.nextInt(0, cells.size)
        var y = Random.nextInt(0, cells[0].size)
        var currentCell = cells[x][y]
        while (currentCell.content != null) {
            x = Random.nextInt(0, cells.size)
            y = Random.nextInt(0, cells[0].size)
            currentCell = cells[x][y]
        }
        currentCell.content = Apple(currentCell, game)
    }
}