package com.dpforge.easyraster

import kotlin.collections.component1
import kotlin.collections.component2

class SolutionContext(field: Field) {

    val fieldSize = field.size

    private val _queens = mutableSetOf<Position>()
    val queens: Set<Position> = _queens

    private val _colorRegions = field.colorRegions
        .mapValues { it.value.toMutableSet() }
        .toMutableMap()
    val colorRegions: Map<Color, Set<Position>> = _colorRegions

    private val cells = Array(field.size) { Array(field.size) { Color.WHITE } }

    var hasChanges = false

    init {
        field.colorRegions.forEach { (color, region) ->
            region.forEach { pos ->
                cells[pos.row][pos.col] = color
            }
        }
    }

    fun putQueen(pos: Position) {
        _queens += pos
        for (col in 0 until pos.col) removePosition(Position(pos.row, col))
        for (col in (pos.col + 1) until fieldSize) removePosition(Position(pos.row, col))
        for (row in 0 until pos.row) removePosition(Position(row, pos.col))
        for (row in (pos.row + 1) until fieldSize) removePosition(Position(row, pos.col))
        removePosition(Position(pos.row - 1, pos.col - 1))
        removePosition(Position(pos.row + 1, pos.col - 1))
        removePosition(Position(pos.row - 1, pos.col + 1))
        removePosition(Position(pos.row + 1, pos.col + 1))
        hasChanges = true
    }

    fun removePosition(pos: Position) {
        for ((_, posSet) in _colorRegions) {
            if (posSet.remove(pos)) {
                hasChanges = true
                break
            }
        }
    }

    fun copy(): SolutionContext {
        val ctx = SolutionContext(
            Field(fieldSize, colorRegions)
        )
        ctx._queens += _queens
        ctx.hasChanges = hasChanges
        return ctx
    }

    fun getRegionsOnRows(rows: Set<Int>, regions: ColorSet) {
        for (row in rows) {
            for (color in cells[row]) {
                regions.add(color)
            }
        }
    }

    fun getRegionsOnCols(cols: Set<Int>, regions: ColorSet) {
        for (rowColors in cells) {
            for (col in cols) {
                regions.add(rowColors[col])
            }
        }
    }
}

