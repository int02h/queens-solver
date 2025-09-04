package com.dpforge.easyraster

class PositionSet(fieldSize: Int, positions: Set<Position>) : Iterable<Position> {

    private val data = Array(fieldSize) { row ->
        BooleanArray(fieldSize) { col ->
            positions.contains(Position(row, col))
        }
    }

    var size: Int = positions.size
        private set

    fun isEmpty() = size == 0

    fun clear() {
        for (row in 0 until data.size) {
            data[row].fill(false)
        }
        size = 0
    }

    fun addAll(other: PositionSet) {
        if (other.data.size != data.size) {
            error("The size of sets do not match")
        }
        for (row in 0 until data.size) {
            for (col in 0 until data.size) {
                if (other.data[row][col]) {
                    data[row][col] = true
                    size++
                }
            }
        }
    }

    fun remove(pos: Position): Boolean {
        if (data.getOrNull(pos.row)?.getOrNull(pos.col) == true) {
            data[pos.row][pos.col] = false
            size--
            return true
        }
        return false
    }

    override fun iterator(): Iterator<Position> {
        return PositionIterator(data, size)
    }

    private class PositionIterator(
        private val data: Array<BooleanArray>,
        positionCount: Int
    ) : Iterator<Position> {
        private val fieldSize = data.size
        private var leftCount: Int = positionCount
        private var row = 0
        private var col = 0

        override fun next(): Position {
            while (!data[row][col]) {
                moveColAndRow()
                if (row == fieldSize) {
                    error("Iterator is in invalid state")
                }
            }
            leftCount--
            val pos = Position(row, col)
            moveColAndRow()
            return pos
        }

        private fun moveColAndRow() {
            col++
            if (col == fieldSize) {
                col = 0
                row++
            }
        }

        override fun hasNext(): Boolean = leftCount > 0

    }
}