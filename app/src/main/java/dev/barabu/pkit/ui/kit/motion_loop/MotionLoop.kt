package dev.barabu.pkit.ui.kit.motion_loop

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun MotionLoop(
    boxSize: Dp,
    tint: Color,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fraction"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Canvas(
            modifier = Modifier.size(boxSize)
        ) {
            val radius = size.width / 10f
            val xOff = size.width / 6f

            drawCircle(
                color = tint,
                radius = radius * fraction,
                center = Offset(xOff, size.height * .5f),
            )

            drawCircle(
                color = tint,
                radius = radius,
                center = Offset(xOff + 2 * xOff * fraction, size.height * .5f),
            )

            drawCircle(
                color = tint,
                radius = radius,
                center = Offset(xOff * 3 + 2 * xOff * fraction, size.height * .5f),
            )

            drawCircle(
                color = tint,
                radius = radius * (1f - fraction),
                center = Offset(xOff * 5, size.height * .5f),
            )
        }
    }
}