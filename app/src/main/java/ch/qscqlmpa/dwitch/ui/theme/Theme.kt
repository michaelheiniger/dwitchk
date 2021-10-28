package ch.qscqlmpa.dwitch.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFFff6f00),
    primaryVariant = Color(0xFFc43e00),
    secondary = SecondaryColor,
    secondaryVariant = Color(0xFF018786),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFFF0000),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

private val LightColorPalette = lightColors(
    primary = PrimaryColor,
    primaryVariant = PrimaryDarkColor,
    secondary = SecondaryColor,
    secondaryVariant = Color(0xFF018786),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFFF0000),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

@Composable
fun DwitchTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
