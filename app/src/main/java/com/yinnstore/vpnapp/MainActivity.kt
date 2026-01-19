package com.yinnstore.vpnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yinnstore.vpnapp.ui.theme.YinnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YinnTheme {
                AppNav()
            }
        }
    }
}

sealed class Route(val r: String) {
    object Login : Route("login")
    object Main : Route("main")
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Route.Login.r) {
        composable(Route.Login.r) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Route.Main.r) {
                        popUpTo(Route.Login.r) { inclusive = true }
                    }
                },
                onGoRegister = { /* TODO */ }
            )
        }
        composable(Route.Main.r) {
            MainScaffold()
        }
    }
}
