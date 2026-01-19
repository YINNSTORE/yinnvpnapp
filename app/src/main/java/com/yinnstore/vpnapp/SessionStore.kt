package com.yinnstore.vpnapp

import android.content.Context

class SessionStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun setToken(token: String, expiresAt: String?) {
        sp.edit().putString("token", token).putString("expires_at", expiresAt).apply()
    }

    fun token(): String? = sp.getString("token", null)

    fun clear() {
        sp.edit().clear().apply()
    }
}
