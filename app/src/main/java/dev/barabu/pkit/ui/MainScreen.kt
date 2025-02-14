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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.barabu.pkit.R
import dev.barabu.pkit.ui.kit.chasing_dots.ChasingDots
import dev.barabu.pkit.ui.kit.citrix_launcher.CitrixLauncher
import dev.barabu.pkit.ui.kit.cube_grid.CubeGrid
import dev.barabu.pkit.ui.kit.double_bounce.DoubleBounce
import dev.barabu.pkit.ui.kit.fading_dots.FadingDots
import dev.barabu.pkit.ui.kit.folding_grid.FoldingGrid
import dev.barabu.pkit.ui.kit.heart_beat.HeartBeat
import dev.barabu.pkit.ui.kit.miui_launcher.MiuiLauncher
import dev.barabu.pkit.ui.kit.motion_loop.MotionLoop
import dev.barabu.pkit.ui.kit.racing_dots.RacingDots
import dev.barabu.pkit.ui.kit.rotating_plane.RotatingPlane
import dev.barabu.pkit.ui.kit.round_ripple.RoundRipple
import dev.barabu.pkit.ui.kit.running_beads.RunningBeads
import dev.barabu.pkit.ui.kit.scaled_dots.ScaledDots
import dev.barabu.pkit.ui.kit.three_bounce.ThreeBounce
import dev.barabu.pkit.ui.kit.wandering_cubes.WanderingCubes
import dev.barabu.pkit.ui.kit.wave_stripes.WaveStripes
import dev.barabu.pkit.ui.live_pictures.AquariumFish
import dev.barabu.pkit.ui.theme.AquariumColor
import dev.barabu.pkit.ui.theme.ChasingDotsColor
import dev.barabu.pkit.ui.theme.CitrixLauncherColor
import dev.barabu.pkit.ui.theme.CubeGridColor
import dev.barabu.pkit.ui.theme.DoubleBounceColor
import dev.barabu.pkit.ui.theme.FadingDotsColor
import dev.barabu.pkit.ui.theme.FoldingGridColor
import dev.barabu.pkit.ui.theme.HeartBeatColor
import dev.barabu.pkit.ui.theme.MiuiLauncherColor
import dev.barabu.pkit.ui.theme.MotionLoopColor
import dev.barabu.pkit.ui.theme.RacingDotsColor
import dev.barabu.pkit.ui.theme.RotatingPlaneColor
import dev.barabu.pkit.ui.theme.RoundRippleColor
import dev.barabu.pkit.ui.theme.RunningBeadsColor
import dev.barabu.pkit.ui.theme.ScaledDotsColor
import dev.barabu.pkit.ui.theme.ThreeBounceColor
import dev.barabu.pkit.ui.theme.WanderingCubesColor
import dev.barabu.pkit.ui.theme.WaveStripesColor
import kotlin.math.abs

enum class Screen(val title: String, val color: Color) {
    RotatingPlane("Rotating plane", RotatingPlaneColor),
    MiuiLauncher("Miui Launcher", MiuiLauncherColor),
    FoldingGrid("Folding Grid", FoldingGridColor),
    CubeGrid("Cube Grid", CubeGridColor),
    WanderingCubes("Wandering Cubes", WanderingCubesColor),
    DoubleBounce("Double Bounce", DoubleBounceColor),
    RoundRipple("Round Ripple", RoundRippleColor),
    ThreeBounce("Three Bounce", ThreeBounceColor),
    MotionLoop("Motion Loop",MotionLoopColor),
    RunningBeads("Running Beads", RunningBeadsColor),
    HeartBeat("Heart Beat", HeartBeatColor),
    CitrixLauncher("Citrix Launcher", CitrixLauncherColor),
    RacingDots("Racing Dots", RacingDotsColor),
    FadingDots("Fading Dots", FadingDotsColor),
    ScaledDots("Scaled Dots", ScaledDotsColor),
    ChasingDots("Chasing dots", ChasingDotsColor),
    WaveStripes("Wave Stripes", WaveStripesColor),
    Aquarium("Aquarium", AquariumColor),
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
        /*.drawBehind {
            drawRect(color = backgroundColor, size = size)
        }*/
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

                    Screen.MotionLoop -> {
                        MotionLoop(
                            tint = Color.White,
                            boxSize = 100.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.HeartBeat -> {
                        HeartBeat(
                            tint = Color.Red,
                            boxSize = 120.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.CitrixLauncher -> {
                        CitrixLauncher(
                            tint = Color.White,
                            boxSize = 80.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.Aquarium -> {
                        AquariumFish(modifier = fillMaxSizeModifier)
                    }

                    Screen.MiuiLauncher -> {
                        MiuiLauncher(
                            color = Color.White,
                            tileSize = 24.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.RotatingPlane -> {
                        RotatingPlane(
                            tintColor = Color.White,
                            boxSize = 80.dp,
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

                    Screen.WanderingCubes -> {
                        WanderingCubes(
                            tintColor = Color.White,
                            boxSize = 100.dp,
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

                    Screen.RoundRipple -> {
                        RoundRipple(
                            tintColor = Color.White,
                            boxSize = 80.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.ThreeBounce -> {
                        ThreeBounce(
                            tintColor = Color.White,
                            dotSize = 20.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.RunningBeads -> {
                        RunningBeads(
                            tintColor = Color.White,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.RacingDots -> {
                        RacingDots(
                            tintColor = Color.White,
                            boxSize = 160.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.FadingDots -> {
                        FadingDots(
                            tintColor = Color.White,
                            boxSize = 160.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.ScaledDots -> {
                        ScaledDots(
                            tintColor = Color.White,
                            boxSize = 160.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.ChasingDots -> {
                        ChasingDots(
                            tintColor = Color.White,
                            boxSize = 100.dp,
                            modifier = fillMaxSizeModifier
                        )
                    }

                    Screen.WaveStripes -> {
                        WaveStripes(
                            tintColor = Color.White,
                            stripSizeDp = DpSize(12.dp, 36.dp),
                            modifier = fillMaxSizeModifier
                        )
                    }
                }
            }
        }
    }

//    val isDarkMode = isSystemInDarkTheme()
//    val context = LocalContext.current as ComponentActivity
//    DisposableEffect(isDarkMode) {
//        context.enableEdgeToEdge(
//            navigationBarStyle = if(!isDarkMode){
//                SystemBarStyle.light(
//                    scrim = Color.Transparent.toArgb(), // background color
//                    darkScrim = Color.Transparent.toArgb()
//                )
//            } else {
//                SystemBarStyle.dark(scrim = Color.Transparent.toArgb())
//            }
//        )
//        onDispose {
//        }
//    }
}