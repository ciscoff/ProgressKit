package dev.barabu.pkit.ui.kit.citrix_launcher

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

private fun setupClipPath(size: Size): Path {

    val ovalSizeRatio = 0.35f
    val baseDim = min(size.width, size.height)
    val strokeDim = baseDim * 0.05f

    val innerPadding = baseDim * ovalSizeRatio
    val innerSize = Size(baseDim - innerPadding * 2f, baseDim - innerPadding * 2f)

    val innerCircle = Path().apply {
        addOval(Rect(offset = Offset(innerPadding, innerPadding), size = innerSize))
    }

    val outerCircle = Path().apply {
        addOval(Rect(Offset.Zero, size))
    }

    val hLine = Path().apply {
        addRect(
            Rect(
                offset = Offset(0f, baseDim / 2 - strokeDim / 2),
                size = Size(baseDim, strokeDim)
            )
        )
    }

    val vLine = Path().apply {
        addRect(
            Rect(
                offset = Offset(baseDim / 2 - strokeDim / 2, 0f),
                size = Size(strokeDim, baseDim)
            )
        )
    }

    return outerCircle.minus(innerCircle).minus(hLine).minus(vLine)
}

private fun provideClipShape(): Shape {

    return object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline = Outline.Generic(setupClipPath(size))
    }
}

@Composable
fun CitrixLauncher(
    boxSize: Dp,
    tint: Color = Color.White,
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val quadrant by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "quadrant"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier
            .size(boxSize)
            .graphicsLayer {
                clip = true
                shape = provideClipShape()
            }
            .drawBehind {
                // background
                drawCircle(color = tint.copy(alpha = 0.3f))

                // progress
                drawArc(
                    color = tint,
                    startAngle = 90f * (quadrant - 1),
                    sweepAngle = 90f,
                    useCenter = true
                )
            }
        )
    }
}