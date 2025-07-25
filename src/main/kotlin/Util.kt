package com.dpforge.easyraster

fun <T> List<T>.allUnorderedPairs(): Sequence<Pair<T, T>> = sequence {
    val list = this@allUnorderedPairs
    for (i in 0 until size) {
        for (j in i + 1 until size) {
            yield(list[i] to list[j])
        }
    }
}
