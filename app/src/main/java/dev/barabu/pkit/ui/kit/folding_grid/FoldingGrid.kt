package dev.barabu.pkit.ui.kit.folding_grid

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import kotlin.math.floor

private const val STEP = 90f
private const val FOLDING_GRID_ROWS = 2
private const val FOLDING_GRID_COLS = 2

@Composable
fun FoldingGrid(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {

    // Tiles seq numbers
    val indices = remember {
        arrayOf(
            intArrayOf(3, 2),
            intArrayOf(0, 1)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val fraction = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "fraction"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer {
                rotationZ = -45f
            }
            .background(color = Color.Unspecified)
    ) {
        // Строка из двух столбцов
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(FOLDING_GRID_COLS) { i ->
                FoldingTiles(
                    indices = indices[i],
                    fraction = fraction,
                    color = tintColor,
                    tileSize = boxSize / 4,
                )
            }
        }
    }
}

@Composable
fun FoldingTiles(
    indices: IntArray,
    fraction: State<Float>,
    color: Color,
    tileSize: Dp,
) {

    // Transformation pivots
    val origins = remember {
        mapOf(
            0 to arrayOf(
                TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f),
                TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0.5f)
            ),
            1 to arrayOf(
                TransformOrigin(pivotFractionX = 0f, pivotFractionY = 0.5f),
                TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f)
            ),
            2 to arrayOf(
                TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f),
                TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0.5f)
            ),
            3 to arrayOf(
                TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0.5f),
                TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)
            )
        )
    }

    Column(
        modifier = Modifier.size(width = tileSize, height = tileSize * 2)
    ) {
        repeat(FOLDING_GRID_ROWS) { row ->
            Spacer(modifier = Modifier
                .graphicsLayer {
                    val indexLow = indices[row]
                    val indexHigh = indexLow + 4

                    val degrees = fraction.value * 720f
                    val angle = degrees.mod(STEP)

                    val indexCurrent = floor(degrees / STEP).toInt()


                    alpha = if (degrees <= 360f) {
                        if (indexLow < indexCurrent) 0f else 1f
                    } else /*if (degrees <= 720f)*/ {
                        if (indexHigh < indexCurrent) 1f else 0f
                    }

                    if (indexLow == indexCurrent || indexHigh == indexCurrent) {

                        when (indexCurrent) {
                            0, 4 -> {
                                when (indexCurrent) {
                                    0 -> {
                                        rotationX = -angle
                                        transformOrigin = origins[0]!![0]
                                        alpha = 1f - angle / STEP
                                    }

                                    4 -> {
                                        rotationY = -(STEP - angle)
                                        transformOrigin = origins[0]!![1]
                                        alpha = angle / STEP
                                    }
                                }
                            }

                            1, 5 -> {
                                when (indexCurrent) {
                                    1 -> {
                                        rotationY = -angle
                                        transformOrigin = origins[1]!![0]
                                        alpha = 1f - angle / STEP
                                    }

                                    5 -> {
                                        rotationX = STEP - angle
                                        transformOrigin = origins[1]!![1]
                                        alpha = angle / STEP
                                    }
                                }
                            }

                            2, 6 -> {
                                when (indexCurrent) {
                                    2 -> {
                                        rotationX = angle
                                        transformOrigin = origins[2]!![0]
                                        alpha = 1f - angle / STEP
                                    }

                                    6 -> {
                                        rotationY = STEP - angle
                                        transformOrigin = origins[2]!![1]
                                        alpha = angle / STEP
                                    }
                                }
                            }

                            3, 7 -> {
                                when (indexCurrent) {
                                    3 -> {
                                        rotationY = angle
                                        transformOrigin = origins[3]!![0]
                                        alpha = 1f - angle / STEP
                                    }

                                    7 -> {
                                        rotationX = -(STEP - angle)
                                        transformOrigin = origins[3]!![1]
                                        alpha = angle / STEP
                                    }
                                }
                            }
                        }
                    }
                }
                .size(tileSize)
                .drawWithCache { onDrawBehind { drawRect(color = color, size = size) } })
        }
    }
}