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

        val tv = findViewById<TextView>(R.id.tvHome)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val nav = findViewById<NavigationView>(R.id.navView)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        // hamburger harus kanan: DrawerLayout END
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_drawer) {
                drawer.openDrawer(GravityCompat.END)
                true
            } else false
        }

        // bottom nav (sementara ganti text sesuai tab)
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> tv.text = "Home"
                R.id.nav_wallet -> tv.text = "Wallet"
                R.id.nav_cart -> tv.text = "Cart"
                R.id.nav_user -> tv.text = "Account"
                R.id.nav_settings -> tv.text = "Settings"
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

                    // transisi smooth (fade)
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
