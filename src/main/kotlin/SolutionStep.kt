package com.dpforge.easyraster

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
    private val connections = ColorSet()
    private var counter = IntArray(0)
    private val intSet = mutableSetOf<Int>()

    override fun doStep(ctx: SolutionContext) {
        counter = IntArray(ctx.fieldSize)
        val copy = ctx.copy()
        for ((_, region) in ctx.colorRegions) {
            for (pos in region) {
                copy.setFrom(ctx)
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
        for (color in ctx.colorRegions.keys) {
            connections.clear()
            buildRegionRowConnections(ctx, color, connections)
            counter.fill(0)
            var uniqueCount = 0
            for (color in connections) {
                for (pos in ctx.colorRegions.getValue(color)) {
                    if (counter[pos.row] == 0) {
                        counter[pos.row]++
                        uniqueCount++
                    }
                }
            }
            if (uniqueCount < connections.size) {
                return false
            }

            connections.clear()
            buildRegionColConnections(ctx, color, connections)
            counter.fill(0)
            uniqueCount = 0
            for (color in connections) {
                for (pos in ctx.colorRegions.getValue(color)) {
                    if (counter[pos.col] == 0) {
                        counter[pos.col]++
                        uniqueCount++
                    }
                }
            }
            if (uniqueCount < connections.size) {
                return false
            }
        }
        return true
    }

    private fun buildRegionRowConnections(ctx: SolutionContext, color: Color, connections: ColorSet) {
        if (connections.contains(color)) {
            return
        }
        connections.add(color)
        intSet.clear()
        val rows = ctx.colorRegions.getValue(color).mapTo(intSet) { it.row }
        val affectedRegions = ColorSet()
        ctx.getRegionsOnRows(rows, affectedRegions)
        connections.add(affectedRegions)
        affectedRegions.forEach { ar ->
            buildRegionRowConnections(ctx, ar, connections)
        }
    }

    private fun buildRegionColConnections(ctx: SolutionContext, color: Color, connections: ColorSet) {
        if (connections.contains(color)) {
            return
        }
        connections.add(color)
        intSet.clear()
        val cols = ctx.colorRegions.getValue(color).mapTo(intSet) { it.col }
        val affectedRegions = ColorSet()
        ctx.getRegionsOnCols(cols, affectedRegions)
        connections.add(affectedRegions)
        affectedRegions.forEach { ar ->
            buildRegionColConnections(ctx, ar, connections)
        }
    }
}

// Check if it can ve replaced by BlockOtherRegionStep
object ClashOtherRegionStep : SolutionStep {
    private val intSet = mutableSetOf<Int>()

    override fun doStep(ctx: SolutionContext) {
        val copy = ctx.copy()
        for ((_, region) in ctx.colorRegions) {
            for (pos in region) {
                copy.setFrom(ctx)
                copy.putQueen(pos)
                val singleCellRegions = copy.colorRegions.values.toList()
                for ((r1, r2) in singleCellRegions.allUnorderedPairs()) {
                    intSet.clear()
                    r1.mapTo(intSet){ it.col }
                    r2.mapTo(intSet) { it.col }
                    if (intSet.size == 1) {
                        ctx.removePosition(pos)
                        return
                    }
                    intSet.clear()
                    r1.mapTo(intSet){ it.row }
                    r2.mapTo(intSet) { it.row }
                    if (intSet.size == 1) {
                        ctx.removePosition(pos)
                        return
                    }
                }
            }
        }
    }
}
