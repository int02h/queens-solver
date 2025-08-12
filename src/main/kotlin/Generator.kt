package com.dpforge.easyraster

import java.util.concurrent.Callable
import kotlin.random.Random
import kotlin.reflect.full.createInstance

class Generator(
    private val random: Random = Random
) {

    private val patternFactories = Pattern::class.sealedSubclasses.map {
        Callable { it.createInstance() }
    }

    fun generateWithPatterns(fieldSize: Int): Field {
        val cells = Array(fieldSize) { Array<Color?>(fieldSize) { null } }
        val allColors = Color.entries.shuffled(random).subList(0, fieldSize).toMutableList()
        val colorRegions = mutableMapOf<Color, MutableSet<Position>>()
        val patternColors = mutableSetOf<Color>()

        var cannotApplyCount = 0
        while (true) {
            val pattern = patternFactories.random(random).call()
            pattern.setTransformation(Pattern.Transformation.entries.random(random))
            val freePositions = getFreePositions(
                cells = cells,
                maxRow = fieldSize - pattern.height,
                maxCol = fieldSize - pattern.width
            )
            if (freePositions.isEmpty()) {
                break
            }
            val pos = freePositions.random(random)
            if (pattern.canApply(cells, pos)) {
                val color = allColors.removeAt(random.nextInt(allColors.size))
                patternColors += color
                colorRegions[color] = pattern.apply(cells, pos, color).toMutableSet()
            } else {
                cannotApplyCount++
                if (cannotApplyCount == 10) {
                    break
                }
            }
        }

        for (row in 0 until fieldSize) {
            for (col in 0 until fieldSize) {
                if (cells[row][col] != null) {
                    continue
                }
                val possibleColors = listOfNotNull(
                    cells.getOrNull(row - 1)?.getOrNull(col),
                    cells.getOrNull(row + 1)?.getOrNull(col),
                    cells.getOrNull(row)?.getOrNull(col - 1),
                    cells.getOrNull(row)?.getOrNull(col + 1),
                    allColors.takeIf { it.isNotEmpty() }?.random(random)
                ).toMutableList()
                if (!possibleColors.all(patternColors::contains)) {
                    possibleColors -= patternColors
                }
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

    private fun getFreePositions(
        cells: Array<Array<Color?>>,
        maxRow: Int,
        maxCol: Int
    ): Set<Position> {
        val result = mutableSetOf<Position>()
        for (row in 0..maxRow) {
            for (col in 0..maxCol) {
                if (cells[row][col] == null) {
                    result += Position(row, col)
                }
            }
        }
        return result
    }

}