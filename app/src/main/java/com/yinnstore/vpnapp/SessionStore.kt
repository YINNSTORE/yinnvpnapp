package com.yinnstore.vpnapp

import android.content.Context

class SessionStore(ctx: Context) {
    private val sp = ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun token(): String? = sp.getString("token", null)
    fun expiresAt(): String? = sp.getString("expires_at", null)

    fun setToken(token: String, expiresAt: String?) {
        sp.edit().putString("token", token).putString("expires_at", expiresAt).apply()
    
    private val KEY_ROLE = "role"

    fun role(): String? = sp.getString(KEY_ROLE, null)

    fun setRole(role: String?) {
        if (role.isNullOrBlank()) {
            sp.edit().remove(KEY_ROLE).apply()
        } else {
            sp.edit().putString(KEY_ROLE, role).apply()
        }
    }
}


    fun clear() { sp.edit().clear().apply() 
    private val KEY_ROLE = "role"

    fun role(): String? = sp.getString(KEY_ROLE, null)

    fun setRole(role: String?) {
        if (role.isNullOrBlank()) {
            sp.edit().remove(KEY_ROLE).apply()
        } else {
            sp.edit().putString(KEY_ROLE, role).apply()
        }
    }
}


    private val KEY_ROLE = "role"

    fun role(): String? = sp.getString(KEY_ROLE, null)

    fun setRole(role: String?) {
        if (role.isNullOrBlank()) {
            sp.edit().remove(KEY_ROLE).apply()
        } else {
            sp.edit().putString(KEY_ROLE, role).apply()
        }
    }
}

