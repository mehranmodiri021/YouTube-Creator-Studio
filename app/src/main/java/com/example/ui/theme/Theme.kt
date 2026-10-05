package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StudioDarkColorScheme = darkColorScheme(
    primary = YouTubeRed,
    onPrimary = TextPrimary,
    primaryContainer = StudioCardHover,
    onPrimaryContainer = TextPrimary,
    secondary = GrowthGreen,
    onSecondary = TextInverse,
    secondaryContainer = StudioCard,
    onSecondaryContainer = GrowthGreen,
    tertiary = MetricCyan,
    onTertiary = TextInverse,
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioCard,
    onSurfaceVariant = TextSecondary,
    outline = StudioBorder,
    outlineVariant = StudioBorderLight,
    error = CriticalRed,
    onError = TextPrimary
)

private val StudioLightColorScheme = lightColorScheme(
    primary = YouTubeRed,
    onPrimary = TextPrimary,
    background = StudioBackground,
    surface = StudioSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun YouTubeCreatorStudioTheme(
    darkTheme: Boolean = true, // Default to Studio Dark aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) StudioDarkColorScheme else StudioDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioBackground.toArgb()
                window.navigationBarColor = StudioSurface.toArgb()
                val windowInsetsController = WindowCompat.getInsetsController(window, view)
                windowInsetsController.isAppearanceLightStatusBars = false
                windowInsetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
