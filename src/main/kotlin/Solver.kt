package com.dpforge.easyraster

class Solver(
    private val onStepListener: (Field, SolutionContext) -> Unit = { _, _ -> }
) {
    fun solveField(field: Field): Set<Position> {
        val oneShotSteps = listOf(
            SameColorColumn
        )
        val steps = listOf(
            SingleCellRegionStep,
            HorizontalRegionStep,
            BlockOtherRegionStep,
            ClashOtherRegionStep,
            CompleteRowStep,
            CompleteColumnStep,
        )
        val ctx = SolutionContext(field)

        for (step in oneShotSteps) {
            ctx.hasChanges = false
            step.doOnce(ctx)
            if (ctx.hasChanges) {
                onStepListener(field, ctx)
            }
        }

        while (ctx.queens.size < field.size) {
            for (step in steps) {
                ctx.hasChanges = false
                step.doStep(ctx)
                if (ctx.hasChanges) {
                    onStepListener(field, ctx)
                    break
                }
            }

            if (!ctx.hasChanges) {
                error("I got stuck")
            }
        }

        return ctx.queens
    }
}