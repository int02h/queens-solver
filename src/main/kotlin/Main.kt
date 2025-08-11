package com.dpforge.easyraster

import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.measureTime

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
)

private const val ANSI_RESET = "\u001B[0m"
private const val ANSI_COLOR_WHITE = "\u001B[97m"

fun main(args: Array<String>) {
    val mode = args.getOrNull(0) ?: "solve"
    when (mode) {
        "solve" -> mainSolve()
        "play" -> mainPlay()
    }
}

private fun mainSolve() {
    val encodedField = listOf(
        "dddddddww",
        "dddpppyyw",
        "ddpppppyw",
        "ddpppppyy",
        "doppoppoy",
        "goooooooy",
        "grrororrv",
        "gbrrrrrvv",
        "gbbbbbvvv",
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
    var solutions: List<Set<Position>> = emptyList()
    val startNs = System.nanoTime()
    val seedRandom = Random(System.nanoTime())
    val solutionFinder = SolutionFinder(exitEarlier = true)
    var generationTotalTime = 0L
    var solutionTotalTime = 0L
    var invalidFieldCount = 0
    println("Start solution generation")
    do {
        seed = seedRandom.nextLong(1, Long.MAX_VALUE)
        generationTotalTime += measureTime {
            field = Generator(Random(seed)).generate(10)
        }.toLong(DurationUnit.NANOSECONDS)

        if (!isValidField(field)) {
            invalidFieldCount++
            continue
        }

        solutionTotalTime += measureTime {
            solutions = solutionFinder.findAllSolutions(field)
        }.toLong(DurationUnit.NANOSECONDS)
    } while (solutions.size != 1)

    println("Total time      : ${(System.nanoTime() - startNs) / 1_000_000} ms")
    println("Generation time : ${(generationTotalTime) / 1_000_000} ms")
    println("Solution time   : ${(solutionTotalTime) / 1_000_000} ms")
    println()
    println("Invalid field count: $invalidFieldCount")

    try {
        Solver().solveField(field)
    } catch (e: Exception) {
        println("Probably field is not solvable: $e")
    }
    println("Field:")
    println(encodeField(field).joinToString(separator = "|"))
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

fun isValidField(field: Field): Boolean {
    if (field.colorRegions.size != field.size) {
        return false
    }

    for ((s1, s2) in field.colorRegions.values.toList().allUnorderedPairs()) {
        val rows1 = s1.map { it.row }.toSet()
        val rows2 = s2.map { it.row }.toSet()
        if (rows1.size == 1 && rows2.size == 1 && rows1.first() == rows2.first()) {
            return false
        }

        val cols1 = s1.map { it.col }.toSet()
        val cols2 = s2.map { it.col }.toSet()
        if (cols1.size == 1 && cols2.size == 1 && cols1.first() == cols2.first()) {
            return false
        }
    }

    val fullLine = mutableSetOf<Color>()
    for (row in 0 until field.size) {
        val rowColors = field.colorRegions.getRowColors(row)
        if (rowColors.size == 1) {
            if (!fullLine.add(rowColors.first())) {
                return false
            }
        }
    }

    fullLine.clear()
    for (col in 0 until field.size) {
        val col = field.colorRegions.getColColors(col)
        if (col.size == 1) {
            if (!fullLine.add(col.first())) {
                return false
            }
        }
    }

    return try {
        Solver().solveField(field)
        true
    } catch (_: Exception) {
        false
    }
}

private fun Map<Color, Set<Position>>.getColor(pos: Position): Color? {
    for ((color, posSet) in this) {
        if (posSet.contains(pos)) {
            return color
        }
    }
    return null
}

private fun Map<Color, Set<Position>>.getRowColors(row: Int): Set<Color> {
    val result = mutableSetOf<Color>()
    for ((color, posSet) in this) {
        for (pos in posSet) {
            if (pos.row == row) {
                result += color
            }
        }
    }
    return result
}

private fun Map<Color, Set<Position>>.getColColors(col: Int): Set<Color> {
    val result = mutableSetOf<Color>()
    for ((color, posSet) in this) {
        for (pos in posSet) {
            if (pos.col == col) {
                result += color
            }
        }
    }
    return result
}

