package com.yinnstore.vpnapp

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object Api {
    private val jsonMedia = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private fun base() = BuildConfig.API_BASE_URL.trimEnd('/')

    fun login(email: String, password: String): JSONObject {
        val body = JSONObject().put("email", email).put("password", password)
            .toString().toRequestBody(jsonMedia)

        val req = Request.Builder()
            .url(base() + "/login.php")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(req).execute().use { res ->
            return JSONObject(res.body?.string().orEmpty())
        }
    }

    fun register(name: String, email: String, password: String): JSONObject {
        val body = JSONObject().put("name", name).put("email", email).put("password", password)
            .toString().toRequestBody(jsonMedia)

        val req = Request.Builder()
            .url(base() + "/register.php")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(req).execute().use { res ->
            return JSONObject(res.body?.string().orEmpty())
        }
    }

    fun me(token: String): JSONObject {
        val req = Request.Builder()
            .url(base() + "/me.php")
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(req).execute().use { res ->
            return JSONObject(res.body?.string().orEmpty())
        }
    }

    fun logout(token: String): JSONObject {
        val req = Request.Builder()
            .url(base() + "/logout.php")
            .post("{}".toRequestBody(jsonMedia))
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(req).execute().use { res ->
            return JSONObject(res.body?.string().orEmpty())
        }
    }
}
