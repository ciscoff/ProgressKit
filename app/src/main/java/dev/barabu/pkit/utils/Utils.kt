package dev.barabu.pkit.utils

import kotlin.math.max
import kotlin.math.min

fun clamp(x: Float, minVal: Float, maxVal: Float): Float {
    return min(max(x, minVal), maxVal)
}