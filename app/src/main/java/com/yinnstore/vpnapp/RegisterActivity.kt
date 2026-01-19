package com.yinnstore.vpnapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        UiPrefs.apply(this)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPass = findViewById<TextInputEditText>(R.id.etPass)

        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val btnBack = findViewById<MaterialButton>(R.id.btnBackLogin)
        val pb = findViewById<View>(R.id.pbRegister)

        btnBack.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name = etName.text?.toString()?.trim().orEmpty()
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val pass = etPass.text?.toString().orEmpty()

            if (name.isBlank() || email.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnBack.isEnabled = false
            pb.visibility = View.VISIBLE

            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { Api.register(name, email, pass) }
                    val ok = res.optBoolean("ok", false)
                    if (!ok) {
                        Toast.makeText(this@RegisterActivity, res.optString("message", "Gagal daftar"), Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    Toast.makeText(this@RegisterActivity, "Akun dibuat, silakan login", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (_: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Server error", Toast.LENGTH_SHORT).show()
                } finally {
                    pb.visibility = View.GONE
                    btnRegister.isEnabled = true
                    btnBack.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
