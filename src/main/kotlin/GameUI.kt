package com.dpforge.easyraster

import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Color as AWTColor

class GameUI(
    private val field: Field,
    private val solution: Set<Position>
) : JPanel() {

    private val cells = Array(field.size) { Array(field.size) { CellState.EMPTY } }

    init {
        preferredSize = Dimension(field.size * CELL_SIZE, field.size * CELL_SIZE)

        val mouseListener = object : MouseAdapter() {
            private var pressRow = -1
            private var pressCol = -1
            private var lastRow = -1
            private var lastCol = -1

            override fun mousePressed(e: MouseEvent) {
                pressRow = e.y / CELL_SIZE
                lastRow = pressRow
                pressCol = e.x / CELL_SIZE
                lastCol = pressCol

                cells[pressRow][pressCol] = when (cells[pressRow][pressCol]) {
                    CellState.EMPTY -> CellState.CROSSED
                    CellState.CROSSED -> CellState.QUEEN
                    CellState.QUEEN -> CellState.EMPTY
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
                        g.drawLine(col * CELL_SIZE, row * CELL_SIZE, (col + 1) * CELL_SIZE, (row + 1) * CELL_SIZE)
                        g.drawLine((col + 1) * CELL_SIZE, row * CELL_SIZE, col * CELL_SIZE, (row + 1) * CELL_SIZE)
                    }
                    CellState.QUEEN -> {
                        g.color = AWTColor.WHITE
                        g.fillOval(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE)
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
            Color.PURPLE -> AWTColor(128, 0, 128)
            Color.VIOLET -> AWTColor(238, 130, 238)
            Color.ORANGE -> AWTColor.ORANGE
            Color.BLUE -> AWTColor.BLUE
            Color.GREEN -> AWTColor.GREEN
            Color.WHITE -> AWTColor.LIGHT_GRAY
            Color.RED -> AWTColor.RED
            Color.YELLOW -> AWTColor.YELLOW
            Color.DARK_GRAY -> AWTColor.DARK_GRAY
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
        }
    }

    companion object {
        private const val CELL_SIZE = 64

        fun show(field: Field, solution: Set<Position>) {
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