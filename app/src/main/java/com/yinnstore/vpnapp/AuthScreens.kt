package com.yinnstore.vpnapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onGoRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(6.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("Welcome 👋", style = MaterialTheme.typography.headlineMedium)
                Text("Login untuk melanjutkan")
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    email, { email = it },
                    label = { Text("Email") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    pass, { pass = it },
                    label = { Text("Password") },
                    shape = RoundedCornerShape(18.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))
                Button(onClick = onLoginSuccess, modifier = Modifier.fillMaxWidth()) {
                    Text("Masuk")
                }
                TextButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
                    Text("Daftar Akun")
                }
            }
        }
    }
}
