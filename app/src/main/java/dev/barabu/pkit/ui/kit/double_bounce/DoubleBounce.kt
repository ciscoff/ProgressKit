package dev.barabu.pkit.ui.kit.double_bounce

import android.graphics.PointF
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.barabu.pkit.utils.bezier.CubicBezierCompatible
import kotlin.math.min

@Composable
fun DoubleBounce(
    boxSize: Dp,
    tintColor: Color,
    modifier: Modifier = Modifier
) {

    val easeInEaseOut = remember {
        CubicBezierCompatible(
            b = PointF(0.3f, 0f),
            c = PointF(0.7f, 1f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = easeInEaseOut),
            repeatMode = RepeatMode.Reverse
        ), label = "fraction"
    )

    val dotColor = remember { tintColor.copy(alpha = 0.6f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Spacer(modifier = Modifier
            .size(boxSize)
            .drawBehind {
                drawCircle(
                    color = dotColor,
                    radius = (min(size.width, size.height) / 2f) * fraction
                )
            }
        )

        Spacer(modifier = Modifier
            .size(boxSize)
            .drawBehind {
                drawCircle(
                    color = dotColor,
                    radius = (min(size.width, size.height) / 2f) * (1f - fraction)
                )
            }
        )
    }
}