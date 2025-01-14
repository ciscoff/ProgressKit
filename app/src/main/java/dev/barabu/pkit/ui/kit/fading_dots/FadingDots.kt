package dev.barabu.pkit.ui.kit.fading_dots

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

private const val NUM_DOTS = 12

@Composable
fun FadingDots() {
}

@Composable
fun FadingCircleDotsBox(
    boxSize: Dp,
    rayAngle: State<Float>,
    numDots: Int = NUM_DOTS
) {

    val density = LocalDensity.current
    val dotRadius = remember { with(density) { boxSize.toPx() * 0.05f } }

    val segment = remember { 360f / numDots }

    Spacer(modifier = Modifier
        .size(boxSize)
        .drawBehind {

            val dotCenter = Offset(size.width - dotRadius * 5, center.y)

            repeat(NUM_DOTS) { i ->

                val dotAngle = i * segment// 0f, 30f, 60f, ...

                rotate(dotAngle) {

                    // NOTE: Операции % и mod работают по разному (см документацию).
                    //  Операция mod это floor div аналогично mod в GLSL.
                    //    mod(X, Y) = X - Y * floor(X/Y)
                    //  Пример:
                    //    mod(-120.0, 100.0)     =>>
                    //    floor(-120./100.) = -2 =>>
                    //    -120 - 100*(-2) = -120 + 200 = 80
                    //  NOTE: Знак результата операции mod равен знаку Y (!)

                    // Это значит, что знак diffAngle всегда положительный
                    // Например, rayAngle=0f, dotAngle=330f
                    //   mod(0f - 330f, 360f)   >
                    //   floor(-330f/360f)      > -1f
                    //   -330f - 360f*(-1f)     >  30f
                    // Например, rayAngle=0f, dotAngle=30f
                    //   mod(0f - 30f, 360f)   >
                    //   floor(-30f/360f)      > -1f
                    //   -30f - 360f*(-1f)     >  330f
                    // То есть мы можем вычислить на сколько градусов луч ОПЕРЕЖАЕТ каждую точку
                    val diffAngle = (rayAngle.value - dotAngle).mod(360f)
                    val fraction = 1f - diffAngle / 360f

                    drawCircle(
                        color = Color.Red,
                        radius = dotRadius,
                        center = dotCenter,
                        alpha = fraction * fraction * fraction // ускоряем градиент цвета
                    )
                }
            }
        }
    )
}
