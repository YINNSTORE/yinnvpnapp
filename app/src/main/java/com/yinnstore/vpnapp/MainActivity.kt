package com.yinnstore.vpnapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
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

        // bottom nav (sementara action basic dulu)
        bottom.setOnItemSelectedListener { mi ->
            // nanti kita isi konten real tiap tab
            true
        }

        nav.setNavigationItemSelectedListener { mi ->
            when (mi.itemId) {
                R.id.menu_dark_mode -> {
                    val checked = !mi.isChecked
                    mi.isChecked = checked
                    AppCompatDelegate.setDefaultNightMode(
                        if (checked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    )
                    // smooth: recreate biar theme apply (anim fade)
                    drawer.closeDrawer(GravityCompat.END)
                    window.decorView.postDelayed({ recreate() }, 120)
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
