package com.dpforge.easyraster

import kotlin.math.abs

class SolutionFinder {
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
            val count = queens.withIndex().count { (row, col) ->
                region.contains(Position(row, col))
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

    private fun shouldAccept(queens: List<Int>): Boolean {
        return queens.size == field.size
    }

    private fun nextQueens(queens: List<Int>): List<Int>? {
        if (queens.size < field.size) {
            return queens + 0
        }
        val next = queens.toMutableList()
        var q = 0
        next[q] += 1
        while (next[q] == field.size) {
            if (q == field.size - 1) {
                return null
            }
            next[q] = 0
            q += 1
            next[q] += 1
            if (q == field.size - 1) {
                Unit
            }
        }
        return next
    }

}