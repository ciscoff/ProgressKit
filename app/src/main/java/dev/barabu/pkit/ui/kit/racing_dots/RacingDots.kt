package dev.barabu.pkit.ui.kit.racing_dots

import android.graphics.PointF
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import dev.barabu.pkit.utils.bezier.CubicBezierCompat
import dev.barabu.pkit.utils.mixFloat
import dev.barabu.pkit.utils.mixPointF

private fun getCenteredEasing(qty: Int): Array<Easing> {

    val bStart = PointF(0.0f, 1.0f)
    val bEnd = PointF(0.5f, 0.0f)

    val cStart = PointF(1.0f, 0.0f)
    val cEnd = PointF(0.5f, 1.0f)

    return Array(qty) { i ->
        val t = (i + 1) / qty.toFloat()
        CubicBezierCompat(mixPointF(bStart, bEnd, t), mixPointF(cStart, cEnd, t))
    }
}

@Composable
private fun InfiniteTransition.getAnimations(
    durationMillis: Int,
    easing: Array<Easing>
): List<State<Float>> = easing.mapIndexed { i, e ->
    animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = e),
            repeatMode = RepeatMode.Restart
        ), label = "$i"
    )
}

@Composable
fun RacingDots(
    tintColor: Color,
    boxSize: Dp,
    numDots: Int = 6,
    modifier: Modifier = Modifier
) {

    val durationMillis = 3600

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val density = LocalDensity.current
    val dotRadius = remember { with(density) { boxSize.toPx() * 0.06f } }

    val easing = remember { getCenteredEasing(qty = numDots) }

    val animations = infiniteTransition.getAnimations(durationMillis, easing)

    val zRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween((durationMillis * 2) / 3, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "zRotation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .size(boxSize)
                .graphicsLayer {
                    rotationZ = zRotation
                }
        ) {
            val dotCenter = Offset(center.x + dotRadius * 4, center.y)

            repeat(numDots) { i ->
                val fraction = animations[i].value
                val j = numDots - 1 - i // inverted index

                val cs: Float
                val ts: Float
                val scaleFactor: Float
                val dots = numDots.toFloat()

                if (fraction <= 0.5f) { // first half circle
                    cs = (i + 1) / dots // current scale
                    ts = (j + 1) / dots // target scale
                    scaleFactor = mixFloat(cs, ts, fraction * 2)
                } else { // second half circle
                    cs = (j + 1) / dots
                    ts = (i + 1) / dots
                    scaleFactor = mixFloat(cs, ts, fraction - 0.5f)
                }

                rotate(360f * 3f * fraction) {
                    drawCircle(
                        color = tintColor,
                        radius = dotRadius * scaleFactor,
                        center = dotCenter,
                    )
                }
            }
        }
    }
}