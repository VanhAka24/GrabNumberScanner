package com.example.grabnumberscanner

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.atomic.AtomicBoolean

class PhoneNumberDetector(
    private val context: Context,
    private val onNumberFound: (String) -> Unit,
    private val onStatus: (String) -> Unit,
    var getString: (key: String, args: Array<out Any>) -> String
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val processing = AtomicBoolean(false)
    @Volatile private var lastCandidate = ""
    @Volatile private var consecutiveFrames = 0
    @Volatile private var lastLaunchedNumber = ""
    @Volatile private var lastLaunchTime = 0L
    @Volatile private var nullCandidateCount = 0

    fun analyze(proxy: ImageProxy) {
        val mediaImage = proxy.image
        if (mediaImage == null || !processing.compareAndSet(false, true)) {
            proxy.close()
            return
        }
        val input = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        recognizer.process(input)
            .addOnSuccessListener { result ->
                val candidate = findPhoneNumber(result.text)
                val now = System.currentTimeMillis()

                if (candidate == null) {
                    consecutiveFrames = 0
                    lastCandidate = ""
                    nullCandidateCount++
                    // Khi đưa camera ra chỗ khác (không thấy số sau 5 frame liên tiếp), xóa nhớ để khi lia lại là quét được ngay
                    if (nullCandidateCount >= 5) {
                        lastLaunchedNumber = ""
                    }
                    onStatus(getString("searching", emptyArray()))
                    return@addOnSuccessListener
                }

                nullCandidateCount = 0

                if (candidate == lastCandidate) {
                    consecutiveFrames++
                } else {
                    lastCandidate = candidate
                    consecutiveFrames = 1
                }

                // Nếu số này vừa được mở gọi trong vòng 3 giây trước và người dùng vẫn đang giữ nguyên camera
                if (candidate == lastLaunchedNumber && (now - lastLaunchTime < 3_000)) {
                    onStatus(getString("already_opened", arrayOf(candidate)))
                    return@addOnSuccessListener
                }

                onStatus(getString("found", arrayOf(candidate, consecutiveFrames)))

                // Chỉ cần 2 frame liên tiếp (~150ms) là mở cuộc gọi ngay, không phải giữ tay lâu
                if (consecutiveFrames >= 2) {
                    lastLaunchedNumber = candidate
                    lastLaunchTime = now
                    consecutiveFrames = 0
                    onNumberFound(candidate)
                    openDialer(candidate)
                }
            }
            .addOnFailureListener {
                onStatus(getString("ocr_failed", emptyArray()))
            }
            .addOnCompleteListener {
                processing.set(false)
                proxy.close()
            }
    }

    /**
     * Nhận diện số điện thoại cả trong nước (Việt Nam) và tất cả các quốc gia quốc tế (Mỹ, Hàn, Trung, Sing, v.v.)
     */
    private fun findPhoneNumber(text: String): String? {
        val normalized = text.replace('O', '0').replace('o', '0')

        // 1. Quét các số có tiền tố quốc tế '+' hoặc '00' (Ví dụ: +1..., +82..., +65..., +86..., +44..., +886...)
        val intlRegex = Regex("""(?:\+|00)\s*[\d\s().-]{7,22}""")
        for (match in intlRegex.findAll(normalized)) {
            val raw = match.value.trim()
            val digits = raw.filter { it.isDigit() }

            val intlNumber = if (raw.startsWith("00")) {
                "+" + digits.substring(2)
            } else {
                "+$digits"
            }

            val digitsCount = intlNumber.length - 1
            // Chuẩn quốc tế E.164: số điện thoại có từ 7 đến 15 chữ số
            if (digitsCount in 7..15) {
                // Nếu là đầu số Việt Nam (+84), chuẩn hóa về 0... cho thân thuộc với cuộc gọi nội địa
                if (intlNumber.startsWith("+84")) {
                    val vnLocal = "0" + intlNumber.substring(3)
                    if (vnLocal.matches(Regex("""0[235789]\d{8,9}"""))) {
                        return vnLocal
                    }
                }
                // Số quốc tế nước ngoài: giữ nguyên dấu '+' (VD: +821012345678, +12025550192) để nhà mạng nhận diện gọi quốc tế
                return intlNumber
            }
        }

        // 2. Quét số nội địa Việt Nam (10 số di động đầu 03/05/07/08/09 hoặc 11 số cố định/nhà hàng đầu 02)
        val domesticRegex = Regex("""(?<!\d)0[\d\s().-]{8,14}(?!\d)""")
        for (match in domesticRegex.findAll(normalized)) {
            val digits = match.value.filter { it.isDigit() }
            // Di động VN 10 chữ số
            if (digits.length == 10 && digits.matches(Regex("""0[35789]\d{8}"""))) {
                return digits
            }
            // Cố định VN 11 chữ số (024, 028...)
            if (digits.length == 11 && digits.matches(Regex("""02\d{9}"""))) {
                return digits
            }
        }

        // 3. Dự phòng trường hợp số VN có mã 84 nhưng thiếu dấu '+' (Ví dụ: 84901234567)
        val vnNoPlusRegex = Regex("""(?<!\d)84[\d\s().-]{9,13}(?!\d)""")
        for (match in vnNoPlusRegex.findAll(normalized)) {
            val digits = match.value.filter { it.isDigit() }
            if (digits.startsWith("84") && digits.length in 11..12) {
                val vnLocal = "0" + digits.substring(2)
                if (vnLocal.matches(Regex("""0[235789]\d{8,9}"""))) {
                    return vnLocal
                }
            }
        }

        return null
    }

    fun openDialer(number: String) {
        vibrateOnSuccess()
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            onStatus(getString("dialer_not_found", emptyArray()))
        }
    }

    private fun vibrateOnSuccess() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(180, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(180)
            }
        } catch (_: Exception) {}
    }

    fun resetHistory() {
        lastCandidate = ""
        consecutiveFrames = 0
        lastLaunchedNumber = ""
        lastLaunchTime = 0L
        nullCandidateCount = 0
    }

    fun reset() {
        resetHistory()
    }

    fun close() {
        recognizer.close()
    }
}
