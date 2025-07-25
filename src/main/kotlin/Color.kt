package com.dpforge.easyraster

enum class Color(val value: Char) {
    PURPLE('p'),
    ORANGE('o'),
    BLUE('b'),
    GREEN('g'),
    WHITE('w'),
    RED('r'),
    YELLOW('y'),
    DARK_GRAY('d');

    companion object {
        fun decode(value: Char): Color = entries.find { it.value == value } ?: error("Unknown color: $value")
    }
}