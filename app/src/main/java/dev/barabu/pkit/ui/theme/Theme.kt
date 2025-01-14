package dev.barabu.pkit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

data class AppExtraColors(
    val textPrimary: Color = Color.Unspecified,
)

val LocalExtraColors = staticCompositionLocalOf {
    AppExtraColors()
}

@Composable
fun ProgressKitTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme
    val extraColors: AppExtraColors
    if (isDarkTheme) {
        colorScheme = DarkColorScheme
        extraColors = DarkExtraColors
    } else {
        colorScheme = LightColorScheme
        extraColors = LightExtraColors
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content // <-- Load MainScreen
        )
    }
}

object AppTheme {

    val extraColors: AppExtraColors
        @Composable
        get() = LocalExtraColors.current
}
