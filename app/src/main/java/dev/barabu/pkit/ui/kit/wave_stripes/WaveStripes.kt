package dev.barabu.pkit.ui.kit.wave_stripes

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpSize
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaveStripes(
    tintColor: Color,
    stripSizeDp: DpSize,
    strips: Int = 5,
    modifier: Modifier = Modifier
) {

    val twoPi = (2 * PI).toFloat()
    val phaseShift = twoPi / (strips * 4)

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Row(horizontalArrangement = Arrangement.Center) {
            repeat(strips) { i ->
                Spacer(modifier = Modifier
                    .size(width = stripSizeDp.width, height = stripSizeDp.height)
                    .graphicsLayer {
                        val scale = 1f + sin(angle.value + i * phaseShift) * 2f
                        scaleY = scale
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
                if (i < (strips - 1)) {
                    Spacer(Modifier.size(stripSizeDp.width / 2))
                }
            }
        }
    }
}