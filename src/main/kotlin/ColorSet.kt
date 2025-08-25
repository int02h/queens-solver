package com.dpforge.easyraster

class ColorSet : Iterable<Color> {
    private val data = BooleanArray(Color.entries.size)
    var size = 0
        private set

    fun add(color: Color) {
        if (!data[color.ordinal]) {
            data[color.ordinal] = true
            size++
        }
    }

    fun add(set: ColorSet) {
        for (i in 0 until set.data.size) {
            if (set.data[i]) {
                if (!data[i]) {
                    data[i] = true
                    size++
                }
            }
        }
    }

    fun contains(color: Color): Boolean {
        return data[color.ordinal]
    }

    fun clear() {
        for (i in 0 until data.size) {
            data[i] = false
        }
        size = 0
    }

    override fun iterator(): Iterator<Color> = object : Iterator<Color> {

        private var leftCount = size
        private var index = 0

        override fun next(): Color {
            while (!data[index]) {
                index++
            }
            leftCount--
            return Color.entries[index++]
        }

        override fun hasNext(): Boolean = leftCount > 0

    }
}