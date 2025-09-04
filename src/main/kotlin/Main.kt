package com.dpforge.easyraster

import java.io.File
import kotlin.random.Random

private val ANSI_BG_COLOR = mapOf(
    Color.PURPLE to "\u001B[45m",
    Color.VIOLET to "\u001B[0;105m",
    Color.ORANGE to "\u001B[0;101m", // high intensity red
    Color.BLUE to "\u001B[44m",
    Color.GREEN to "\u001B[42m",
    Color.WHITE to "\u001B[47m",
    Color.RED to "\u001B[41m",
    Color.YELLOW to "\u001B[43m",
    Color.DARK_GRAY to "\u001B[0;100m", // high intensity black
    Color.LIGHT_BLUE to "\u001B[0;104m", // high intensity blue
    Color.CYAN to "\u001B[46m"
)

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_COLOR_WHITE = "\u001B[97m"

private val solutionFinder = SolutionFinder(exitEarlier = true)

fun main(args: Array<String>) {
    val mode = args.getOrNull(0) ?: "solve"
    when (mode) {
        "solve" -> mainSolve()
        "play" -> mainPlay()
        "server" -> mainServer(port = args.getOrNull(1)?.toIntOrNull())
        "generator" -> mainGenerator(args.getOrNull(1)?.toIntOrNull())
        "perf" -> mainPerformance()
    }
}

private fun mainSolve() {
    val encodedField = listOf(
        "wwwwwyyyw",
        "wrrwwyydw",
        "wrrrwwddw",
        "wvgwwwddw",
        "vvggwwwww",
        "vvggwobbb",
        "wpppwoobb",
        "wppwwooww",
        "wwwwwwwww",
    )
    val field = FieldCodec.decodeFromHumanText(encodedField)
    printField(field)
    println()

    val allSolutions = SolutionFinder().findAllSolutions(field)
    println("Solution total count: ${allSolutions.size}")

    Solver(
        onStepListener = { field, ctx ->
            printSolutionStep(field, ctx.colorRegions, ctx.queens)
            println()
        }
    ).solveField(field)
}

private fun mainPlay() {
    val startNs = System.nanoTime()
    println("Start solution generation")
    val field = Generator(Random(System.nanoTime())).generate(11, allowEnlarging = false)

    println("Total time      : ${(System.nanoTime() - startNs) / 1_000_000} ms")

    try {
        Solver().solveField(field)
    } catch (e: Exception) {
        println("Probably field is not solvable: $e")
    }
    println("Field:")
    println(FieldCodec.encodeToCompressedString(field))
    GameUI.show(field, solutionFinder.findAllSolutions(field).first())
}

private fun mainPerformance() {
    val startNs = System.nanoTime()
    println("Start performance testing")
    val field = Generator(Random(12345)).generate(11, allowEnlarging = false)
    println("Total time: ${(System.nanoTime() - startNs) / 1_000_000} ms")
    if (
        FieldCodec.encodeToCompressedString(field) != "2l3w3o3c2l3wov4c2l3w2v4c3l2wvbcp2c3l2wv2b3y3ld4b3ylr6d3y3r2d5gy3r2d6g5r6g4r7g"
    ) {
        error("Field has changed")
    }
}

private fun mainServer(port: Int?) {
    WebServer().start(port = port ?: 0)
}

private fun mainGenerator(size: Int?) {
    if (size == null) {
        println("Size is not provided")
        return
    }
    GeneratorApp(folder = File("field-db"), size = size).start()
}

fun printField(field: Field) {
    for (row in 0 until field.size) {
        for (col in 0 until field.size) {
            val pos = Position(row, col)
            val color = field.colorRegions.getColor(pos) ?: error("Wrong position")
            print(ANSI_BG_COLOR.getValue(color) + "[ ]" + ANSI_RESET)
        }
        println()
    }
}

fun printSolutionStep(field: Field, stepColorRegions: Map<Color, PositionSet>, stepQueens: Set<Position>) {
    for (row in 0 until field.size) {
        for (col in 0 until field.size) {
            val pos = Position(row, col)
            val color = field.colorRegions.getColor(pos) ?: error("Wrong position")
            val hasQueen = stepQueens.contains(pos)
            val isCrossed = stepColorRegions.getColor(pos) == null
            print(ANSI_BG_COLOR.getValue(color))
            when {
                hasQueen -> print("${ANSI_COLOR_WHITE}[Q]")
                isCrossed -> print("[x]")
                else -> print("[ ]")
            }
            print(ANSI_RESET)
        }
        println()
    }
}

private fun Map<Color, Iterable<Position>>.getColor(pos: Position): Color? {
    for ((color, posSet) in this) {
        if (posSet.contains(pos)) {
            return color
        }
    }
    return null
}

