package dev.barabu.pkit.ui.kit.wavy_contour

import android.graphics.PointF
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.plus
import androidx.core.graphics.times
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private fun provideRoughWavyPath(dim: Dp, pieces: Int = 8, density: Density): Path {
    val num = if (pieces % 2 == 0) pieces else (pieces + 1)

    val origin = with(density) { dim.toPx() * 0.5f }
    val center = PointF(origin, origin)

    val scale = with(density) { dim.toPx() * 0.5f }

    // массив точке на внутренней единичной окружности
    var innerArray: Array<PointF> = Array(num) { PointF() }
    // массив точек на внешней окружности
    var outerArray: Array<PointF> = Array(num / 2) { PointF() }

    var angle = 0.0
    val step = (2 * PI) / num

    repeat(num) { i ->
        innerArray[i] = PointF(cos(angle).toFloat(), sin(angle).toFloat())
        angle += step
    }

    // формируется сложением "векторов"
    repeat(num / 2) { i ->
        val j = i * 2
        outerArray[i] = innerArray[j] + innerArray[j + 1]
    }

    // Скалируем и позиционируем относительно центра
    innerArray = innerArray.map { it * scale + center }.toTypedArray()
    outerArray = outerArray.map { it * scale + center }.toTypedArray()

    // Строим контур.
    // Используем абсолютные координаты, поэтому quadraticTo, а не relativeQuadraticTo
    return Path().apply {
        moveTo(innerArray[0].x, innerArray[0].y)
        repeat(num) { i ->
            val p1 = if (i % 2 == 0) outerArray[i / 2] else center
            val p2 = if(i == num - 1) innerArray[0] else innerArray[i + 1]
            quadraticTo(
                x1 = p1.x,
                y1 = p1.y,
                x2 = p2.x,
                y2 = p2.y,
            )
        }
        close()
    }
}

/**
 * @param sectors - это количество "опорных" секторов окружности. Точки окружности на границах
 * этих секторов являются start/end кривых Безье. Каждый опорный сектор делится пополам и точки
 * окружности, разделяющие эти половинки, являются контрольными точками кривых Безье.
 * Контрольные точки скалируются, чтобы смещаться от контура окружности внутрь и наружу.
 * Таким образом получается волна.
 */
private fun provideSmoothWavyPath(size: Dp, sectors: Int = 8, density: Density): Path {
    val num = sectors * 2

    val sizeScale = with(density) { size.toPx() * 0.9f }
    val origin = with(density) { size.toPx() * 0.5f }
    val center = PointF(origin, origin)

    var angle = 0.0
    val step = (2 * PI) / num
    val cps = 0.1f // control point's scale
    var cpsDir = 1.0f // cps скалирование - уменьшаем или увеличиваем

    // массив точек формирующих кривую
    var points: Array<PointF> = Array(num) { PointF() }

    repeat(num) { i ->
        val point = PointF(cos(angle).toFloat(), sin(angle).toFloat())
        points[i] = when {
            i % 2 == 0 -> point
            else -> {
                cpsDir *= -1f
                point * (1f + cps * cpsDir)
            }
        }
        angle += step
    }

    // Скалируем и позиционируем относительно центра
    points = points.map { it * sizeScale + center }.toTypedArray()

    // Строим контур.
    // Используем абсолютные координаты, поэтому quadraticTo, а не relativeQuadraticTo
    return Path().apply {
        moveTo(points[0].x, points[0].y)
        repeat(sectors) { i ->
            val j = if (i == sectors - 1) 0 else (i * 2 + 2)
            val p1 = points[i * 2 + 1]
            val p2 = points[j]
            quadraticTo(
                x1 = p1.x,
                y1 = p1.y,
                x2 = p2.x,
                y2 = p2.y,
            )
        }
        close()
    }
}

@Composable
fun WavyContour(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val contour = remember { provideSmoothWavyPath(boxSize, 24, density) }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        infiniteRepeatable(
            animation = tween(25600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "angle"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Spacer(
            modifier = Modifier
                .size(boxSize)
                .graphicsLayer {
                    rotationZ = angle
                }
                .drawWithCache {
                    onDrawBehind {
                        drawPath(
                            path = contour,
                            color = tintColor,
                            style = Stroke(
                                width = 2f
                            )
                        )
                    }
                }
        )
    }
}
