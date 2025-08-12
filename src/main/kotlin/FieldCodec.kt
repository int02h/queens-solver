package com.dpforge.easyraster

import kotlin.math.sqrt

object FieldCodec {

    fun encodeToString(field: Field): String {
        var encoded = ""
        for (row in 0 until field.size) {
            for (col in 0 until field.size) {
                val color = field.colorRegions.getColor(Position(row, col)) ?: error("Bad field")
                encoded += color.value
            }
        }
        return encoded
    }

    fun encodeToCompressedString(field: Field): String {
        // space is a terminal character to let the compressing algorithm finish
        val encoded = encodeToString(field) + " "
        val chars = encoded.toMutableList()
        var currentChar = ' '
        var charCount = 0
        var result = ""

        while (chars.isNotEmpty()) {
            val ch = chars.removeFirst()
            if (ch != currentChar) {
                if (currentChar != ' ') {
                    result += if (charCount > 1) {
                        "$charCount$currentChar"
                    } else {
                        currentChar
                    }
                }
                currentChar = ch
                charCount = 1
            } else {
                charCount++
            }
        }

        return result
    }

    fun encodeToHumanText(field: Field): List<String> {
        val encoded = encodeToString(field)
        return encoded.chunked(field.size)
    }

    fun decodeFromString(encoded: String): Field {
        val colorRegions = mutableMapOf<Color, MutableSet<Position>>()
        val fieldSize = getStringEncodedFieldSize(encoded)
        encoded.forEachIndexed { index, ch ->
            val pos = Position(index / fieldSize, index % fieldSize)
            val color = Color.decode(ch)
            colorRegions.getOrPut(color) { mutableSetOf() } += pos
        }

        // the field must be square
        val totalCells = fieldSize * fieldSize
        if (colorRegions.values.sumOf { it.size } != totalCells) {
            error("Wrongly encoded field")
        }

        return Field(
            size = fieldSize,
            colorRegions = colorRegions
        )
    }

    fun decodeFromCompressedString(compressed: String): Field {
        var encoded = ""
        var i = 0
        var countRaw = ""
        while (i < compressed.length) {
            if (compressed[i].isDigit()) {
                countRaw += compressed[i]
            } else {
                val count = if (countRaw == "") 1 else countRaw.toInt()
                repeat(count) {
                    encoded += compressed[i]
                }
                countRaw = ""
            }
            i++
        }
        return decodeFromString(encoded)
    }

    fun decodeFromHumanText(text: List<String>): Field {
        return decodeFromString(text.joinToString(separator = ""))
    }

    private fun getStringEncodedFieldSize(encoded: String): Int {
        val fieldSize = sqrt(encoded.length.toDouble()).toInt()
        if (fieldSize * fieldSize != encoded.length) {
            error("Wrongly encoded field")
        }
        return fieldSize
    }

    private fun Map<Color, Set<Position>>.getColor(pos: Position): Color? {
        for ((color, posSet) in this) {
            if (posSet.contains(pos)) {
                return color
            }
        }
        return null
    }
}

