package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnBackLogin = findViewById<TextView>(R.id.btnBackLogin)

        btnBackLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPass.text?.toString().orEmpty()

            if (name.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val res = Api.register(name, email, pass)
                    val ok = res.optBoolean("ok", false)

                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        if (!ok) {
                            Toast.makeText(this@RegisterActivity, res.optString("message", "Gagal"), Toast.LENGTH_SHORT).show()
                            return@withContext
                        }
                        Toast.makeText(this@RegisterActivity, "Akun dibuat, silakan login", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                } catch (e: Throwable) {
                    withContext(Dispatchers.Main) {
                        btnRegister.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Server error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
