package dev.barabu.pkit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.barabu.pkit.R
import dev.barabu.pkit.ui.kit.chasing_dots.ChasingDots
import dev.barabu.pkit.ui.kit.cube_grid.CubeGrid
import dev.barabu.pkit.ui.kit.double_bounce.DoubleBounce
import dev.barabu.pkit.ui.kit.folding_grid.FoldingGrid
import dev.barabu.pkit.ui.kit.rotating_plane.RotatingPlane
import dev.barabu.pkit.ui.theme.ChasingDotsColor
import dev.barabu.pkit.ui.theme.CubeGridColor
import dev.barabu.pkit.ui.theme.DoubleBounceColor
import dev.barabu.pkit.ui.theme.FadingDotsColor
import dev.barabu.pkit.ui.theme.FoldingGridColor
import dev.barabu.pkit.ui.theme.RotatingPlaneColor
import kotlin.math.abs

enum class Screen(val title: String, val color: Color) {
    RotatingPlane("Rotating plane", RotatingPlaneColor),
    FoldingGrid("Folding Grid", FoldingGridColor),
    CubeGrid("Cube Grid", CubeGridColor),
    DoubleBounce("Double Bounce", DoubleBounceColor),
//    FadingDots("Fading Dots", FadingDotsColor),
    ChasingDots("Chasing dots", ChasingDotsColor)
}

@Composable
fun MainScreen(windowInsets: PaddingValues) {

    val fillMaxSizeModifier = remember { Modifier.fillMaxSize() }

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { Screen.entries.size }
    )

    val backgroundColor by remember {
        derivedStateOf {
            val fraction = pagerState.currentPageOffsetFraction
            val currentPage = pagerState.currentPage

            if (currentPage == -1) { // First composition
                Screen.entries[0].color
            } else if (fraction == 0f) { // No Scroll or page landed (especially the last one)
                Screen.entries[currentPage].color
            } else if (fraction > 0f) { // Scroll to left
                lerp(
                    start = Screen.entries[currentPage].color,
                    stop = Screen.entries[currentPage + 1].color,
                    fraction = fraction
                )
            } else { // Scroll to right
                lerp(
                    start = Screen.entries[currentPage].color,
                    stop = Screen.entries[currentPage - 1].color,
                    fraction = abs(fraction)
                )
            }
        }
    }

    val fontFamily = remember {
        FontFamily(Font(R.font.lobster_regular, FontWeight.Normal))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(windowInsets)
    ) {
        // Background
        Spacer(modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color = backgroundColor, size = size)
            })

        Column(modifier = fillMaxSizeModifier.padding(windowInsets)) {
            Text(
                text = Screen.entries[pagerState.currentPage].title,
                textAlign = TextAlign.Start,
                color = Color.White,
                fontSize = 24.sp,
                fontFamily = fontFamily,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState,
                key = { i -> i }) { index ->

                when (Screen.entries[index]) {
                    Screen.RotatingPlane -> {
                        RotatingPlane(
                            tintColor = Color.White,
                            boxSize = 100.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.FoldingGrid -> {
                        FoldingGrid(
                            tintColor = Color.White,
                            boxSize = 160.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.CubeGrid -> {
                        CubeGrid(
                            tintColor = Color.White,
                            tileSize = 30.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.DoubleBounce -> {
                        DoubleBounce(
                            tintColor = Color.White,
                            boxSize = 80.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

//                    Screen.FadingDots -> {
//                    }

                    Screen.ChasingDots -> {
                        ChasingDots(
                            tintColor = Color.White,
                            boxSize = 100.dp,
                            containerColor = Color.Unspecified,
                            modifier = fillMaxSizeModifier
                        )
                    }
                }
            }
        }
    }
}