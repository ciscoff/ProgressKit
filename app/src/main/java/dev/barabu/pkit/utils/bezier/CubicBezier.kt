package dev.barabu.pkit.utils.bezier

import android.graphics.PointF
import androidx.compose.animation.core.Easing

data class CubicBezier(val a: PointF, val b: PointF, val c: PointF, val d: PointF) : Easing {

    override fun transform(fraction: Float): Float {

        val t = fraction
        val t2 = t * t
        val t3 = t2 * t

        val mt = 1 - t
        val mt2 = mt * mt
        val mt3 = mt2 * mt

        return a.y * mt3 + 3 * b.y * mt2 * t + 3 * c.y * t2 * mt + d.y * t3
    }
}

/**
 * Quick case with fist and last control points (0,0) and (1,1)
 */
data class CubicBezierCompatible(val b: PointF, val c: PointF) : Easing {

    override fun transform(fraction: Float): Float {
        val t = fraction
        val t2 = t * t
        val t3 = t2 * t

        val mt = 1 - t
        val mt2 = mt * mt

        return 3 * b.y * mt2 * t + 3 * c.y * t2 * mt + t3
    }
}