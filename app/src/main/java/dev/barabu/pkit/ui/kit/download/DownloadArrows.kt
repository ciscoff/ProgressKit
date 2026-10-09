package dev.barabu.pkit.ui.kit.download

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import dev.barabu.pkit.R
import dev.barabu.pkit.ui.theme.AccentArrowColor
import dev.barabu.pkit.ui.theme.CasualArrowColor
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

@Composable
fun DownloadArrows(
    arrowSize: Dp,
    modifier: Modifier = Modifier,
    colorA: Color = CasualArrowColor,
    colorB: Color = AccentArrowColor,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Arrows Transition")
    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Arrows Progress"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            ChasingArrows(
                fraction = fraction, // Раз в 2 сек (durationMillis = 2000)
                arrowSize = arrowSize,
                arrows = 4,
                colorA = colorA,
                colorB = colorB
            )

            ScatteringArrows(
                fraction = (fraction * 2) % 1, // Раз в 1 сек
                arrowSize = arrowSize,
                colorA = colorA,
                colorB = colorB
            )
        }
    }
}

/**
 * Оптимально:
 *  4 стрелы и durationMillis = 2000
 *  2 стрелы и durationMillis = 1000
 */

@Composable
private fun ChasingArrows(
    fraction: Float,
    arrowSize: Dp,
    arrows: Int = 4,
    colorA: Color = CasualArrowColor,
    colorB: Color = AccentArrowColor,

    ) {

    /**
     * Прямоугольная коробка: высота в 2 раза выше чем Icon. Из-за этого у Icon
     * есть пространство для старта (это как бы выше ее собственной позиции).
     */
    Box(
        modifier = Modifier
            .height(arrowSize * 2)
            .width(arrowSize)
            .clipToBounds(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val density = LocalDensity.current
        val arrowHeightPx = with(density) { arrowSize.toPx() }

        val peakPower = 4.0 // Это превращает синусоиду в торчки.
        val startY = -arrowHeightPx

        repeat(arrows) { i ->

            val progress = (fraction + i * (1f / arrows)) % 1
            val sinFraction = sin(PI * progress)
            val translateY = startY + arrowHeightPx * progress // Start -> Finish
            val alpha = sinFraction.pow(peakPower).toFloat() // 0 -> 1 -> 0
            val iconColor = if (i % 2 == 0) colorA else colorB

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "arrow_$i",
                tint = iconColor.copy(alpha = alpha),
                modifier = Modifier
                    .size(arrowSize)
                    .zIndex(alpha * alpha)
                    .graphicsLayer {
                        translationY = translateY
                    }
            )
        }
    }

}

@Composable
private fun ScatteringArrows(
    fraction: Float,
    arrowSize: Dp,
    colorA: Color = CasualArrowColor,
    colorB: Color = AccentArrowColor,
) {

    val density = LocalDensity.current
    val arrowHeightPx = with(density) { arrowSize.toPx() }
    val startY = 0

    Box(
        modifier = Modifier
            .height(arrowSize * 2)
            .width(arrowSize)
            .clipToBounds(),
        contentAlignment = Alignment.Center
    ) {

        // Резкий старт, плавный спуск
        val x = PI * fraction
        val sinFraction = sin(x + sin(x))

        // Start -> Finish -> Start
        val translateY = (startY + (arrowHeightPx * 0.2f) * sinFraction.toFloat())

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = "arrow_1",
            tint = colorA.copy(alpha = min(1f, 1f - fraction * 4)),
            modifier = Modifier
                .size(arrowSize * (1 - fraction))
                .graphicsLayer {
                    translationY = -fraction * arrowHeightPx * 0.4f
                }
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = "arrow_2",
            tint = colorB.copy(alpha = 0.8f),
            modifier = Modifier
                .size(arrowSize)
                .graphicsLayer {
                    translationY = translateY
                }
        )
    }
}