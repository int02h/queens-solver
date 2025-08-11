package com.dpforge.easyraster

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

interface SolutionStep {
    fun doStep(ctx: SolutionContext)
}

object SingleCellRegionStep : SolutionStep {

    override fun doStep(ctx: SolutionContext) {
        val singleCellRegions = ctx.colorRegions.filter { it.value.size == 1 && !ctx.queens.contains(it.value.first()) }
        singleCellRegions.forEach { color, set ->
            ctx.putQueen(set.first())
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
                if (!checkRegions(copy)) {
                    ctx.removePosition(pos)
                    return
                }
            }
        }
    }

    private fun checkRegions(ctx: SolutionContext): Boolean {
        val connections = mutableSetOf<Color>()
        for (color in ctx.colorRegions.keys) {
            connections.clear()
            buildRegionRowConnections(ctx, color, connections)
            val rows = connections.flatMap { color -> ctx.colorRegions.getValue(color) }.map { it.row }.toSet()
            if (rows.size < connections.size) {
                return false
            }

            connections.clear()
            buildRegionColConnections(ctx, color, connections)
            val cols = connections.flatMap { color -> ctx.colorRegions.getValue(color) }.map { it.col }.toSet()
            if (cols.size < connections.size) {
                return false
            }
        }
        return true
    }

    private fun buildRegionRowConnections(ctx: SolutionContext, color: Color, connections: MutableSet<Color>) {
        if (connections.contains(color)) {
            return
        }
        connections += color
        val rows = ctx.colorRegions.getValue(color).map { it.row }.toSet()
        val affectedRegions = ctx.getRegionsOnRows(rows)
        connections += affectedRegions
        affectedRegions.forEach { ar ->
            buildRegionRowConnections(ctx, ar, connections)
        }
    }

    private fun buildRegionColConnections(ctx: SolutionContext, color: Color, connections: MutableSet<Color>) {
        if (connections.contains(color)) {
            return
        }
        connections += color
        val cols = ctx.colorRegions.getValue(color).map { it.col }.toSet()
        val affectedRegions = ctx.getRegionsOnCols(cols)
        connections += affectedRegions
        affectedRegions.forEach { ar ->
            buildRegionColConnections(ctx, ar, connections)
        }
    }
}

// Check if it can ve replaced by BlockOtherRegionStep
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
