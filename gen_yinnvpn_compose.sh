#!/usr/bin/env bash
set -euo pipefail

APP_NAME="YinnVPN"
PKG="com.yinnstore.vpnapp"
NS="$PKG"
APP_ID="$PKG"
MIN_SDK=24
TARGET_SDK=34
COMPILE_SDK=34

# -------- helpers --------
pkg_path() { echo "$1" | tr '.' '/'; }
SRC_DIR="app/src/main/java/$(pkg_path "$PKG")"
THEME_DIR="$SRC_DIR/ui/theme"

echo "[1/6] Create directories..."
mkdir -p \
  ".github/workflows" \
  "app/src/main" \
  "$SRC_DIR" \
  "$THEME_DIR"

echo "[2/6] Write Gradle settings/build files..."
cat > settings.gradle <<EOF
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "$APP_NAME"
include(":app")
EOF

cat > build.gradle <<'EOF'
plugins { }
EOF

cat > gradle.properties <<'EOF'
org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
EOF

mkdir -p gradle
cat > gradle/libs.versions.toml <<'EOF'
[versions]
agp = "8.5.2"
kotlin = "1.9.24"
coreKtx = "1.13.1"
activityCompose = "1.9.1"
composeBom = "2024.06.00"
navCompose = "2.7.7"

[libraries]
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
material3 = { group = "androidx.compose.material3", name = "material3" }
material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
nav-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navCompose" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
EOF

cat > app/build.gradle <<EOF
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace "$NS"
    compileSdk $COMPILE_SDK

    defaultConfig {
        applicationId "$APP_ID"
        minSdk $MIN_SDK
        targetSdk $TARGET_SDK
        versionCode 1
        versionName "1.0"
    }

    buildFeatures { compose true }

    composeOptions {
        kotlinCompilerExtensionVersion "1.5.14"
    }

    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.material.icons.extended)

    implementation(libs.nav.compose)

    debugImplementation(libs.compose.ui.tooling)
}
EOF

echo "[3/6] Write AndroidManifest..."
cat > app/src/main/AndroidManifest.xml <<EOF
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application
        android:allowBackup="true"
        android:label="$APP_NAME"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.NoActionBar">

        <activity
            android:name=".$(echo "$PKG" | awk -F. '{print $NF}')MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
EOF

# Fix activity name to .MainActivity (simpler & correct)
perl -0777 -pe 's/android:name="\.[^"]*MainActivity"/android:name=".MainActivity"/g' -i app/src/main/AndroidManifest.xml

echo "[4/6] Write Kotlin source files (MainActivity + Nav + Screens + Auth + Theme)..."
cat > "$SRC_DIR/MainActivity.kt" <<'EOF'
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
    data object Login : Route("login")
    data object Register : Route("register")
    data object Main : Route("main")
}

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Route.Login.r
    ) {
        composable(Route.Login.r) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Route.Main.r) {
                        popUpTo(Route.Login.r) { inclusive = true }
                    }
                },
                onGoRegister = { nav.navigate(Route.Register.r) }
            )
        }
        composable(Route.Register.r) {
            RegisterScreen(onRegisterDone = { nav.popBackStack() })
        }
        composable(Route.Main.r) {
            MainScaffold()
        }
    }
}
EOF

cat > "$SRC_DIR/MainScaffold.kt" <<'EOF'
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
EOF

cat > "$SRC_DIR/Screens.kt" <<'EOF'
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
EOF

cat > "$SRC_DIR/AuthScreens.kt" <<'EOF'
package com.yinnstore.vpnapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("YinnVPN", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(6.dp))
        Text("Login untuk lanjut", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onLoginSuccess() }, // sementara langsung masuk
            modifier = Modifier.fillMaxWidth()
        ) { Text("Masuk") }

        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onGoRegister, modifier = Modifier.fillMaxWidth()) {
            Text("Belum punya akun? Daftar")
        }
    }
}

@Composable
fun RegisterScreen(onRegisterDone: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Daftar", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = pass2,
            onValueChange = { pass2 = it },
            label = { Text("Ulangi Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = { onRegisterDone() }, modifier = Modifier.fillMaxWidth()) {
            Text("Buat Akun")
        }
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onRegisterDone, modifier = Modifier.fillMaxWidth()) {
            Text("Kembali ke Login")
        }
    }
}
EOF

cat > "$THEME_DIR/Color.kt" <<'EOF'
package com.yinnstore.vpnapp.ui.theme

import androidx.compose.ui.graphics.Color

val Navy = Color(0xFF0B1B3B)
val Navy2 = Color(0xFF102A5C)
val Sky = Color(0xFF3B82F6)

val BgLight = Color(0xFFF6F8FF)
val BgDark = Color(0xFF070C16)
EOF

cat > "$THEME_DIR/Theme.kt" <<'EOF'
package com.yinnstore.vpnapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Navy2,
    secondary = Sky,
    background = BgLight,
    surface = BgLight
)

private val DarkColors = darkColorScheme(
    primary = Sky,
    secondary = Sky,
    background = BgDark,
    surface = BgDark
)

@Composable
fun YinnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content
    )
}
EOF

echo "[5/6] Write GitHub Actions workflow (build APK)..."
cat > .github/workflows/android.yml <<'EOF'
name: Android CI

on:
  push:
    branches: [ "main" ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: "temurin"
          java-version: "17"
          cache: "gradle"

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew :app:assembleDebug

      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/*.apk
EOF

echo "[6/6] Ensure Gradle Wrapper exists (download once)..."
if [ ! -f "gradlew" ] || [ ! -d "gradle/wrapper" ]; then
  echo "Gradle wrapper not found. Downloading..."
  GRADLE_VER="8.7"
  curl -L -o gradle.zip "https://services.gradle.org/distributions/gradle-${GRADLE_VER}-bin.zip"
  unzip -q gradle.zip
  mkdir -p gradle/wrapper
  cp "gradle-${GRADLE_VER}/lib/plugins/gradle-wrapper-main-${GRADLE_VER}.jar" gradle/wrapper/gradle-wrapper.jar 2>/dev/null || true
  # Fallback: new gradle wrapper jars sometimes moved; use find
  if [ ! -f gradle/wrapper/gradle-wrapper.jar ]; then
    WRAPJAR="$(find "gradle-${GRADLE_VER}" -type f -name "gradle-wrapper-*.jar" | head -n 1 || true)"
    if [ -n "${WRAPJAR:-}" ]; then
      cp "$WRAPJAR" gradle/wrapper/gradle-wrapper.jar
    fi
  fi

  cat > gradle/wrapper/gradle-wrapper.properties <<EOF
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\\://services.gradle.org/distributions/gradle-${GRADLE_VER}-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

  cat > gradlew <<'EOF'
#!/usr/bin/env sh
set -e
DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
JAVA_CMD="${JAVA_HOME:-}/bin/java"
if [ ! -x "$JAVA_CMD" ]; then JAVA_CMD="java"; fi
exec "$JAVA_CMD" -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
EOF
  chmod +x gradlew

  rm -rf "gradle-${GRADLE_VER}" gradle.zip
fi

echo "DONE. Project generated."
echo "Next: git add . && git commit -m 'init' && git push"
