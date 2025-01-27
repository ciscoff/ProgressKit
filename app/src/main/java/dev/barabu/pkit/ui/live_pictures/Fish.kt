package dev.barabu.pkit.ui.live_pictures

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

private const val TOTAL_STRIPES = 33
private const val HEAD_STRIPES = 9

val bodyBColors = listOf(
    Color(0xFF607D8B), // top
    Color.Unspecified,
    Color(0xFF2196F3),
    Color(0xFFE64A19), // bottom
)

val headColors = listOf(
    Color(0xFF607D8B), // top
    Color(0xFFE64A19),
    Color(0xAA2196F3),
    Color(0xAA607D8B), // bottom
)

val tailColors = listOf(
    Color(0xFF607D8B),
    Color(0xFFE64A19),
    Color(0xFF2196F3),
)
val finsBrush = listOf(
    Color(0xFF607D8B),
    Color(0xFFE64A19),
)

@Composable
private fun Fins(
    stripeSize: DpSize,
    angle: Float,
    phaseShift: Float,
    fishAnatomy: FishAnatomy,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        repeat(TOTAL_STRIPES) { i ->
            Spacer(Modifier
                .size(stripeSize)
                .graphicsLayer {
                    shape = fishAnatomy.provideFinsOnlyShape(i)
                    clip = true

                    transformOrigin = TransformOrigin(pivotFractionX = 4f, pivotFractionY = 0.1f)
                    rotationY = 30f * (sin(angle + i * phaseShift))
                }
                .drawWithCache {
                    onDrawBehind {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = finsBrush,
                            )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun Head(
    headSize: DpSize,
    angle: Float,
    phaseShift: Float,
    fishAnatomy: FishAnatomy,
) {

    Spacer(Modifier
        .size(headSize)
        .graphicsLayer {
            shape = fishAnatomy.provideHeadShape(mouthFraction = sin(angle * 0.5f))
            clip = true

            transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0.1f)
            rotationY = 10f * (sin(angle + fishAnatomy.bodyStripes * phaseShift))
        }
        .drawWithCache {
            onDrawBehind { drawRect(brush = Brush.linearGradient(colors = headColors)) }
        }
    )
}

@Composable
fun Body(
    stripeSize: DpSize,
    angle: Float,
    phaseShift: Float,
    fishAnatomy: FishAnatomy,
) {

    repeat(fishAnatomy.bodyStripes) { i ->
        Spacer(Modifier
            .size(stripeSize)
            .graphicsLayer {
                shape = fishAnatomy.provideHeadlessBodyShape(i)
                clip = true

                transformOrigin = TransformOrigin(pivotFractionX = 4f, pivotFractionY = 0.1f)
                rotationY = 30f * (sin(angle + i * phaseShift))
            }
            .drawWithCache {

                onDrawBehind {
                    drawRect(

                        brush = when {
                            i < 7 -> Brush.sweepGradient(
                                colors = tailColors,
                                center = Offset(size.width * 2, size.height / 2)
                            )

                            i % 2 == 0 -> Brush.sweepGradient(
                                colors = bodyBColors,
                                center = Offset(-size.width * 2, size.height / 2)
                            )

                            else -> SolidColor(Color.Unspecified)
                        }
                    )
                }
            }
        )
    }
}

@Composable
fun Fish(
    boxHeight: Dp = 180.dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val twoPi = remember { (2 * PI).toFloat() }
    val phaseShift: Float = remember { twoPi / (TOTAL_STRIPES * 4) }

    val stripeHeight: Dp = boxHeight
    val stripeWidth: Dp = boxHeight * .022f
    val stripeSize = remember { DpSize(stripeWidth, stripeHeight) }

    val headSize = remember { DpSize(stripeWidth * HEAD_STRIPES, stripeHeight) }

    val fishAnatomy = remember {
        FishAnatomy(
            totalStripes = TOTAL_STRIPES,
            headStripes = HEAD_STRIPES,
            mouthOffset = with(density) {
                Offset(headSize.width.toPx() * 0.05f, headSize.height.toPx() * 0.05f)
            },
            eyeOffset = with(density) {
                Offset(0f, headSize.height.toPx() * 0.045f)
            }
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Body(stripeSize, angle, phaseShift, fishAnatomy)
            Head(headSize, angle, phaseShift, fishAnatomy)
        }

        Fins(stripeSize, angle, phaseShift, fishAnatomy)
    }
}