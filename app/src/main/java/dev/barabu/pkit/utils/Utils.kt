package dev.barabu.pkit.utils

import android.graphics.PointF
import kotlin.math.max
import kotlin.math.min

fun clamp(x: Float, minVal: Float, maxVal: Float): Float {
    return min(max(x, minVal), maxVal)
}

fun mixPointF(a: PointF, b: PointF, t: Float): PointF = PointF(
    a.x * (1f - t) + b.x * t,
    a.y * (1f - t) + b.y * t
)

fun mixFloat(a: Float, b: Float, t: Float): Float = (1f - t) * a + t * b