package dev.barabu.pkit.ui.kit.chasing_dots

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ChasingDots(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val twoPi = (2 * PI).toFloat()
    val phaseShift = twoPi / 3
    val offX = 0.7f
    val offY = 1.2f

    val angles = remember {
        arrayOf(-phaseShift, 0f, phaseShift)
    }

    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "1"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Canvas(modifier = Modifier.size(boxSize)) {
            val radius = min(size.width, size.height) / 4

            angles.forEach { angleOffset ->
                val angle = fraction * twoPi + angleOffset
                val scale = max(0f, sin(angle))

                if (scale > 0.0001f) {
                    val offsetHor = radius * offX
                    val circleCenter =
                        center + Offset(cos(angle) * offsetHor, scale * radius * offY)

                    drawCircle(
                        color = tintColor,
                        radius = radius * scale,
                        center = circleCenter,
                    )
                }
            }
        }
    }
}