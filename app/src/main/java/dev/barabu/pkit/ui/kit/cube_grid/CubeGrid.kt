package dev.barabu.pkit.ui.kit.cube_grid

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import dev.barabu.pkit.utils.clamp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos

private const val CUBE_GRID_ROWS = 3
private const val CUBE_GRID_COLS = 3

@Composable
fun CubeGrid(
    tintColor: Color,
    tileSize: Dp,
    modifier: Modifier = Modifier
) {

    val pi = remember { PI.toFloat() }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val angle = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * pi,
        infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Column(
            modifier = Modifier.size(tileSize * 3)
        ) {

            repeat(CUBE_GRID_ROWS) { i ->
                TilesRow(
                    rowNum = CUBE_GRID_ROWS - 1 - i,
                    color = tintColor,
                    tileSize = tileSize,
                    angle = angle
                )
            }
        }
    }
}

@Composable
fun TilesRow(
    rowNum: Int,
    color: Color,
    tileSize: Dp,
    angle: State<Float>,
) {

    val pi = remember { PI.toFloat() }

    val offset = remember { pi / 4 }

    Row(
        modifier = Modifier.size(width = tileSize * 3, height = tileSize)
    ) {

        repeat(CUBE_GRID_COLS) { i ->
            Spacer(modifier = Modifier
                .graphicsLayer {
                    val a = clamp((angle.value - i * offset - rowNum * offset), 0f, pi)
                    val fraction = abs(cos(a))
                    scaleX = fraction
                    scaleY = fraction
                }
                .size(tileSize)
                .drawWithCache { onDrawBehind { drawRect(color = color, size = size) } })
        }
    }
}