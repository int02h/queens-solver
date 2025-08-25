package com.dpforge.easyraster

data class Field(
    val size: Int,
    val colorRegions: Map<Color, Set<Position>>
)
