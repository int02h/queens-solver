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

    private val solutionFinder = SolutionFinder(exitEarlier = true)

    fun generate(size: Int, allowEnlarging: Boolean = true): Field {
        val isQuickGeneration = size < SLOW_GENERATION_SIZE
        if (isQuickGeneration || !allowEnlarging) {
            return generateSolvableField(size)
        }
        var field = generateSolvableField(SLOW_GENERATION_SIZE - 1)
        while (field.size != size) {
            field = enlargeToSolvable(field)
        }
        return field
    }

    private fun enlargeToSolvable(field: Field): Field {
        var solutions: List<Set<Position>>
        var enlarged: Field
        do {
            enlarged = enlarge(field)
            solutions = solutionFinder.findAllSolutions(enlarged)
        } while (solutions.size != 1)
        return enlarged
    }

    private fun enlarge(field: Field): Field {
        val newSize = field.size + 1
        val cells = Array(newSize) { Array<Color?>(newSize) { null } }
        val target = Position(
            row = random.nextInt(0, newSize),
            col = random.nextInt(0, newSize)
        )
        val allColors = mutableSetOf<Color>()

        for (row in 0 until field.size) {
            val dstRow = if (row < target.row) row else row + 1
            for (col in 0 until field.size) {
                val dstCol = if (col < target.col) col else col + 1
                val srcColor = field.colorRegions.getColor(Position(row, col))
                cells[dstRow][dstCol] = srcColor
                allColors += srcColor
            }
        }

        val newColor = (Color.entries - allColors).random(random)
        cells[target.row][target.col] = newColor

        var canUseNewColor = true
        for (col in target.col - 1 downTo 0) {
            val colorAbove = cells.getOrNull(target.row - 1)?.getOrNull(col)
            val colorBelow = cells.getOrNull(target.row + 1)?.getOrNull(col)
            val possibleColors = setOfNotNull(
                colorAbove,
                colorBelow,
                newColor.takeIf { canUseNewColor && colorAbove != colorBelow }
            )
            val color = possibleColors.random(random)
            if (color != newColor) {
                canUseNewColor = false
            }
            cells[target.row][col] = color
        }

        canUseNewColor = true
        for (col in target.col + 1 until newSize) {
            val colorAbove = cells.getOrNull(target.row - 1)?.getOrNull(col)
            val colorBelow = cells.getOrNull(target.row + 1)?.getOrNull(col)
            val possibleColors = setOfNotNull(
                colorAbove,
                colorBelow,
                newColor.takeIf { canUseNewColor && colorAbove != colorBelow }
            )
            val color = possibleColors.random(random)
            if (color != newColor) {
                canUseNewColor = false
            }
            cells[target.row][col] = color
        }

        canUseNewColor = true
        for (row in target.row - 1 downTo 0) {
            val colorLeft = cells.getOrNull(row)?.getOrNull(target.col - 1)
            val colorRight = cells.getOrNull(row)?.getOrNull(target.col + 1)
            val possibleColors = setOfNotNull(
                colorLeft,
                colorRight,
                newColor.takeIf { canUseNewColor && colorLeft != colorRight }
            )
            val color = possibleColors.random(random)
            if (color != newColor) {
                canUseNewColor = false
            }
            cells[row][target.col] = color
        }

        canUseNewColor = true
        for (row in target.row + 1 until newSize) {
            val colorLeft = cells.getOrNull(row)?.getOrNull(target.col - 1)
            val colorRight = cells.getOrNull(row)?.getOrNull(target.col + 1)
            val possibleColors = setOfNotNull(
                colorLeft,
                colorRight,
                newColor.takeIf { canUseNewColor && colorLeft != colorRight }
            )
            val color = possibleColors.random(random)
            if (color != newColor) {
                canUseNewColor = false
            }
            cells[row][target.col] = color
        }

        val colorRegions = mutableMapOf<Color, MutableSet<Position>>()

        for (row in 0 until newSize) {
            for (col in 0 until newSize) {
                colorRegions.getOrPut(cells[row][col]!!) { mutableSetOf() } += Position(row, col)
            }
        }

        return Field(
            size = newSize,
            colorRegions = colorRegions
        )
    }

    private fun generateSolvableField(size: Int): Field {
        var field: Field? = null
        var solutions: List<Set<Position>> = emptyList()

        do {
            try {
                field = generateWithPatterns(size)
            } catch (e: Exception) {
                println("Failed to generate field with patterns: $e")
                continue
            }
            if (!isValidField(field, useSolver = true)) {
                continue
            }
            solutions = solutionFinder.findAllSolutions(field)
        } while (solutions.size != 1)
        return field!!
    }

    private fun generateWithPatterns(fieldSize: Int): Field {
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

    private fun isValidField(field: Field, useSolver: Boolean): Boolean {
        if (field.colorRegions.size != field.size) {
            return false
        }

        for ((s1, s2) in field.colorRegions.values.toList().allUnorderedPairs()) {
            val rows1 = s1.map { it.row }.toSet()
            val rows2 = s2.map { it.row }.toSet()
            if (rows1.size == 1 && rows2.size == 1 && rows1.first() == rows2.first()) {
                return false
            }

            val cols1 = s1.map { it.col }.toSet()
            val cols2 = s2.map { it.col }.toSet()
            if (cols1.size == 1 && cols2.size == 1 && cols1.first() == cols2.first()) {
                return false
            }
        }

        val fullLine = mutableSetOf<Color>()
        for (row in 0 until field.size) {
            val rowColors = field.colorRegions.getRowColors(row)
            if (rowColors.size == 1) {
                if (!fullLine.add(rowColors.first())) {
                    return false
                }
            }
        }

        fullLine.clear()
        for (col in 0 until field.size) {
            val col = field.colorRegions.getColColors(col)
            if (col.size == 1) {
                if (!fullLine.add(col.first())) {
                    return false
                }
            }
        }

        return try {
            if (useSolver) {
                Solver().solveField(field)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun Map<Color, Set<Position>>.getColor(pos: Position): Color {
        for ((color, posSet) in this) {
            if (posSet.contains(pos)) {
                return color
            }
        }
        error("Bad position")
    }

    private fun Map<Color, Set<Position>>.getRowColors(row: Int): Set<Color> {
        val result = mutableSetOf<Color>()
        for ((color, posSet) in this) {
            for (pos in posSet) {
                if (pos.row == row) {
                    result += color
                }
            }
        }
        return result
    }

    private fun Map<Color, Set<Position>>.getColColors(col: Int): Set<Color> {
        val result = mutableSetOf<Color>()
        for ((color, posSet) in this) {
            for (pos in posSet) {
                if (pos.col == col) {
                    result += color
                }
            }
        }
        return result
    }

    companion object {
        private const val SLOW_GENERATION_SIZE = 10
    }

}