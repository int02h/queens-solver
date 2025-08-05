package com.dpforge.easyraster

class Solver(
    private val onStepListener: (Field, SolutionContext) -> Unit = { _, _ -> }
) {
    fun solveField(field: Field): Set<Position> {
        val steps = listOf(
            SingleCellRegionStep,
            HorizontalRegionStep,
            BlockOtherRegionStep,
            ClashOtherRegionStep,
            CompleteRowStep,
            CompleteColumnStep,
            SameColorColumn,
        )
        val ctx = SolutionContext(field)

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
                SameColorColumn.doStep(ctx)
                error("I got stuck")
            }
        }

        return ctx.queens
    }
}