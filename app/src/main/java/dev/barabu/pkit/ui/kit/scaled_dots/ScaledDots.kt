package dev.barabu.pkit.ui.kit.scaled_dots

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.min


private const val NUM_DOTS = 12

@Composable
fun ScaledDots(
    tintColor: Color,
    boxSize: Dp,
    numDots: Int = NUM_DOTS,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val rayAngle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rayAngle"
    )

    val density = LocalDensity.current
    val dotRadius = remember { with(density) { boxSize.toPx() * 0.05f } }
    val segment = remember { 360f / numDots }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier
            .size(boxSize)
            .drawBehind {
                val dotCenter = Offset(size.width - dotRadius * 5, center.y)

                repeat(NUM_DOTS) { i ->
                    val dotAngle = i * segment// 0f, 30f, 60f, ...

                    rotate(dotAngle) {
                        var diffAngle = abs(rayAngle.value - dotAngle)
                        diffAngle = min(360f - diffAngle, diffAngle)

                        val fraction = 1f - diffAngle / 180f

                        drawCircle(
                            color = tintColor,
                            radius = dotRadius * fraction,
                            center = dotCenter,
                            alpha = fraction
                        )
                    }
                }
            }
        )
    }
}