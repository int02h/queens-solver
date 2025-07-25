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

    var hasChanges = false

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

    fun removeRegion(color: Color) {
        if (_colorRegions.remove(color) != null) {
            hasChanges = true
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
}

