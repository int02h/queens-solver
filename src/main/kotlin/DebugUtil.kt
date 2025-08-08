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
            Color.PURPLE -> AWTColor(128, 0, 128)
            Color.VIOLET -> AWTColor(238, 130, 238)
            Color.ORANGE -> AWTColor.ORANGE
            Color.BLUE -> AWTColor.BLUE
            Color.GREEN -> AWTColor.GREEN
            Color.WHITE -> AWTColor.LIGHT_GRAY
            Color.RED -> AWTColor.RED
            Color.YELLOW -> AWTColor.YELLOW
            Color.DARK_GRAY -> AWTColor.DARK_GRAY
            Color.LIGHT_BLUE -> AWTColor(0x87, 0xce, 0xfa)
        }
    }
}