package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val nav = findViewById<NavigationView>(R.id.navView)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // hamburger kanan atas
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_drawer) {
                drawer.openDrawer(GravityCompat.END)
                true
            } else false
        }

        // bottom nav (sementara: toast biar gak error kalau view content belum siap)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_wallet -> Toast.makeText(this, "Wallet", Toast.LENGTH_SHORT).show()
                R.id.nav_cart -> Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show()
                R.id.nav_user -> Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // drawer items
        val darkItem = nav.menu.findItem(R.id.menu_dark_mode)
        darkItem.isChecked = UiPrefs.isDark(this)

        nav.setNavigationItemSelectedListener { mi ->
            when (mi.itemId) {
                R.id.menu_dark_mode -> {
                    val newVal = !mi.isChecked
                    mi.isChecked = newVal

                    UiPrefs.setDark(this, newVal)
                    drawer.closeDrawer(GravityCompat.END)

                    // smooth transition
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
