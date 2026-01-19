package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun isAdmin(session: SessionStore): Boolean {
        // PRIORITAS: role dari DB/backend (disarankan)
        val r = session.role()?.lowercase()?.trim().orEmpty()
        if (r == "admin") return true

        // fallback sementara (kalau role belum dikirim)
        val email = session.email()?.lowercase()?.trim().orEmpty()
        return email == "tesreset660@gmail.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        UiPrefs.apply(this)
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

        val tvHome = findViewById<TextView>(R.id.tvHome)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val navView = findViewById<NavigationView>(R.id.navView)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // hamburger kanan atas
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_drawer) {
                drawer.openDrawer(GravityCompat.END)
                true
            } else false
        }

        // bottom nav base
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> tvHome.text = "Home"
                R.id.nav_wallet -> tvHome.text = "Wallet"
                R.id.nav_cart -> tvHome.text = "Cart"
                R.id.nav_user -> tvHome.text = "Akun"
                R.id.nav_settings -> tvHome.text = "Setting"
                R.id.nav_admin_dynamic -> tvHome.text = "Admin"
            }
            true
        }

        // Admin tab dynamic (tanpa XML)
        if (isAdmin(session) && bottom.menu.findItem(R.id.nav_admin_dynamic) == null) {
            bottom.menu.add(0, R.id.nav_admin_dynamic, 999, "Admin")
                .setIcon(R.drawable.ic_settings) // aman pakai icon yg sudah ada dulu
        }

        // drawer: toggle mode + logout
        val darkItem = navView.menu.findItem(R.id.menu_dark_mode)
        darkItem.isChecked = UiPrefs.isDark(this)

        navView.setNavigationItemSelectedListener { mi ->
            when (mi.itemId) {
                R.id.menu_dark_mode -> {
                    val newVal = !mi.isChecked
                    mi.isChecked = newVal
                    UiPrefs.setDark(this, newVal)
                    drawer.closeDrawer(GravityCompat.END)
                    window.decorView.post {
                        recreate()
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                    true
                }
                R.id.menu_logout -> {
                    scope.launch {
                        try { withContext(Dispatchers.IO) { Api.logout(token) } } catch (_: Throwable) {}
                        session.clear()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
