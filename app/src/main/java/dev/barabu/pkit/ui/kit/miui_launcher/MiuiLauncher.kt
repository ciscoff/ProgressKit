package dev.barabu.pkit.ui.kit.miui_launcher

import android.graphics.PointF
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

private const val BASE_RATIO = 0.45f
private const val DIFF_RATIO = 1f - BASE_RATIO

/**
 * ref:
 * https://www.desmos.com/calculator/7hyfifl0yx
 */
private fun headY(x: Float): Float = x * x * x // {0 <= x <= 6}
private fun tailY(x: Float): Float = 2f - x // {0 <= x <= 6}
private fun scaleY(x: Float): Float = max(min(headY(x), tailY(x)), 0f)

private fun headX(x: Float): Float = (x - 3) * (x - 3) * (x - 3) // {0 <= x <= 6}
private fun tailX(x: Float): Float = 5f - x // {0 <= x <= 6}
private fun scaleX(x: Float): Float = max(min(headX(x), tailX(x)), 0f)

/**
 * steps graph: https://www.desmos.com/calculator/7hyfifl0yx
 */
private fun calculateScale(fraction: Float, step: Int, scales: FloatArray) {
    val scaleX = BASE_RATIO
    val scaleY = BASE_RATIO

    when (step) {
        0, 1 -> {
            scales[0] = scaleX
            scales[1] = scaleY + DIFF_RATIO * scaleY(fraction.mod(6f))
        }

        3, 4 -> {
            scales[0] = scaleX + DIFF_RATIO * scaleX(fraction.mod(6f))
            scales[1] = scaleY
        }

        2, 5 -> {
            scales[0] = scaleX
            scales[1] = scaleY
        }
    }
}

@Composable
fun MiuiLauncher(
    tileSize: Dp,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fraction"
    )

    val boxSize = remember { tileSize * 2.5f }

    val scales = remember { FloatArray(2) { 1f } }

    val density = LocalDensity.current
    val cornerRadius = remember {
        with(density) {
            val v = tileSize.toPx() * 0.25f
            CornerRadius(v, v)
        }
    }

    val pivots = remember {
        arrayOf(
            /* 0*/ PointF(0f, 1f), // push up
            /* 1*/ PointF(0f, 0f), // pull up
            /* 2*/ PointF(0f, 0f), // pause
            /* 3*/ PointF(0f, 0f), // push right
            /* 4*/ PointF(1f, 0f), // pull right
            /* 5*/ PointF(1f, 0f), // pause
            /* 6*/ PointF(1f, 0f), // push down
            /* 7*/ PointF(1f, 1f), // pull down
            /* 8*/ PointF(1f, 1f), // pause
            /* 9*/ PointF(1f, 1f), // push left
            /*10*/ PointF(0f, 1f), // pull left
            /*11*/ PointF(0f, 1f), // pause
        ).map { TransformOrigin(pivotFractionX = it.x, pivotFractionY = it.y) }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        repeat(3) { i ->
            Spacer(
                Modifier
                    .size(boxSize)
                    .graphicsLayer {
                        val indexAwareFraction = fraction + 4 * i
                        val step = floor(indexAwareFraction).toInt().mod(pivots.size)

                        transformOrigin = pivots[step]
                        calculateScale(indexAwareFraction, step.mod(6), scales)

                        scaleX = scales[0]
                        scaleY = scales[1]

                    }
                    .drawWithCache {
                        onDrawBehind {
                            drawRoundRect(
                                color = color,
                                cornerRadius = cornerRadius
                            )
                        }
                    }
            )
        }
    }
}