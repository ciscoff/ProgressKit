package dev.barabu.pkit.ui.kit.running_beads

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun RunningBeads(
    tintColor: Color,
    dotSize: Dp = 10.dp,
    numDots: Int = 20,
    modifier: Modifier = Modifier
) {

    val twoPi = (2 * PI).toFloat()

    val phaseShift = twoPi / (numDots * 2)

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Row(horizontalArrangement = Arrangement.Center) {
            repeat(numDots) { i ->
                Spacer(modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        val scale = sin(angle - i * phaseShift) * 0.5f + 0.5f
                        scaleY = scale
                        scaleX = scale
                    }
                    .drawWithCache {
                        onDrawBehind {
                            drawCircle(color = tintColor, radius = (dotSize / 2).toPx())
                        }
                    }
                )
                if (i < (numDots - 1)) {
                    Spacer(Modifier.size(dotSize / 4))
                }
            }
        }
    }
}