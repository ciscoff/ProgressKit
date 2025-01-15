package dev.barabu.pkit.ui.kit.round_ripple

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import kotlin.math.min

@Composable
fun RoundRipple(
    boxSize: Dp,
    tintColor: Color,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fraction"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                scaleY = fraction
                scaleX = fraction
                alpha = 1f - fraction
            }
            .drawWithCache {
                onDrawBehind {
                    drawCircle(
                        color = tintColor,
                        radius = (min(size.width, size.height) / 2f)
                    )
                }
            }
        )
    }
}