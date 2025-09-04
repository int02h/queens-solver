package com.dpforge.easyraster

import kotlin.collections.component1
import kotlin.collections.component2

class SolutionContext(field: Field) {

    val fieldSize = field.size

    private val _queens = mutableSetOf<Position>()
    val queens: Set<Position> = _queens

    private val _colorRegions = field.colorRegions
        .mapValues { PositionSet(field.size, it.value,) }
        .toMutableMap()
    val colorRegions: Map<Color, PositionSet> = _colorRegions

    private val cells = Array(field.size) { Array<Color?>(field.size) { null } }

    var hasChanges = false

    init {
        field.colorRegions.forEach { (color, region) ->
            region.forEach { pos ->
                cells[pos.row][pos.col] = color
            }
        }
    }

    fun setFrom(other: SolutionContext) {
        _queens.clear()
        _queens.addAll(other._queens)

        _colorRegions.values.forEach { it.clear() }
        cells.forEach { row -> row.fill(null) }

        other.colorRegions.forEach { (color, region) ->
            (_colorRegions.getValue(color)).let { dst ->
                dst.clear()
                dst.addAll(region)
            }
            region.forEach { pos ->
                cells[pos.row][pos.col] = color
            }
        }

        hasChanges = other.hasChanges
    }

    fun putQueen(pos: Position) {
        _queens += pos

        val region = colorRegions.filterValues { it.contains(pos) }.values.first()
        region.forEach { p -> if (p != pos) removePosition(p) }

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
                cells[pos.row][pos.col] = null
                hasChanges = true
                break
            }
        }
    }

    fun copy(): SolutionContext {
        val ctx = SolutionContext(
            Field(fieldSize, colorRegions.mapValues { it.value.toSet() })
        )
        ctx._queens += _queens
        ctx.hasChanges = hasChanges
        return ctx
    }

    fun getRegionsOnRows(rows: Set<Int>, regions: ColorSet) {
        for (row in rows) {
            for (color in cells[row]) {
                color?.let(regions::add)
            }
        }
    }

    fun getRegionsOnCols(cols: Set<Int>, regions: ColorSet) {
        for (rowColors in cells) {
            for (col in cols) {
                rowColors[col]?.let(regions::add)
            }
        }
    }

    fun countCellOnRow(row: Int): Int {
        return cells[row].count { it != null }
    }

    fun countCellOnCol(col: Int): Int {
        var count = 0
        for (row in 0 until cells.size) {
            if (cells[row][col] != null) {
                count++
            }
        }
        return count
    }
}

