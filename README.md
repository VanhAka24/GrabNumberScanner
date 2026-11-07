# Grab Number Scanner 📱⚡

An Android utility app designed for delivery and ride-hailing drivers (e.g., Grab, ShopeeFood) who use a dual-phone setup. The app scans phone numbers directly from the screen of a secondary phone (or printed paper/receipts) using on-device OCR and automatically launches the system dialer to place cellular calls immediately.

---

## ✨ Features

- ⚡ **Ultra-Fast On-Device OCR:** Powered by Google ML Kit Text Recognition and Android CameraX. Completely offline with zero latency and high privacy preservation.
- 📞 **Comprehensive Phone Number Detection:**
  - Full support for Vietnamese mobile prefixes (`03x`, `05x`, `07x`, `08x`, `09x`) and landlines (`02x`).
  - Full support for international phone numbers in standard E.164 formats (`+...`, `00...`).
- 🎯 **2-Frame Consecutive Confirmation:** Requires matching phone numbers across 2 consecutive frames (~150ms) to prevent accidental triggers from order codes, invoice totals, or balances.
- 📳 **Haptic Feedback:** Vibrates subtly upon successful detection right before launching the dialer.
- 💡 **Smart Flashlight Control:** Dedicated hardware torch toggle that works even while scanning is paused.
- 🌐 **Multi-Language Support (7 Languages):** Seamless real-time switching between Vietnamese, English, Chinese, Russian, French, Korean, and Japanese.
- 🪶 **Ultra Lightweight (~2.7 MB):** Drastically reduced APK footprint using R8 minification, resource shrinking, and ABI filters (`arm64-v8a`, `armeabi-v7a`).

---

## 🛠 Prerequisites

- Android 6.0 (API Level 23) or higher
- Android Studio (Ladybug / Koala or newer)
- JDK 17
- Compile SDK 35 / Target SDK 35

---

## 🚀 Getting Started

1. Clone this repository:
   ```bash
   git clone https://github.com/VanhAka24/GrabNumberScanner.git
   ```
2. Open the project in **Android Studio**.
3. Allow Gradle to sync dependencies.
4. Connect an Android device with USB debugging enabled, then click **Run** (`Shift + F10`).
5. Grant Camera permission on first launch.
6. Aim the camera at the phone number to automatically detect and launch the dialer.

---

## 📦 Pre-built APKs

Ready-to-install APK files are available:
- **Release APK (Optimized, ~2.7 MB):** `app/build/outputs/apk/release/app-release.apk`
- **Debug APK (~7.3 MB):** `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
