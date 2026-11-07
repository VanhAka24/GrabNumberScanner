# Grab Number Scanner 📱⚡

Ứng dụng Android hỗ trợ tài xế công nghệ quét số điện thoại từ màn hình điện thoại thứ hai (hoặc văn bản/hóa đơn) bằng camera và tự động mở trình quay số gọi điện tức thì.

---

## ✨ Tính năng nổi bật

- ⚡ **Nhận diện siêu tốc & chính xác:** Sử dụng Google ML Kit Vision On-Device kết hợp CameraX, xử lý trực tiếp trên máy không cần mạng.
- 📞 **Hỗ trợ toàn diện số điện thoại:**
  - Thuật toán nhận diện tất cả đầu số di động Việt Nam (`03x`, `05x`, `07x`, `08x`, `09x`) và số bàn (`02x`).
  - Hỗ trợ số điện thoại quốc tế định dạng chuẩn E.164 (`+...`, `00...`).
- 🎯 **Cơ chế xác thực 2-frame:** Xác nhận số sau 2 khung hình liên tiếp (~150ms) chống quét nhầm mã đơn hàng, giá tiền hay số dư.
- 📳 **Rung phản hồi (Haptic Feedback):** Rung nhẹ báo hiệu quét thành công trước khi chuyển sang màn hình quay số.
- 💡 **Điều khiển đèn Flash thông minh:** Bật/tắt đèn trợ sáng trực tiếp ngay cả khi đang tạm dừng quét.
- 🌐 **Đa ngôn ngữ (7 ngôn ngữ):** Hỗ trợ chuyển đổi nhanh giữa Tiếng Việt, Tiếng Anh, Tiếng Trung, Tiếng Nga, Tiếng Pháp, Tiếng Hàn và Tiếng Nhật.
- 🪶 **Siêu nhẹ & Tối ưu hóa:** Dung lượng bản Release chỉ **~2.7 MB** nhờ tối ưu R8/ProGuard và ABI Filters (`arm64-v8a`, `armeabi-v7a`).

---

## 🛠 Yêu cầu hệ thống

- Android 6.0 (API level 23) trở lên
- Android Studio Ladybug / Koala hoặc mới hơn
- JDK 17
- Compile SDK 35 / Target SDK 35

---

## 🚀 Cài đặt & Sử dụng

1. Mở dự án `GrabNumberScanner` bằng **Android Studio**.
2. Đồng bộ Gradle (Sync Project with Gradle Files).
3. Kết nối điện thoại hoặc máy ảo, nhấn **Run** (`Shift + F10`) để cài đặt.
4. Cấp quyền Camera khi ứng dụng mở lần đầu.
5. Hướng camera vào số điện thoại cần gọi để ứng dụng tự động nhận diện và mở cuộc gọi.

---

## 📦 File APK đóng gói sẵn

File APK đã ký sẵn và có thể cài đặt trực tiếp lên thiết bị Android:
- `app/build/outputs/apk/release/app-release.apk` (~2.7 MB)
- `app/build/outputs/apk/debug/app-debug.apk` (~7.3 MB)
