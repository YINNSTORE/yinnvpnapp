package com.yinnstore.vpnapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.*

sealed class MainTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : MainTab("home", "Home", Icons.Filled.Home)
    object Deposit : MainTab("deposit", "Deposit", Icons.Filled.Settings)
    object Buy : MainTab("buy", "Beli VPN", Icons.Filled.Settings)
    object Account : MainTab("account", "Akun", Icons.Filled.Person)
    object Panel : MainTab("panel", "Control", Icons.Filled.Settings)
}

@Composable
fun MainScaffold() {
    val nav = rememberNavController()
    val tabs = listOf(
        MainTab.Home,
        MainTab.Deposit,
        MainTab.Buy,
        MainTab.Account,
        MainTab.Panel
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YinnVPN") },
                actions = {
                    var open by remember { mutableStateOf(false) }

                    IconButton(onClick = { open = true }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }

                    DropdownMenu(
                        expanded = open,
                        onDismissRequest = { open = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (ThemeState.isDark.value)
                                        "🌞 Mode Siang"
                                    else
                                        "🌙 Mode Malam"
                                )
                            },
                            onClick = {
                                ThemeState.isDark.value = !ThemeState.isDark.value
                                open = false
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination?.route

                tabs.forEach {
                    NavigationBarItem(
                        selected = current == it.route,
                        onClick = {
                            nav.navigate(it.route) {
                                launchSingleTop = true
                                popUpTo(nav.graph.startDestinationId)
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = it.label
                            )
                        },
                        label = { Text(it.label) }
                    )
                }
            }
        }
    ) { inner ->
        Box(Modifier.padding(inner)) {
            NavHost(nav, startDestination = MainTab.Home.route) {
                composable("home") { HomeScreen() }
                composable("deposit") { DepositScreen() }
                composable("buy") { BuyVpnScreen() }
                composable("account") { AccountScreen() }
                composable("panel") { ControlPanelScreen() }
            }
        }
    }
}
