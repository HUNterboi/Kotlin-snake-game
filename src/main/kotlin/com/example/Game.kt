package com.example

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage

class Game : Application() {

    companion object {
//        512
        private const val WIDTH = 512
        private const val HEIGHT = 256
        private const val maxTimeThreshold: Long = 100
    }

    private lateinit var mainScene: Scene
    private lateinit var graphicsContext: GraphicsContext

    private var lastFrameTime: Long = System.nanoTime()

    private val timer: Timer = Timer()

    private lateinit var map: Map
    private lateinit var snake: Snake
    private var score: Int = 0
    private var isRunning: Boolean = true

    private var currentTimeThreshold: Long = 0

    // use a set so duplicates are not possible
    private val currentlyActiveKeys = mutableSetOf<KeyCode>()

    override fun start(mainStage: Stage) {
        mainStage.title = "Snake"

        val root = Group()
        mainScene = Scene(root)
        mainStage.scene = mainScene

        val canvas = Canvas(WIDTH.toDouble(), HEIGHT.toDouble())
        root.children.add(canvas)

        prepareActionHandlers()

        graphicsContext = canvas.graphicsContext2D

        startGame()

        timer.start()

        mainStage.show()
    }

    private fun startGame () {
        map = Map(WIDTH / Cell.SIZE, HEIGHT / Cell.SIZE, this)
        snake = Snake(3, map.getCell(3, 1), this)
        map.spawnApple()
    }

    private inner class Timer : AnimationTimer() {
        override fun handle(currentNanoTime: Long) {
            tickAndRender(currentNanoTime)
        }
    }

    private fun prepareActionHandlers() {
        mainScene.onKeyPressed = EventHandler { event ->
            currentlyActiveKeys.add(event.code)
        }
        mainScene.onKeyReleased = EventHandler { event ->
            currentlyActiveKeys.remove(event.code)
        }
    }

    private fun tickAndRender(currentNanoTime: Long) {
        // the time elapsed since the last frame, in nanoseconds
        // can be used for physics calculation, etc
        val elapsedNanos = currentNanoTime - lastFrameTime
        lastFrameTime = currentNanoTime

        // clear canvas
        graphicsContext.clearRect(0.0, 0.0, WIDTH.toDouble(), HEIGHT.toDouble())

        map.draw(graphicsContext)

        if (isRunning) {
            snake.changeDirection(currentlyActiveKeys)
            currentTimeThreshold += elapsedNanos
            if (currentTimeThreshold / 1_000_000 >= maxTimeThreshold) {
                snake.tick()
                currentTimeThreshold = 0
            }
        }

        if (!isRunning && currentlyActiveKeys.contains(KeyCode.SPACE)) {
            startGame()
            isRunning = true
            score = 0
        }

        // display crude fps counter
        val elapsedMs = elapsedNanos / 1_000_000
        if (elapsedMs != 0L) {
            graphicsContext.font = Font(12.0)
            graphicsContext.fill = Color.WHITE
            graphicsContext.fillText("${1000 / elapsedMs} fps", 10.0, 10.0)
            graphicsContext.font = Font(40.0)
            graphicsContext.fillText("$score", WIDTH / 2 - "$score".length.toDouble(), 40.0)
            if (!isRunning) {
                val restartText = "Press SPACE to restart"
                graphicsContext.font = Font(20.0)
                graphicsContext.fillText(restartText, (WIDTH / 3 - 40).toDouble(), (HEIGHT / 2).toDouble())
            }
        }
    }

    fun snakeDied() {
        isRunning = false
//        timer.stop()
    }

    fun increaseScore(amount: Int) {
        score += amount
    }

    fun spawnApple() {
        map.spawnApple()
    }

    fun increaseSnake() {
        snake.increaseSize()
    }
}
