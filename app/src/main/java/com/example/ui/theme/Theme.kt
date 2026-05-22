package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CustomDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    secondary = ElectricPurple,
    tertiary = GoldenOrange,
    background = CyberBackground,
    surface = CyberSurface,
    onPrimary = CyberBackground,
    onSecondary = CyberBackground,
    onBackground = TradingTextPrimary,
    onSurface = TradingTextPrimary,
    onTertiary = CyberBackground,
    outline = CyberBorder,
    surfaceVariant = CyberSurfaceLighter
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
