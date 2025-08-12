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

    fun generateWithPatterns(fieldSize: Int, maxPatternCount: Int = fieldSize / 2): Field {
        val cells = Array(fieldSize) { Array<Color?>(fieldSize) { null } }
        val allColors = Color.entries.shuffled(random).subList(0, fieldSize).toMutableList()
        val colorRegions = mutableMapOf<Color, MutableSet<Position>>()
        val patternColors = mutableSetOf<Color>()

        val patternCount = random.nextInt(1, maxPatternCount)
        repeat(patternCount) {
            val pattern = patternFactories.random(random).call()
            pattern.setTransformation(Pattern.Transformation.entries.random(random))
            val pos = Position(
                row = random.nextInt(fieldSize - pattern.width + 1),
                col = random.nextInt(fieldSize - pattern.height + 1)
            )
            if (pattern.canApply(cells, pos)) {
                val color = allColors.removeAt(random.nextInt(allColors.size))
                patternColors += color
                colorRegions[color] = pattern.apply(cells, pos, color).toMutableSet()
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

}