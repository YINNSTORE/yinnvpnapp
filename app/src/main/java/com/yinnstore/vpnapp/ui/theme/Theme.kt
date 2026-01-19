package com.yinnstore.vpnapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.yinnstore.vpnapp.ThemeState

private val LightColors = lightColorScheme(
    primary = Navy2,
    secondary = Sky,
    background = BgLight,
    surface = BgLight
)

private val DarkColors = darkColorScheme(
    primary = Sky,
    secondary = Sky,
    background = Navy,
    surface = Navy
)

@Composable
fun YinnTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (ThemeState.isDark.value) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
