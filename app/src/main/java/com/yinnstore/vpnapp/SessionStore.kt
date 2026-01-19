package com.yinnstore.vpnapp

import android.content.Context

class SessionStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun token(): String? = sp.getString("token", null)
    fun email(): String? = sp.getString("email", null)
    fun role(): String? = sp.getString("role", null) // "admin" / "user" (kalau backend ngirim)

    fun setToken(token: String, expiresAt: String? = null) {
        sp.edit()
            .putString("token", token)
            .putString("expires_at", expiresAt)
            .apply()
    }

    fun setProfile(email: String?, role: String?) {
        sp.edit()
            .putString("email", email)
            .putString("role", role)
            .apply()
    }

    fun clear() {
        sp.edit().clear().apply()
    }
}
