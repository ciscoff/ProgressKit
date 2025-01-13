package dev.barabu.pkit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.barabu.pkit.ui.chasing_dots.ChasingDots
import dev.barabu.pkit.ui.rotating_plane.RotatingPlane
import dev.barabu.pkit.ui.theme.AppTheme

enum class Screen(val title: String) {
    RotatingPlane("Rotating plane"),
    ChasingDots("Chasing dots")
}

@Composable
fun MainScreen(padding: PaddingValues) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { Screen.entries.size }
    )

    val fillMaxSizeModifier = remember { Modifier.fillMaxSize() }

    Column(
        modifier = Modifier
            .consumeWindowInsets(padding)
            .background(color = AppTheme.extraColors.rotatingPlane)
            .fillMaxSize()
    ) {

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            key = { i -> i }) { index ->

            Box(modifier = fillMaxSizeModifier) {

                when (Screen.entries[index]) {
                    Screen.RotatingPlane -> {
                        RotatingPlane(
                            tintColor = Color.White,
                            boxSize = 100.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

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