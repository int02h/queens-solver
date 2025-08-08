package com.dpforge.easyraster

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
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
)

private val ANSI_RESET = "\u001B[0m"
private val ANSI_COLOR_WHITE = "\u001B[97m"

fun main(args: Array<String>) {
    val mode = args.getOrNull(0) ?: "solve"
    when (mode) {
        "solve" -> mainSolve()
        "play" -> mainPlay()
    }
}

private fun mainSolve() {
    val encodedField = listOf(
        "ppppobbb",
        "ppppobbb",
        "ppppobbb",
        "ppppoggg",
        "wwwppppp",
        "rryppppp",
        "rryppppd",
        "rryppppp",
    )
    val field = decodeField(encodedField)
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
    var seed: Long
    var field: Field
    var solutions: List<Set<Position>>
    val startNs = System.nanoTime()
    val seedRandom = Random(System.nanoTime())
    println("Start solution generation")
    do {
        seed = seedRandom.nextLong(1, Long.MAX_VALUE)
        field = Generator(Random(seed)).generate(9)
        solutions = SolutionFinder().findAllSolutions(field)
    } while (solutions.size != 1)
    println("Solution generated in ${(System.nanoTime() - startNs) / 1_000_000} ms")
    try {
        Solver().solveField(field)
    } catch (e: Exception) {
        println("Probably field is not solvable: $e")
    }
    println("Seed: $seed, size: ${field.size}")
    GameUI.show(field, solutions.first())
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

fun printSolutionStep(field: Field, stepColorRegions: Map<Color, Set<Position>>, stepQueens: Set<Position>) {
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

fun decodeField(encodedField: List<String>): Field {
    val colorRegions = mutableMapOf<Color, MutableSet<Position>>()
    encodedField.forEachIndexed { row, rowValue ->
        rowValue.forEachIndexed { col, cell ->
            val pos = Position(row, col)
            val color = Color.decode(cell)
            colorRegions.getOrPut(color) { mutableSetOf() } += pos
        }
    }

    // the field must be square
    val totalCells = encodedField.size * encodedField.size
    if (colorRegions.values.sumOf { it.size } != totalCells) {
        error("Wrongly encoded field")
    }

    return Field(
        size = encodedField.size,
        colorRegions = colorRegions
    )
}

fun encodeField(field: Field): List<String> {
    val encoded = mutableListOf<String>()
    for (row in 0 until field.size) {
        var rowValue = ""
        for (col in 0 until field.size) {
            val color = field.colorRegions.getColor(Position(row, col)) ?: error("Bad field")
            rowValue += color.value
        }
        encoded += rowValue
    }
    return encoded
}

private fun Map<Color, Set<Position>>.getColor(pos: Position): Color? {
    for ((color, posSet) in this) {
        if (posSet.contains(pos)) {
            return color
        }
    }
    return null
}

