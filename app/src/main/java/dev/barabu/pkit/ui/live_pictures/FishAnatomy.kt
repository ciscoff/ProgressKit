package dev.barabu.pkit.ui.live_pictures

import android.graphics.PointF
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.min

class FishAnatomy(
    private val totalStripes: Int,
    private val headStripes: Int,
    private val mouthOffset: Offset,
    private val eyeOffset: Offset
) {

    val bodyStripes = totalStripes - headStripes

    // fish body path (no fins)
    private val p0 = PointF(0f, 0.6f)
    private val p1 = PointF(1.29f, -0.11f)
    private val p2 = PointF(1.29f, 1.11f)
    private val p3 = PointF(0f, 0.4f)

    // fins only path
    private val f0 = PointF(0f, 0f)
    private val f1 = PointF(.2f, .2f)
    private val f2 = PointF(.2f, .8f)
    private val f3 = PointF(0f, 1f)
    private val f4 = PointF(1.3f, .7f)
    private val f5 = PointF(1.0f, .2f)

    fun provideHeadlessBodyShape(stripeIndex: Int): Shape {
        return object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline =
                Outline.Generic(createFinlessBodyPath(size.width, size.height, stripeIndex))
        }
    }

    fun provideHeadShape(mouthFraction: Float): Shape {
        return object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline = Outline.Generic(createHeadPath(size.width, size.height, mouthFraction))
        }
    }

    fun provideFinsOnlyShape(stripeIndex: Int): Shape {
        return object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline =
                Outline.Generic(createClippedFinsPath(size.width, size.height, stripeIndex))
        }
    }

    private fun createMouthPath(headWidth: Float, headHeight: Float, fraction: Float): Path {
        val topLeft = Offset(headWidth / 2, headHeight / 2) + mouthOffset
        val width = headWidth * 0.5f
        val height = headHeight * 0.03f * fraction
        val rect = Rect(topLeft, Size(width, height))

        return Path().apply { addOval(rect) }
    }

    private fun createEyeHolePath(headWidth: Float, headHeight: Float, eyeRadius: Float): Path {
        val topLeft = Offset(
            x = headWidth / 2 - eyeOffset.x - eyeRadius,
            y = headHeight / 2 - eyeOffset.y - eyeRadius
        )
        return Path().apply { addOval(Rect(topLeft, Size(eyeRadius * 2, eyeRadius * 2))) }
    }

    private fun createEyeDotPath(headWidth: Float, headHeight: Float, eyeRadius: Float): Path {
        val topLeft = Offset(
            x = headWidth / 2 - eyeOffset.x - eyeRadius * 0.8f,
            y = headHeight / 2 - eyeOffset.y - eyeRadius * 0.7f
        )
        return Path().apply { addOval(Rect(topLeft, Size(eyeRadius * 2, eyeRadius * 2))) }
    }

    private fun createHeadPath(width: Float, height: Float, mouthFraction: Float): Path {
        val stripeWidth = width / headStripes
        val boundsWidth = stripeWidth * totalStripes
        val boundsCenter = Offset(width - boundsWidth / 2, height / 2)

        val bounds = Rect(
            boundsCenter.x - boundsWidth / 2,
            boundsCenter.y - height / 2,
            boundsCenter.x + boundsWidth / 2,
            boundsCenter.y + height / 2
        )

        val eyeRadius = min(width, height) * 0.15f

        return buildFinlessBodyPath(bounds)
            .minus(createMouthPath(width, height, mouthFraction))
            .minus(createEyeHolePath(width, height, eyeRadius))
            .plus(createEyeDotPath(width, height, eyeRadius * 0.7f))
    }

    private fun createClippedFinsPath(width: Float, height: Float, index: Int): Path {
        val i = totalStripes / 2
        val boundsWidth = width * totalStripes
        val boundsCenter = Offset(width / 2 - (index - i) * width, height / 2)

        val bounds = Rect(
            boundsCenter.x - boundsWidth / 2,
            boundsCenter.y - height / 2,
            boundsCenter.x + boundsWidth / 2,
            boundsCenter.y + height / 2
        )

        return buildFinsPath(bounds) - buildFinlessBodyPath(bounds)
    }

    private fun createFinlessBodyPath(width: Float, height: Float, index: Int): Path {

        val i = totalStripes / 2
        val boundsWidth = width * totalStripes
        val boundsCenter = Offset(width / 2 - (index - i) * width, height / 2)

        val bounds = Rect(
            boundsCenter.x - boundsWidth / 2,
            boundsCenter.y - height / 2,
            boundsCenter.x + boundsWidth / 2,
            boundsCenter.y + height / 2
        )

        return buildFinlessBodyPath(bounds)
    }

    private fun buildFinlessBodyPath(bounds: Rect): Path = Path().apply {
        moveTo(bounds.left + p0.x * bounds.width, bounds.top + p0.y * bounds.height)

        cubicTo(
            x1 = bounds.left + p1.x * bounds.width, y1 = bounds.top + p1.y * bounds.height,
            x2 = bounds.left + p2.x * bounds.width, y2 = bounds.top + p2.y * bounds.height,
            x3 = bounds.left + p3.x * bounds.width, y3 = bounds.top + p3.y * bounds.height
        )

        lineTo(bounds.left + 0.05f * bounds.width, bounds.top + 0.5f * bounds.height)
        lineTo(bounds.left + p0.x * bounds.width, bounds.top + p0.y * bounds.height)
    }

    private fun buildFinsPath(bodyBounds: Rect): Path {

        val boundsCenter = Offset(
            bodyBounds.left + bodyBounds.width / 2,
            bodyBounds.top + bodyBounds.height / 2
        ) + Offset(bodyBounds.width * 0.2f, 0.0f)

        val bounds = Rect(
            boundsCenter.x - bodyBounds.width / 4,
            boundsCenter.y - bodyBounds.height / 6,
            boundsCenter.x + bodyBounds.width / 4,
            boundsCenter.y + bodyBounds.height / 6
        )

        return Path().apply {
            moveTo(bounds.left + f0.x * bounds.width, bounds.top + f0.y * bounds.height)
            cubicTo(
                x1 = bounds.left + f1.x * bounds.width, y1 = bounds.top + f1.y * bounds.height,
                x2 = bounds.left + f2.x * bounds.width, y2 = bounds.top + f2.y * bounds.height,
                x3 = bounds.left + f3.x * bounds.width, y3 = bounds.top + f3.y * bounds.height
            )
            cubicTo(
                x1 = bounds.left + f4.x * bounds.width, y1 = bounds.top + f4.y * bounds.height,
                x2 = bounds.left + f5.x * bounds.width, y2 = bounds.top + f5.y * bounds.height,
                x3 = bounds.left + f0.x * bounds.width, y3 = bounds.top + f0.y * bounds.height
            )
        }
    }
}