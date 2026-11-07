# Giữ nguyên mã nguồn của app
-keep class com.example.grabnumberscanner.** { *; }

# Giữ các thư viện CameraX và ML Kit cần thiết
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
