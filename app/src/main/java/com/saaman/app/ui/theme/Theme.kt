package com.saaman.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val UniversalColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF3F4F6), // Gray 100
    outline = BorderLight,
    error = Negative
)

@Composable
fun SaamanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = UniversalColorScheme,
        content = content
    )
}
