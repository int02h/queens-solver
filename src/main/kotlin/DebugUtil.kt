package com.dpforge.easyraster

import java.awt.image.BufferedImage
import java.awt.Color as AWTColor

@Suppress("unused")
object DebugUtil {

    private const val CELL_SIZE = 16

    fun drawField(field: Field): BufferedImage {
        val img = BufferedImage(field.size * CELL_SIZE, field.size * CELL_SIZE, BufferedImage.TYPE_INT_RGB)
        val g = img.createGraphics()

        // draw cell backgrounds
        field.colorRegions.forEach { (color, positions) ->
            positions.forEach { p ->
                g.color = toAWTColor(color)
                g.fillRect(p.col * CELL_SIZE, p.row * CELL_SIZE, CELL_SIZE, CELL_SIZE)
            }
        }

        // draw grid
        for (i in 0 until field.size) {
            g.color = AWTColor.BLACK
            g.drawLine(0, i * CELL_SIZE, field.size * CELL_SIZE, i * CELL_SIZE)
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, field.size * CELL_SIZE)
        }
        g.dispose()
        return img
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
}