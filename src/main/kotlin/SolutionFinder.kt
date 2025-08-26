package com.dpforge.easyraster

import kotlin.math.abs

class SolutionFinder(
    private val exitEarlier: Boolean = false
) {
    private lateinit var field: Field
    private val solutions = mutableListOf<List<Int>>()

    fun findAllSolutions(field: Field): List<Set<Position>> {
        this.field = field
        this.solutions.clear()
        backtrack(emptyList())
        return solutions.map { s ->
            s.withIndex().map { (row, col) -> Position(row, col) }.toSet()
        }
    }

    private fun backtrack(queens: List<Int>) {
        val row = queens.size
        if (row == field.size) {
            if (!shouldReject(queens)) {
                solutions += queens
            }
            return
        }

        for (col in 0 until field.size) {
            val newQueens = queens + col
            if (!shouldReject(newQueens)) {
                backtrack(newQueens)
                if (exitEarlier && solutions.size > 1) {
                    break
                }
            }
        }
    }

    private fun shouldReject(queens: List<Int>): Boolean {
        if (queens.isEmpty()) {
            return false
        }
        val isFullQueenSet = queens.size == field.size
        val columns = queens.toSet()
        if (columns.size != queens.size) { // at least two queens share the same column
            return true
        }
        for (row in 1 until queens.lastIndex) {
            val queenAbove = queens[row - 1]
            val queen = queens[row]
            val queenBelow = queens[row + 1]

            if (abs(queenAbove - queen) <= 1 || abs(queen - queenBelow) <= 1) {
                return true
            }
        }
        for (region in field.colorRegions.values) {
            var count = 0
            for (pos in region) {
                if (pos.row < queens.size && queens[pos.row] == pos.col) {
                    count++
                }
            }
            if (isFullQueenSet && count != 1) {
                return true
            }
            if (!isFullQueenSet && count > 1) {
                return true
            }
        }
        return false
    }
}