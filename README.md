# 🚀 YinnVPN — Modern VPN Account Manager

YinnVPN adalah aplikasi **Android Native (Kotlin + Jetpack Compose)** untuk pengelolaan dan penjualan akun VPN seperti **SSH, VMESS, VLESS, dan TROJAN** dengan tampilan modern, ringan, dan siap dikembangkan ke sistem produksi.

Aplikasi ini dirancang agar:
- 🔐 Aman (tanpa kredensial VPS di APK)
- ⚡ Cepat & ringan
- 🎨 Tampilan modern (Material 3 + Dark Navy Mode)
- 🤖 Bisa di-build otomatis via GitHub Actions (tanpa PC)

---

## ✨ Fitur Utama

### 🔑 Autentikasi
- Login & Register (UI siap, backend-ready)
- Struktur siap untuk JWT / API Auth

### 🧭 Navigasi Modern
- Bottom Navigation (5 menu)
  - Home
  - Deposit
  - Beli VPN
  - Akun
  - Control Panel
- Top App Bar dengan **Hamburger Menu (☰)**

### 🌗 Dark / Light Mode
- Toggle **Mode Siang & Malam**
- Dark mode menggunakan **Dark Navy** (bukan abu-abu / hitam)
- Transisi smooth (tanpa restart aplikasi)

### 🎨 UI/UX
- Input field **rounded (bulat)**
- Layout rapi & modern
- Login / Register tidak kosong, berbasis Card
- Material 3 + Jetpack Compose

### ⚙️ Build & CI/CD
- Build otomatis via **GitHub Actions**
- Input **custom version name**
- Opsi **upload GitHub Release (true/false)**
- APK sudah **signed (keystore)**
- Ukuran APK sudah dioptimasi (minify + shrink + split ABI)

---

## 📦 Teknologi yang Digunakan

- **Android Native** (Kotlin)
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**
- **Gradle + R8**
- **GitHub Actions (CI/CD)**

---

## 📱 Status Fitur

| Fitur | Status |
|-----|------|
| UI & Navigasi | ✅ Selesai |
| Dark / Light Mode | ✅ Selesai |
| Build Otomatis (CI) | ✅ Selesai |
| Signing APK | ✅ Selesai |
| Optimasi Ukuran APK | ✅ Selesai |
| Backend API | ⏳ Dalam pengembangan |
| Provisioning VPN | ⏳ Dalam pengembangan |
| Payment Gateway | ⏳ Opsional |

---

## 🧠 Arsitektur (Singkat)