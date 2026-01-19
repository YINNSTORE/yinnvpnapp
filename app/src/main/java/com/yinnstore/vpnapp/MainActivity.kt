package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

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

        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val nav = findViewById<NavigationView>(R.id.navView)
        val tv = findViewById<TextView>(R.id.tvHome)

        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_drawer) {
                drawer.openDrawer(GravityCompat.END)
                true
            } else false
        }

        nav.setNavigationItemSelectedListener { mi ->
            when (mi.itemId) {
                R.id.menu_dark_mode -> {
                    mi.isChecked = !mi.isChecked
                    AppCompatDelegate.setDefaultNightMode(
                        if (mi.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    )
                    drawer.closeDrawer(GravityCompat.END)
                    true
                }
                R.id.menu_logout -> {
                    doLogout(session)
                    drawer.closeDrawer(GravityCompat.END)
                    true
                }
                else -> false
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val res = Api.me(token)
                val ok = res.optBoolean("ok", false)
                val user = res.optJSONObject("user")
                withContext(Dispatchers.Main) {
                    if (ok && user != null) {
                        tv.text = "Halo, " + user.optString("name", "User")
                    } else {
                        Toast.makeText(this@MainActivity, "Session habis, login lagi", Toast.LENGTH_SHORT).show()
                        session.clear()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            } catch (_: Throwable) {
                // ignore
            }
        }
    }

    private fun doLogout(session: SessionStore) {
        val token = session.token() ?: ""
        CoroutineScope(Dispatchers.IO).launch {
            try { Api.logout(token) } catch (_: Throwable) {}
            withContext(Dispatchers.Main) {
                session.clear()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
