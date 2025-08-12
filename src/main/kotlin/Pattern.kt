package com.dpforge.easyraster

sealed class Pattern(private vararg val shape: String) {

    private val positions = mutableSetOf<Position>()
    private val shapeWidth: Int = shape.maxOf { it.length }
    private val shapeHeight: Int = shape.size

    var width: Int = shapeWidth
        private set
    var height: Int = shapeHeight
        private set

    init {
        setTransformation(Transformation.NONE)
    }

    fun setTransformation(transformation: Transformation) {
        positions.clear()
        shape.forEachIndexed { row, rowValue ->
            rowValue.forEachIndexed { col, cell ->
                if (cell == '#') {
                    positions += transformation.transform(shapeWidth, shapeHeight, Position(row, col))
                }
            }
        }
        width = positions.maxOf { it.col } + 1
        height = positions.maxOf { it.row } + 1
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

    fun apply(cells: Array<Array<Color?>>, pos: Position, color: Color): Set<Position> {
        val region = getTranslatedPositions(pos.row, pos.col)
        region.forEach { cells[it.row][it.col] = color }
        return region
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

    class UShape : Pattern(
        "#.#",
        "#.#",
        "###",
    )

    class Mug : Pattern(
        "#.#",
        "###",
    )

    class Bowl : Pattern(
        "#..#",
        "####",
    )

    class Ladder : Pattern(
        "#..",
        "##.",
        "###",
    )

    class Square2 : Pattern(
        "##",
        "##",
    )

    class Square3 : Pattern(
        "###",
        "###",
        "###",
    )

    class Saw : Pattern(
        "#.#.#",
        "#####"
    )

    class OneShape : Pattern(
        "##",
        ".#",
        ".#",
        ".#",
        ".#",
    )

    class TwoShape : Pattern(
        "###",
        "..#",
        "###",
        "#..",
        "###",
    )

    class ThreeShape : Pattern(
        "###",
        "..#",
        "###",
        "..#",
        "###",
    )

    class FourShape : Pattern(
        "#.#",
        "#.#",
        "###",
        "..#",
        "..#",
    )

    // 5-Shape is horizontally-flipped 2-Shape
    // 6-Shape, 8-Shape, and 9-Shape have a closed area inside

    class SevenShape : Pattern(
        "###",
        "..#",
        "..#",
        "..#",
        "..#",
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