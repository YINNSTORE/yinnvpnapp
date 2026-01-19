package com.yinnstore.vpnapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Navy2,
    secondary = Sky,
    background = BgLight,
    surface = BgLight
)

private val DarkColors = darkColorScheme(
    primary = Sky,
    secondary = Sky,
    background = BgDark,
    surface = BgDark
)

@Composable
fun YinnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
