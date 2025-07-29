package com.dpforge.easyraster

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

interface SolutionStep {
    fun doStep(ctx: SolutionContext)
}

interface OneShotSolutionStep {
    fun doOnce(ctx: SolutionContext)
}

object SingleCellRegionStep : SolutionStep {

    override fun doStep(ctx: SolutionContext) {
        val singleCellRegions = ctx.colorRegions.filter { it.value.size == 1 }
        singleCellRegions.forEach { color, set ->
            ctx.removeRegion(color)
            ctx.putQueen(set.first())
        }
    }

}

object HorizontalRegionStep : SolutionStep {
    override fun doStep(ctx: SolutionContext) {
        ctx.colorRegions.values
            .filter { set ->
                set.size > 1 && set.map { pos -> pos.row }.toSet().size == 1
            }
            .forEach { horizontalRegion ->
                val startCol = horizontalRegion.minOf { it.col }
                val endCol = horizontalRegion.maxOf { it.col }
                val row = horizontalRegion.first().row
                for (col in 0 until startCol) ctx.removePosition(Position(row, col))
                for (col in (endCol + 1) until ctx.fieldSize) ctx.removePosition(Position(row, col))
            }
    }

}

object BlockOtherRegionStep : SolutionStep {
    override fun doStep(ctx: SolutionContext) {
        for ((_, region) in ctx.colorRegions) {
            for (pos in region) {
                val copy = ctx.copy()
                copy.putQueen(pos)
                if (copy.colorRegions.values.any { it.isEmpty() }) {
                    ctx.removePosition(pos)
                    return
                }
            }
        }
    }
}

object ClashOtherRegionStep : SolutionStep {
    override fun doStep(ctx: SolutionContext) {
        for ((_, region) in ctx.colorRegions) {
            for (pos in region) {
                val copy = ctx.copy()
                copy.putQueen(pos)
                val singleCellRegions = copy.colorRegions.values.toList()
                for ((r1, r2) in singleCellRegions.allUnorderedPairs()) {
                    if ((r1.map { it.col } + r2.map { it.col }).toSet().size == 1) {
                        ctx.removePosition(pos)
                        return
                    }
                    if ((r1.map { it.row } + r2.map { it.row }).toSet().size == 1) {
                        ctx.removePosition(pos)
                        return
                    }
                }
            }
        }
    }
}

object CompleteRowStep : SolutionStep {
    override fun doStep(ctx: SolutionContext) {
        for (row in 0 until ctx.fieldSize) {
            val freeCells = getRowFreeCells(ctx, row)
            if (freeCells.size == 1) {
                ctx.putQueen(freeCells.first())
                break
            }
        }
    }

    private fun getRowFreeCells(ctx: SolutionContext, row: Int): List<Position> {
        return (0 until ctx.fieldSize).mapNotNull { col ->
            val pos = Position(row, col)
            if (ctx.colorRegions.any { it.value.contains(pos) }) pos else null
        }
    }
}

object CompleteColumnStep : SolutionStep {
    override fun doStep(ctx: SolutionContext) {
        for (col in 0 until ctx.fieldSize) {
            val freeCells = getColumnFreeCells(ctx, col)
            if (freeCells.size == 1) {
                ctx.putQueen(freeCells.first())
                break
            }
        }
    }

    private fun getColumnFreeCells(ctx: SolutionContext, col: Int): List<Position> {
        return (0 until ctx.fieldSize).mapNotNull { row ->
            val pos = Position(row, col)
            if (ctx.colorRegions.any { it.value.contains(pos) }) pos else null
        }
    }
}

object SameColorColumn : OneShotSolutionStep {
    override fun doOnce(ctx: SolutionContext) {
        for (col in 0 until ctx.fieldSize) {
            val freeCells = getColumnFreeCells(ctx, col)
            val isSameColor = freeCells.toSet().size == 1
            if (isSameColor && freeCells.size == ctx.fieldSize) {
                val color = freeCells.first()
                ctx.colorRegions.getValue(color)
                    .filter { it.col != col }
                    .forEach { ctx.removePosition(it) }
            }
        }
    }

    private fun getColumnFreeCells(ctx: SolutionContext, col: Int): List<Color> {
        return (0 until ctx.fieldSize).mapNotNull { row ->
            val pos = Position(row, col)
            ctx.colorRegions.filterValues { it.contains(pos) }.keys.firstOrNull()
        }
    }

}