package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val session = SessionStore(this)
        if (!session.token().isNullOrBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnToRegister = findViewById<MaterialButton>(R.id.btnToRegister)

        btnToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPass.text?.toString().orEmpty()

            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res = Api.login(email, pass)
                    val ok = res.optBoolean("ok", false)

                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        if (!ok) {
                            Toast.makeText(this@LoginActivity, res.optString("message", "Gagal"), Toast.LENGTH_SHORT).show()
                            return@withContext
                        }

                        val token = res.optString("token", "")
                        val exp = res.optString("expires_at", null)
                        if (token.isNotBlank()) session.setToken(token, exp)
                    
                    val role = res.optString("role", "").trim().ifEmpty { null }
                    session.setRole(role)
session.setProfile(res.optString("role", null))

                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        btnLogin.isEnabled = true
                        Toast.makeText(this@LoginActivity, "Server error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
