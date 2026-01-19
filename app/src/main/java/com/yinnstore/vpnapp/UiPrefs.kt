package com.yinnstore.vpnapp

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object UiPrefs {
    private const val SP = "ui_prefs"
    private const val KEY_DARK = "dark_mode"

    fun isDark(ctx: Context): Boolean =
        ctx.getSharedPreferences(SP, Context.MODE_PRIVATE).getBoolean(KEY_DARK, false)

    fun apply(ctx: Context) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark(ctx)) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun setDark(ctx: Context, dark: Boolean) {
        ctx.getSharedPreferences(SP, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DARK, dark).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
