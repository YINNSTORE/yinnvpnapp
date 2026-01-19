package com.yinnstore.vpnapp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object Api {
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    private fun base(): String {
        val fromBuild = try { BuildConfig.API_BASE_URL } catch (_: Throwable) { "" }
        val b = (fromBuild ?: "").trim()
        return if (b.isNotEmpty()) b.trimEnd('/') else "https://yinnhosting.serv00.net/api"
    }

    private fun post(url: String, bodyObj: JSONObject, token: String? = null): JSONObject {
        val body = bodyObj.toString().toRequestBody(jsonMedia)
        val rb = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
        if (!token.isNullOrBlank()) rb.addHeader("Authorization", "Bearer $token")

        client.newCall(rb.build()).execute().use { res ->
            val s = res.body?.string().orEmpty()
            return JSONObject(s.ifBlank { "{}" })
        }
    }

    private fun get(url: String, token: String): JSONObject {
        val req = Request.Builder()
            .url(url)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(req).execute().use { res ->
            val s = res.body?.string().orEmpty()
            return JSONObject(s.ifBlank { "{}" })
        }
    }

    fun login(email: String, password: String): JSONObject =
        post(base() + "/login.php", JSONObject().put("email", email).put("password", password))

    fun register(name: String, email: String, password: String): JSONObject =
        post(base() + "/register.php", JSONObject().put("name", name).put("email", email).put("password", password))

    fun me(token: String): JSONObject =
        get(base() + "/me.php", token)

    fun logout(token: String): JSONObject =
        post(base() + "/logout.php", JSONObject(), token)
}
