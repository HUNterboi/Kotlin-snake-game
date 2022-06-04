package com.example

enum class Direction {
    UP, RIGHT, DOWN, LEFT;

    companion object {
        fun inverse(direction: Direction): Direction {
            return when (direction) {
                UP -> { DOWN }
                RIGHT -> { LEFT }
                DOWN -> { UP }
                LEFT -> { RIGHT }
            }
        }
    }
}