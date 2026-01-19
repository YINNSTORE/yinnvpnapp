package com.yinnstore.vpnapp.ui.theme

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

private fun lightScheme(): ColorScheme = lightColorScheme(
    primary = Navy,
    background = LightBg,
    surface = Color(0xFFF7F8FB),
    onPrimary = Color.White,
    onBackground = TextDarkNavy,
    onSurface = TextDarkNavy
)

private fun darkScheme(): ColorScheme = darkColorScheme(
    primary = Navy,
    background = DarkNavy,
    surface = Color(0xFF0B203F),
    onPrimary = Color.White,
    onBackground = Color(0xFFE9EEF8),
    onSurface = Color(0xFFE9EEF8)
)

@Composable
fun YinnVPNTheme(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {
    val target = if (darkMode) darkScheme() else lightScheme()

    val primary by animateColorAsState(targetValue = target.primary, animationSpec = tween(220), label = "primary")
    val background by animateColorAsState(targetValue = target.background, animationSpec = tween(220), label = "background")
    val surface by animateColorAsState(targetValue = target.surface, animationSpec = tween(220), label = "surface")
    val onPrimary by animateColorAsState(targetValue = target.onPrimary, animationSpec = tween(220), label = "onPrimary")
    val onBackground by animateColorAsState(targetValue = target.onBackground, animationSpec = tween(220), label = "onBackground")
    val onSurface by animateColorAsState(targetValue = target.onSurface, animationSpec = tween(220), label = "onSurface")

    val scheme = target.copy(
        primary = primary,
        background = background,
        surface = surface,
        onPrimary = onPrimary,
        onBackground = onBackground,
        onSurface = onSurface
    )

    MaterialTheme(
        colorScheme = scheme,
        content = content
    )
}
