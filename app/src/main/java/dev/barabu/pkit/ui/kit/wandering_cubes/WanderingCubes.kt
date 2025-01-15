package dev.barabu.pkit.ui.kit.wandering_cubes

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import dev.barabu.pkit.utils.bezier.CubicBezierDouble
import kotlin.math.abs
import kotlin.math.max

@Composable
fun WanderingCubes(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {

    val tileSizeRatio = 4f

    val density = LocalDensity.current
    val distance = remember {
        with(density) {
            ((boxSize / tileSizeRatio) * (tileSizeRatio - 1)).toPx()
        }
    }

    val easing = remember {
        CubicBezierDouble(
            b = PointF(0.6f, 0f),
            c = PointF(0.4f, 1f),
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = -1f,
        infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = easing),
            repeatMode = RepeatMode.Restart
        ), label = "1"
    )


    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {


        Box(modifier = Modifier.size(boxSize)) {

            Spacer(
                Modifier
                    .align(Alignment.TopStart)
                    .size(boxSize / tileSizeRatio)
                    .graphicsLayer {
                        val scale = abs(fraction) * 0.5f + 0.5f // 1 <> .5 <> 1
                        scaleY = scale
                        scaleX = scale

                        val angle = fraction * 90f
                        rotationZ = angle

                        translationX = (1f - max(0f, fraction)) * distance
                        translationY = (max(0f, -fraction)) * distance
                    }
                    .drawWithCache {
                        onDrawBehind {
                            drawRect(color = tintColor)
                        }
                    })

            Spacer(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(boxSize / tileSizeRatio)
                    .graphicsLayer {
                        val scale = abs(fraction) * 0.5f + 0.5f // 1 <> .5 <> 1
                        scaleY = scale
                        scaleX = scale

                        val angle = fraction * 90f
                        rotationZ = angle

                        translationX = -(1f - max(0f, fraction)) * distance
                        translationY = -(max(0f, -fraction)) * distance
                    }
                    .drawWithCache {
                        onDrawBehind {
                            drawRect(color = tintColor)
                        }
                    })
        }
    }
}