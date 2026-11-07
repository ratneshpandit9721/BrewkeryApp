package com.brewkery.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Espresso,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF0DFD0),
    onPrimaryContainer = EspressoDark,
    secondary = Caramel,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCF),
    onSecondaryContainer = Color(0xFF3A0E00),
    tertiary = Leaf,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8EED8),
    onTertiaryContainer = Color(0xFF002113),
    background = Cream,
    onBackground = Cocoa,
    surface = Foam,
    onSurface = Cocoa,
    surfaceVariant = Latte,
    onSurfaceVariant = Color(0xFF564237),
    outline = Color(0xFFD9C7B8),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = EspressoLight,
    onPrimary = EspressoDark,
    primaryContainer = Color(0xFF4E3424),
    onPrimaryContainer = Color(0xFFF6E6D8),
    secondary = CaramelLight,
    onSecondary = Color(0xFF3A0E00),
    secondaryContainer = Color(0xFF5C2E1C),
    onSecondaryContainer = Color(0xFFFFDBCF),
    tertiary = LeafLight,
    onTertiary = Color(0xFF003820),
    tertiaryContainer = Color(0xFF0E4F31),
    onTertiaryContainer = Color(0xFFC8EED8),
    background = FoamDark,
    onBackground = Color(0xFFF6EDE4),
    surface = SurfaceDark,
    onSurface = Color(0xFFF6EDE4),
    surfaceVariant = Color(0xFF3A2C24),
    onSurfaceVariant = Color(0xFFDCC8B8),
    outline = Color(0xFF6E5A4C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
