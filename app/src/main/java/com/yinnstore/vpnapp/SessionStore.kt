package com.yinnstore.vpnapp

import android.content.Context

class SessionStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun token(): String? = sp.getString("token", null)?.takeIf { it.isNotBlank() }
    fun expiresAt(): String? = sp.getString("expires_at", null)?.takeIf { it.isNotBlank() }
    fun email(): String? = sp.getString("email", null)?.takeIf { it.isNotBlank() }

    fun setToken(token: String, expiresAt: String? = null) {
        sp.edit()
            .putString("token", token)
            .putString("expires_at", expiresAt ?: "")
            .apply()
    }

    fun setEmail(email: String?) {
        sp.edit().putString("email", email ?: "").apply()
    }

    // ===== Role support =====
    fun setRole(role: String?) {
        sp.edit().putString("role", role ?: "").apply()
    }

    fun role(): String? = sp.getString("role", null)?.takeIf { it.isNotBlank() }

    fun isAdmin(): Boolean = role()?.equals("admin", ignoreCase = true) == true

    fun clear() {
        sp.edit().clear().apply()
    }
}
