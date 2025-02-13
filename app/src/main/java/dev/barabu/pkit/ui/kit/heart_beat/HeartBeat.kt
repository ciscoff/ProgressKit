package dev.barabu.pkit.ui.kit.heart_beat

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import dev.barabu.pkit.utils.bezier.CubicBezierCompat

private fun setupPath(size: Size, points: List<PointF>): Path = Path().apply {
    moveTo(points[0].x * size.width, points[0].y * size.height)
    repeat(2) { i ->
        val j = i * 3 + 1
        cubicTo(
            points[j].x * size.width, points[j].y * size.height,
            points[j + 1].x * size.width, points[j + 1].y * size.height,
            points[j + 2].x * size.width, points[j + 2].y * size.height,
        )
    }
    close()
}

private fun provideShape(path: Path): Shape {
    return object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Generic(path)
    }
}

@Composable
fun HeartBeat(
    boxSize: Dp,
    tint: Color = Color.Red,
    modifier: Modifier = Modifier
) {

    val heartPoints = remember {
        listOf(
            PointF(.5f, .2f),
            PointF(0f, 0f),
            PointF(0f, .7f),
            PointF(.5f, .9f),
            PointF(1f, .7f),
            PointF(1f, 0f),
            PointF(.5f, .2f),
        )
    }

    val circlePoints = remember {
        listOf(
            PointF(.5f, .2f),
            PointF(.1f, .2f),
            PointF(.1f, .8f),
            PointF(.5f, .8f),
            PointF(.9f, .8f),
            PointF(.9f, .2f),
            PointF(.5f, .2f),
        )
    }

    val easeInEaseOut = remember {
        CubicBezierCompat(
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
            repeatMode = RepeatMode.Restart
        ), label = "fraction"
    )

    val density = LocalDensity.current

    val heartPath = remember {
        setupPath(with(density) { Size(boxSize.toPx(), boxSize.toPx()) }, heartPoints)
    }

    val circlePath = remember {
        setupPath(with(density) { Size(boxSize.toPx(), boxSize.toPx()) }, circlePoints)
    }

    val circleColor = remember { tint.copy(alpha = 0.5f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                clip = true
                shape = provideShape(heartPath)
                alpha = 1f - fraction
                scaleY = fraction
                scaleX = fraction
            }
            .drawWithCache {
                onDrawBehind {
                    drawRect(color = tint)
                }
            }
        )

        Spacer(modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                clip = true
                shape = provideShape(circlePath)
                alpha = fraction
                scaleY = 1f - fraction
                scaleX = 1f - fraction
            }
            .drawWithCache {
                onDrawBehind {
                    drawRect(color = circleColor)
                }
            }
        )
    }
}