package com.dpforge.easyraster

sealed class Pattern(private vararg val shape: String) {

    private val positions = mutableSetOf<Position>()
    private val width: Int = shape.maxOf { it.length }
    private val height: Int = shape.size

    init {
        setTransformation(Transformation.NONE)
    }

    fun setTransformation(transformation: Transformation) {
        positions.clear()
        shape.forEachIndexed { row, rowValue ->
            rowValue.forEachIndexed { col, cell ->
                if (cell == '#') {
                    positions += transformation.transform(width, height, Position(row, col))
                }
            }
        }
    }

    fun canApply(cells: Array<Array<Color?>>, pos: Position): Boolean {
        if (pos.row < 0 || pos.col < 0) {
            return false
        }
        val fieldSize = cells.size
        if (pos.row + height >= fieldSize || pos.col + width >= fieldSize) {
            return false
        }
        return getTranslatedPositions(pos.row, pos.col).all { cells[it.row][it.col] == null }
    }

    fun apply(cells: Array<Array<Color?>>, pos: Position, color: Color) {
        getTranslatedPositions(pos.row, pos.col).forEach { cells[it.row][it.col] = color }
    }

    private fun getTranslatedPositions(dRow: Int, dCol: Int): Set<Position> {
        return positions
            .map {
                Position(it.row + dRow, it.col + dCol)
            }
            .toSet()
    }

    class TShape : Pattern(
        "###",
        ".#.",
        ".#.",
    )

    class Ladder : Pattern(
        "#..",
        "##.",
        "###",
    )

    enum class Transformation {
        NONE {
            override fun transform(width: Int, height: Int, position: Position): Position = position
        },
        FLIP_VERTICALLY {
            override fun transform(width: Int, height: Int, position: Position): Position =
                Position(height - position.row - 1, position.col)
        },
        FLIP_HORIZONTALLY {
            override fun transform(width: Int, height: Int, position: Position): Position =
                Position(position.row, width - position.col - 1)
        },
        ROTATE_90_CW {
            override fun transform(
                width: Int,
                height: Int,
                position: Position
            ): Position {
                return Position(position.col, height - position.row - 1)
            }
        },
        ROTATE_90_CCW {
            override fun transform(
                width: Int,
                height: Int,
                position: Position
            ): Position {
                return Position(width - position.col - 1, position.row)
            }
        },
        ROTATE_180 {
            override fun transform(
                width: Int,
                height: Int,
                position: Position
            ): Position {
                return Position(height - position.row - 1, width - position.col - 1)
            }
        };

        abstract fun transform(width: Int, height: Int, position: Position): Position
    }

}