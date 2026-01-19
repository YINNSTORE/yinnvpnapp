package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val session = SessionStore(this)
        val token = session.token()
        if (token.isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // cek token via /me.php (backend kamu)
        scope.launch {
            try {
                val res = withContext(Dispatchers.IO) { Api.me(token) }
                if (!res.optBoolean("ok", false)) {
                    session.clear()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                    return@launch
                }
            } catch (_: Throwable) {
                // kalau jaringan error, biarin user tetap masuk
            }
        }

        val tv = findViewById<TextView>(R.id.tvPage)
        val nav = findViewById<BottomNavigationView>(R.id.bottomNav)

        nav.setOnItemSelectedListener { item ->
            tv.text = when (item.itemId) {
                R.id.nav_home -> "Home"
                R.id.nav_deposit -> "Deposit"
                R.id.nav_buy -> "Beli VPN"
                R.id.nav_account -> "Akun"
                R.id.nav_control -> "Control Panel"
                else -> "Home"
            }
            true
        }

        nav.selectedItemId = R.id.nav_home
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
