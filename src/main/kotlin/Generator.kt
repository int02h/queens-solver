package com.dpforge.easyraster

import kotlin.random.Random

class Generator(
    private val random: Random = Random
) {
    fun generate(fieldSize: Int): Field {
        val cells = Array(fieldSize) { Array<Color?>(fieldSize) { null } }
        val allColors = Color.entries.shuffled(random).subList(0, fieldSize).toMutableList()
        val colorRegions = mutableMapOf<Color, MutableSet<Position>>()

        for (row in 0 until fieldSize) {
            for (col in 0 until fieldSize) {
                val possibleColors = listOfNotNull(
                    cells.getOrNull(row - 1)?.getOrNull(col),
                    cells.getOrNull(row + 1)?.getOrNull(col),
                    cells.getOrNull(row)?.getOrNull(col - 1),
                    cells.getOrNull(row)?.getOrNull(col + 1),
                    allColors.takeIf { it.isNotEmpty() }?.random(random)
                )
                val color = possibleColors.random(random)
                allColors.remove(color)
                cells[row][col] = color
                colorRegions.getOrPut(color) { mutableSetOf() } += Position(row, col)
            }
        }

        return Field(
            size = fieldSize,
            colorRegions = colorRegions
        )
    }

}