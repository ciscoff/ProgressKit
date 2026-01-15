package dev.barabu.pkit.ui.controls.wavy_button

import android.graphics.PointF
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.plus
import androidx.core.graphics.times
import dev.barabu.pkit.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private class Wavy(
    private val size: Dp,
    private val sectors: Int = 8,
    private val density: Density
) {
    private val sizeScale = with(density) { size.toPx() * 0.48f }
    private val numPoints = sectors * 2
    private val path = Path()

    /**
     * Точки на единичной окружности
     */
    private val circlePoints: Array<PointF> by lazy {

        var angle = 0.0
        val step = (2 * PI) / numPoints

        var points: Array<PointF> = Array(numPoints) { PointF() }
        repeat(numPoints) { i ->
            points[i] = PointF(cos(angle).toFloat(), sin(angle).toFloat())
            angle += step
        }
        points
    }

    fun getCurvedPath(
        /** 0.0 <> 1.0 */
        curvature: Float
    ): Path {
        val origin = with(density) { size.toPx() * 0.5f }
        val center = PointF(origin, origin)

        // Скалируем и позиционируем относительно центра
        val points = getCurvePoints(curvature).map { it * sizeScale + center }.toTypedArray()

        return path.apply {
            reset()
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

    /**
     * Эти точки формируют волновую линию, но также сформированы вокруг единичной окружности.
     * @param curvature - степень искривления и она же scale ratio.
     */
    private fun getCurvePoints(
        /** 0.0 <> 1.0 */
        curvature: Float
    ): Array<PointF> {
        var curvatureDir = 1.0f // направление искривления

        val points = circlePoints.copyOf()
        repeat(numPoints) { i ->
            if (i % 2 != 0) {
                curvatureDir *= -1f
                points[i] = points[i] * (1f + curvature * curvatureDir)
            }
        }
        return points
    }
}

/**
 * Расчет скалирования иконки для реакции на клик.
 * Функция выдает 1.0>0.9>1.0 в диапазоне изменения аргумента -1.0<>1.0
 */
private fun iconScaleEasing(fraction: Float): Float = fraction * fraction * 0.1f + 0.9f

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
            val p2 = if (i == num - 1) innerArray[0] else innerArray[i + 1]
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
    val cps = 0.05f // control point's scale
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
fun WavyButton(
    tintColor: Color,
    boxSize: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    val wavy = remember { Wavy(boxSize, 32, density) }

    var isClicked by remember { mutableStateOf(false) }

    var iconScaleTrigger by remember { mutableFloatStateOf(1f) }

    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")

    var isRotating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val angle = if (isRotating) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            infiniteRepeatable(
                animation = tween(4800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "angle"
        ).value
    } else 0f

    val curvature by animateFloatAsState(
        targetValue = if (isClicked) 1f else 0f,
        animationSpec = tween(500),
        label = "curvature"
    )

    val scaleFraction by animateFloatAsState(
        targetValue = iconScaleTrigger,
        animationSpec = tween(160),
        label = "scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Icon(
            modifier = Modifier
                .size(boxSize * 0.36f)
                .scale(iconScaleEasing(scaleFraction)),
            painter = painterResource(id = R.drawable.ic_on_off),
            contentDescription = null,
            tint = tintColor
        )

        Spacer(
            modifier = Modifier
                .size(boxSize)
                .clip(shape = CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Color.White, bounded = true),
                    onClick = {
                        isClicked = !isClicked
                        iconScaleTrigger = -1f * iconScaleTrigger
                        if (isClicked) {
                            isRotating = true
                        } else {
                            scope.launch {
                                delay(500)
                                isRotating = false
                            }
                        }
                    }
                )
                .graphicsLayer {
                    rotationZ = angle
                }
                .drawWithCache {
                    onDrawBehind {
                        drawPath(
                            path = wavy.getCurvedPath(curvature * 0.05f),
                            color = tintColor,
                            style = Stroke(
                                width = 2.dp.toPx()
                            )
                        )
                    }
                }
        )
    }
}
