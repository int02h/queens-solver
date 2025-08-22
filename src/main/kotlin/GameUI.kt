package com.dpforge.easyraster

import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import kotlin.collections.set
import kotlin.system.exitProcess
import java.awt.Color as AWTColor

class GameUI(
    private val field: Field,
    private val solution: Set<Position>
) : JPanel() {

    private val cells = Array(field.size) { Array(field.size) { CellState.EMPTY } }
    private var isSolved = false
    private val queenAutoCrosses = mutableMapOf<Position, Set<Position>>()

    init {
        preferredSize = Dimension(field.size * CELL_SIZE, field.size * CELL_SIZE)

        val mouseListener = object : MouseAdapter() {
            private var pressRow = -1
            private var pressCol = -1
            private var lastRow = -1
            private var lastCol = -1

            override fun mousePressed(e: MouseEvent) {
                if (isSolved) {
                    exitProcess(0)
                }
                pressRow = e.y / CELL_SIZE
                lastRow = pressRow
                pressCol = e.x / CELL_SIZE
                lastCol = pressCol

                cells[pressRow][pressCol] = when (cells[pressRow][pressCol]) {
                    CellState.EMPTY -> CellState.CROSSED
                    CellState.CROSSED -> {
                        putCrossesForQueen(pressRow, pressCol)
                        CellState.QUEEN
                    }
                    CellState.QUEEN -> {
                        removeCrossesForQueen(pressRow, pressCol)
                        CellState.EMPTY
                    }
                }
                checkSolution()
                repaint()
            }

            override fun mouseReleased(e: MouseEvent) {
                pressRow = -1
                pressCol = -1
            }

            override fun mouseDragged(e: MouseEvent) {
                if (pressRow < 0 || pressCol < 0) {
                    return
                }
                val row = e.y / CELL_SIZE
                val col = e.x / CELL_SIZE
                if (row < 0 || row >= field.size || col < 0 || col >= field.size) {
                    return
                }
                if (row != lastRow || col != lastCol) {
                    lastRow = row
                    lastCol = col
                    if (cells[row][col] == CellState.EMPTY) {
                        cells[row][col] = CellState.CROSSED
                        repaint()
                    }
                }
            }
        }

        addMouseListener(mouseListener)
        addMouseMotionListener(mouseListener)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        if (isSolved) {
            g.color = AWTColor.WHITE
            g.fillRect(0, 0, width, height)
            for (row in 0 until field.size) {
                for (col in 0 until field.size) {
                    if (cells[row][col] == CellState.QUEEN) {
                        g.color = AWTColor.GREEN
                        g.fillOval(
                            col * CELL_SIZE + CELL_SIZE / 4,
                            row * CELL_SIZE + CELL_SIZE / 4,
                            CELL_SIZE / 2,
                            CELL_SIZE / 2
                        )
                    }
                }
            }
            return
        }

        // draw cell backgrounds
        field.colorRegions.forEach { (color, positions) ->
            positions.forEach { p ->
                g.color = toAWTColor(color)
                g.fillRect(p.col * CELL_SIZE, p.row * CELL_SIZE, CELL_SIZE, CELL_SIZE)
            }
        }

        // draw cells
        for (row in 0 until field.size) {
            for (col in 0 until field.size) {
                when (cells[row][col]) {
                    CellState.EMPTY -> Unit
                    CellState.CROSSED -> {
                        g.color = AWTColor.BLACK
                        g.drawLine(
                            col * CELL_SIZE + CELL_SIZE / 4,
                            row * CELL_SIZE + CELL_SIZE / 4,
                            (col + 1) * CELL_SIZE - CELL_SIZE / 4,
                            (row + 1) * CELL_SIZE - CELL_SIZE / 4
                        )
                        g.drawLine(
                            (col + 1) * CELL_SIZE - CELL_SIZE / 4,
                            row * CELL_SIZE + CELL_SIZE / 4,
                            col * CELL_SIZE + CELL_SIZE / 4,
                            (row + 1) * CELL_SIZE - CELL_SIZE / 4
                        )
                    }
                    CellState.QUEEN -> {
                        g.color = AWTColor.WHITE
                        g.fillOval(
                            col * CELL_SIZE + CELL_SIZE / 4,
                            row * CELL_SIZE + CELL_SIZE / 4,
                            CELL_SIZE / 2,
                            CELL_SIZE / 2
                        )
                    }
                }
            }
        }

        // draw grid
        for (i in 0 until field.size) {
            g.color = AWTColor.BLACK
            g.drawLine(0, i * CELL_SIZE, field.size * CELL_SIZE, i * CELL_SIZE)
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, field.size * CELL_SIZE)
        }
    }

    private fun toAWTColor(color: Color): AWTColor {
        return when (color) {
            Color.PURPLE -> AWTColor(223, 160, 192)
            Color.VIOLET -> AWTColor(188, 163, 226)
            Color.ORANGE -> AWTColor(255, 201, 146)
            Color.BLUE -> AWTColor(151, 190, 255)
            Color.GREEN -> AWTColor(179, 223, 160)
            Color.WHITE -> AWTColor(223, 223, 223)
            Color.RED -> AWTColor(255, 123, 96)
            Color.YELLOW -> AWTColor(230, 243, 137)
            Color.DARK_GRAY -> AWTColor(185, 178, 158)
            Color.LIGHT_BLUE -> AWTColor(163, 211, 216)
            Color.CYAN -> AWTColor(99, 240, 235)
        }
    }

    private fun checkSolution() {
        val actual = mutableSetOf<Position>()
        for (row in 0 until field.size) {
            for (col in 0 until field.size) {
                if (cells[row][col] == CellState.QUEEN) {
                    actual += Position(row, col)
                }
            }
        }
        if (actual == solution) {
            println("Solved!")
            isSolved = true
            repaint()
        }
    }

    private fun putCrossesForQueen(queenRow: Int, queenCol: Int) {
        @Suppress("KotlinConstantConditions")
        if (!IS_AUTO_CROSS_ENABLED) {
            return
        }
        val crosses = mutableSetOf<Position>()
        fun putCrossIfEmpty(row: Int, col: Int) {
            if (cells.getOrNull(row)?.getOrNull(col) == CellState.EMPTY) {
                cells[row][col] = CellState.CROSSED
                crosses += Position(row, col)
            }
        }

        val region = field.colorRegions.filterValues { it.contains(Position(queenRow, queenCol)) }.values.first()
        region.forEach { pos -> putCrossIfEmpty(pos.row, pos.col) }

        for (col in 0 until queenCol) putCrossIfEmpty(queenRow, col)
        for (col in (queenCol + 1) until field.size) putCrossIfEmpty(queenRow, col)
        for (row in 0 until queenRow) putCrossIfEmpty(row, queenCol)
        for (row in (queenRow + 1) until field.size) putCrossIfEmpty(row, queenCol)
        putCrossIfEmpty(queenRow - 1, queenCol - 1)
        putCrossIfEmpty(queenRow + 1, queenCol - 1)
        putCrossIfEmpty(queenRow - 1, queenCol + 1)
        putCrossIfEmpty(queenRow + 1, queenCol + 1)
        repaint()

        queenAutoCrosses[Position(queenRow, queenCol)] = crosses
    }

    private fun removeCrossesForQueen(queenRow: Int, queenCol: Int) {
        @Suppress("KotlinConstantConditions")
        if (!IS_AUTO_CROSS_ENABLED) {
            return
        }
        val crosses = queenAutoCrosses.getValue(Position(queenRow, queenCol))
        crosses.forEach { p ->
            cells[p.row][p.col] = CellState.EMPTY
        }
        queenAutoCrosses.remove(Position(queenRow, queenCol))
    }

    companion object {
        private const val CELL_SIZE = 48
        private const val IS_AUTO_CROSS_ENABLED = true

        fun show(field: Field, solution: Set<Position>) {
            FieldHistory.add(field)
            val frame = JFrame()
            frame.add(GameUI(field, solution))
            frame.pack()
            frame.isResizable = false
            frame.isVisible = true
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        }
    }

    private enum class CellState {
        EMPTY,
        CROSSED,
        QUEEN
    }
}