package dev.barabu.pkit.ui.kit.rotating_plane

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

@Composable
fun RotatingPlane(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Spacer(modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                if (angle <= 180f) {
                    rotationY = angle
                } else {
                    rotationX = -angle
                }
            }
            .drawWithCache {
                onDrawBehind {
                    drawRect(
                        color = tintColor,
                        size = size
                    )
                }
            }
        )
    }
}