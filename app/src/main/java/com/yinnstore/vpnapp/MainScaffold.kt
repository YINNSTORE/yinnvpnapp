package com.yinnstore.vpnapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class MainTab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Home : MainTab("home", "Home", Icons.Filled.Home)
    data object Deposit : MainTab("deposit", "Deposit", Icons.Filled.AttachMoney)
    data object Buy : MainTab("buy", "Beli VPN", Icons.Filled.RocketLaunch)
    data object Account : MainTab("account", "Akun", Icons.Filled.Person)
    data object Panel : MainTab("panel", "Control Panel", Icons.Filled.Settings)
}

@Composable
fun MainScaffold() {
    val tabs = listOf(MainTab.Home, MainTab.Deposit, MainTab.Buy, MainTab.Account, MainTab.Panel)
    val nav = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination?.route

                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab.route,
                        onClick = {
                            nav.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            NavHost(navController = nav, startDestination = MainTab.Home.route) {
                composable(MainTab.Home.route) { HomeScreen() }
                composable(MainTab.Deposit.route) { DepositScreen() }
                composable(MainTab.Buy.route) { BuyVpnScreen() }
                composable(MainTab.Account.route) { AccountScreen() }
                composable(MainTab.Panel.route) { ControlPanelScreen() }
            }
        }
    }
}
