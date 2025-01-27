package dev.barabu.pkit.ui.live_pictures

import android.graphics.PointF
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import dev.barabu.pkit.utils.mixFloat
import dev.barabu.pkit.utils.rotate2d
import kotlin.math.PI
import kotlin.random.Random

private data class Bubble(
    val startPosition: Offset,
    val endPosition: Offset,
    val startDelay: Int,
    val duration: Int,
    val radius: Float
) {

    fun mixPosition(size: Size, fraction: Float): Offset = Offset(
        mixFloat(startPosition.x, endPosition.x, fraction) * size.width,
        mixFloat(startPosition.y, endPosition.y, fraction) * size.height + radius
    )
}

private class Water(
    private val widthOffsetRatio: Float = 0.5f,
    private val waterLevel: Float = 0.5f
) {

    private val controlPoints = Array(4) { PointF(0f, 0f) }

    private val twoPi = (PI * 2).toFloat()
    private val pivot = FloatArray(2) { 0f }
    private val pointToRotate1 = FloatArray(2) { 0f }
    private val pointToRotate2 = FloatArray(2) { 0f }
    private val rotatedPoint1 = FloatArray(2) { 0f }
    private val rotatedPoint2 = FloatArray(2) { 0f }

    fun populateControlPoints(width: Float, height: Float) {
        val baseY = height * (1f - waterLevel)

        controlPoints[0] = PointF(0f, baseY)
        controlPoints[1] = PointF(width * widthOffsetRatio, baseY)
        controlPoints[2] = PointF(width - width * widthOffsetRatio, baseY)
        controlPoints[3] = PointF(width, baseY)
    }

    fun provideFillShape(fraction: Float): Shape {
        return object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline =
                Outline.Generic(createClipPath(size.width, size.height, fraction))
        }
    }

    private fun createClipPath(width: Float, height: Float, fraction: Float): Path {

        val angle = twoPi * fraction

        pointToRotate1[0] = controlPoints[1].x
        pointToRotate1[1] = controlPoints[1].y
        pointToRotate2[0] = controlPoints[2].x
        pointToRotate2[1] = controlPoints[2].y

        pivot[0] = width * (widthOffsetRatio + 0.08f)
        pivot[1] = height * (1f - waterLevel)

        rotate2d(
            angle = angle,
            pointToRotate = pointToRotate1,
            pivot = pivot,
            rotatedPoint = rotatedPoint1
        )

        pivot[0] = width - width * (widthOffsetRatio + 0.04f)
        pivot[1] = height * (1f - waterLevel)

        rotate2d(
            angle = angle,
            pointToRotate = pointToRotate2,
            pivot = pivot,
            rotatedPoint = rotatedPoint2
        )

        return Path().apply {
            moveTo(controlPoints[0].x, controlPoints[0].y)
            cubicTo(
                rotatedPoint1[0], rotatedPoint1[1],
                rotatedPoint2[0], rotatedPoint2[1],
                controlPoints[3].x, controlPoints[3].y,
            )
            lineTo(width, height)
            lineTo(0f, height)
            lineTo(controlPoints[0].x, controlPoints[0].y)
        }
    }
}

private fun Modifier.fillTheWater(
    water: Water
) = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    water.populateControlPoints(placeable.width.toFloat(), placeable.height.toFloat())

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(IntOffset.Zero)
    }
}

@Composable
fun Aquarium(
    waterColor: Color = Color(0xFF78B3CE),
    bubblesColor: Color = Color(0xFFC9E6F0),
    waterLevel: Float = 0.6f,
    numberBubbles: Int = 6,
    modifier: Modifier,
    fish: @Composable () -> Unit
) {

    val density = LocalDensity.current

    val water = remember { Water(widthOffsetRatio = 0.25f, waterLevel = waterLevel) }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "water"
    )

    val bubbles = remember(numberBubbles) {
        List(numberBubbles) { i ->

            val startPos = Offset(
                x = Random.nextFloat(),
                y = 1f
            )

            val endPos = Offset(
                x = Random.nextFloat(),
                y = 0f
            )

            val duration = Random.nextInt(10000, 20000)
            val radius = with(density) { (Random.nextFloat() * 10.dp + 8.dp).toPx() }

            Bubble(
                startPosition = startPos,
                endPosition = endPos,
                startDelay = Random.nextInt(1000, 20000),
                duration = duration,
                radius = radius
            )
        }
    }

    Box(modifier = modifier
        .fillTheWater(
            water
        )
        .graphicsLayer {
            clip = true
            shape = water.provideFillShape(fraction)
        }
        .drawBehind {
            drawRect(color = waterColor)
        }
    ) {

        for (bubble in bubbles) {

            val bubbleFraction by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                infiniteRepeatable(
                    animation = tween(
                        durationMillis = bubble.duration,
                        easing = EaseInOut,
                        delayMillis = bubble.startDelay
                    ),
                    repeatMode = RepeatMode.Restart
                ), label = "bubble"
            )

            Spacer(modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = bubblesColor.copy(alpha = (waterLevel - bubbleFraction)),
                        radius = bubble.radius,
                        center = bubble.mixPosition(size, bubbleFraction)
                    )
                }
            )
        }

        fish.invoke()
    }
}