package com.yinnstore.vpnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yinnstore.vpnapp.ui.theme.YinnVPNTheme

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val darkMode by appViewModel.darkMode.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            YinnVPNTheme(darkMode = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.Auth
                    ) {
                        composable(Routes.Auth) {
                            AuthFlow(
                                onSuccess = {
                                    navController.navigate(Routes.Main) {
                                        popUpTo(Routes.Auth) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        composable(Routes.Main) {
                            MainScaffold(
                                navController = navController,
                                darkMode = darkMode,
                                onToggleDarkMode = { appViewModel.setDarkMode(it) }
                            ) { mod ->
                                Box(modifier = mod.fillMaxSize()) {
                                    MainTabs()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
