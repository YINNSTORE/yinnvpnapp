package com.yinnstore.vpnapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
private fun SimplePage(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable fun HomeScreen() = SimplePage("Home", "Dashboard ringkas (stats, info, promo)")
@Composable fun DepositScreen() = SimplePage("Deposit", "Topup saldo (placeholder)")
@Composable fun BuyVpnScreen() = SimplePage("Beli VPN", "Pilih paket SSH/VMESS/VLESS/TROJAN")
@Composable fun AccountScreen() = SimplePage("Akun", "Profil + riwayat pesanan")
@Composable fun ControlPanelScreen() = SimplePage("Control Panel", "Khusus admin/fitur lanjutan")
