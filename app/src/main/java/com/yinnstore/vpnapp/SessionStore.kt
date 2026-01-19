package com.yinnstore.vpnapp

import android.content.Context

class SessionStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun token(): String? = sp.getString("token", null)
    fun expiresAt(): String? = sp.getString("expires_at", null)

    fun setToken(token: String, expiresAt: String?) {
        sp.edit().putString("token", token).putString("expires_at", expiresAt).apply()
    }

    fun clear() { sp.edit().clear().apply() }
}
