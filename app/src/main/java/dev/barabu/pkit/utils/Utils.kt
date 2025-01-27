package dev.barabu.pkit.utils

import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

fun clamp(x: Float, minVal: Float, maxVal: Float): Float {
    return min(max(x, minVal), maxVal)
}

fun mixPointF(a: PointF, b: PointF, t: Float): PointF = PointF(
    a.x * (1f - t) + b.x * t,
    a.y * (1f - t) + b.y * t
)

fun mixFloat(a: Float, b: Float, t: Float): Float = (1f - t) * a + t * b

fun rotate2d(angle: Float, pointToRotate: FloatArray, pivot: FloatArray, rotatedPoint: FloatArray) {
    val x = pointToRotate[0] - pivot[0]
    val y = pointToRotate[1] - pivot[1]

    val c = cos(angle)
    val s = sin(angle)

    val x1 = x * c - y * s
    val y1 = x * s + y * c

    rotatedPoint[0] = x1 + pivot[0]
    rotatedPoint[1] = y1 + pivot[1]
}
