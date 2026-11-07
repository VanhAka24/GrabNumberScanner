package com.example.grabnumberscanner

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    VI("vi", "Tiếng Việt", "🇻🇳"),
    EN("en", "English", "🇺🇸"),
    ZH("zh", "中文", "🇨🇳"),
    RU("ru", "Русский", "🇷🇺"),
    FR("fr", "Français", "🇫🇷"),
    KO("ko", "한국어", "🇰🇷"),
    JA("ja", "日本語", "🇯🇵")
}

object LocaleStrings {
    fun get(lang: AppLanguage, key: String, vararg args: Any): String {
        val map = when (lang) {
            AppLanguage.VI -> mapOf(
                "app_title" to "Grab Number Scanner",
                "no_number" to "Chưa nhận diện số",
                "init_cam" to "Đang khởi tạo camera...",
                "scan_cont" to "Đang quét liên tục...",
                "paused" to "Đã tạm dừng.",
                "ready" to "Sẵn sàng quét số...",
                "searching" to "Đang tìm số điện thoại...",
                "found" to "Đã thấy %s — xác nhận %d/2",
                "opening" to "Đang mở cuộc gọi...",
                "already_opened" to "Đã nhận diện: %s (chạm 📞 để gọi lại)",
                "note" to "Hướng camera vào số để tự động gọi",
                "permission_needed" to "Cần cấp quyền camera để quét số.",
                "cannot_start_cam" to "Không khởi động được camera: %s",
                "ocr_failed" to "Không đọc được chữ. Hãy giữ camera ổn định.",
                "dialer_not_found" to "Không tìm thấy ứng dụng gọi điện.",
                "dialog_title" to "🌐 Chọn ngôn ngữ",
                "dialog_cancel" to "Hủy"
            )
            AppLanguage.EN -> mapOf(
                "app_title" to "Grab Number Scanner",
                "no_number" to "No number detected",
                "init_cam" to "Initializing camera...",
                "scan_cont" to "Scanning continuously...",
                "paused" to "Paused.",
                "ready" to "Ready to scan...",
                "searching" to "Looking for phone number...",
                "found" to "Found %s — confirming %d/2",
                "opening" to "Opening dialer...",
                "already_opened" to "Detected: %s (tap 📞 to call again)",
                "note" to "Point camera at number to auto-call",
                "permission_needed" to "Camera permission required.",
                "cannot_start_cam" to "Failed to start camera: %s",
                "ocr_failed" to "Cannot read text. Hold camera steady.",
                "dialer_not_found" to "No dialer app found.",
                "dialog_title" to "🌐 Select Language",
                "dialog_cancel" to "Cancel"
            )
            AppLanguage.ZH -> mapOf(
                "app_title" to "Grab号码扫描器",
                "no_number" to "未检测到号码",
                "init_cam" to "正在初始化相机...",
                "scan_cont" to "正在连续扫描...",
                "paused" to "已暂停。",
                "ready" to "准备扫描号码...",
                "searching" to "正在查找电话号码...",
                "found" to "已发现 %s — 确认中 %d/2",
                "opening" to "正在打开拨号盘...",
                "already_opened" to "已识别: %s (点击 📞 重新拨打)",
                "note" to "对准号码即可自动拨打",
                "permission_needed" to "需要相机权限以扫描号码。",
                "cannot_start_cam" to "无法启动相机: %s",
                "ocr_failed" to "无法识别文字，请保持相机稳定。",
                "dialer_not_found" to "未找到拨号应用。",
                "dialog_title" to "🌐 选择语言",
                "dialog_cancel" to "取消"
            )
            AppLanguage.RU -> mapOf(
                "app_title" to "Сканер номеров Grab",
                "no_number" to "Номер не найден",
                "init_cam" to "Инициализация камеры...",
                "scan_cont" to "Непрерывное сканирование...",
                "paused" to "Пауза.",
                "ready" to "Готов к сканированию...",
                "searching" to "Поиск номера телефона...",
                "found" to "Найден %s — подтверждение %d/2",
                "opening" to "Открытие звонка...",
                "already_opened" to "Распознан: %s (нажмите 📞 для звонка)",
                "note" to "Наведите камеру на номер для звонка",
                "permission_needed" to "Требуется разрешение на камеру.",
                "cannot_start_cam" to "Не удалось запустить камеру: %s",
                "ocr_failed" to "Не удается прочесть текст. Держите камеру неподвижно.",
                "dialer_not_found" to "Приложение для звонков не найдено.",
                "dialog_title" to "🌐 Выберите язык",
                "dialog_cancel" to "Отмена"
            )
            AppLanguage.FR -> mapOf(
                "app_title" to "Scanner de numéros Grab",
                "no_number" to "Aucun numéro",
                "init_cam" to "Initialisation de la caméra...",
                "scan_cont" to "Scan en continu...",
                "paused" to "En pause.",
                "ready" to "Prêt à scanner...",
                "searching" to "Recherche de numéro...",
                "found" to "Trouvé %s — confirmation %d/2",
                "opening" to "Ouverture de l'appel...",
                "already_opened" to "Détecté: %s (appuyez sur 📞 pour rappeler)",
                "note" to "Pointez la caméra sur le numéro pour appeler",
                "permission_needed" to "Autorisation de la caméra requise.",
                "cannot_start_cam" to "Échec du démarrage de la caméra: %s",
                "ocr_failed" to "Impossible de lire le texte. Gardez la caméra stable.",
                "dialer_not_found" to "Aucune application d'appel trouvée.",
                "dialog_title" to "🌐 Choisir la langue",
                "dialog_cancel" to "Annuler"
            )
            AppLanguage.KO -> mapOf(
                "app_title" to "Grab 번호 스캐너",
                "no_number" to "번호 미감지",
                "init_cam" to "카메라 초기화 중...",
                "scan_cont" to "연속 스캔 중...",
                "paused" to "일시 중지됨.",
                "ready" to "스캔 준비 완료...",
                "searching" to "전화번호 찾는 중...",
                "found" to "%s 발견 — 확인 중 %d/2",
                "opening" to "통화 화면 여는 중...",
                "already_opened" to "인식됨: %s (다시 걸려면 📞 터치)",
                "note" to "번호에 카메라를 비추면 자동 연결",
                "permission_needed" to "카메라 권한이 필요합니다.",
                "cannot_start_cam" to "카메라를 시작할 수 없습니다: %s",
                "ocr_failed" to "글자를 읽을 수 없습니다. 카메라를 고정해 주세요.",
                "dialer_not_found" to "전화 앱을 찾을 수 없습니다.",
                "dialog_title" to "🌐 언어 선택",
                "dialog_cancel" to "취소"
            )
            AppLanguage.JA -> mapOf(
                "app_title" to "Grab番号スキャナー",
                "no_number" to "番号未検出",
                "init_cam" to "カメラ初期化中...",
                "scan_cont" to "スキャン中...",
                "paused" to "一時停止中。",
                "ready" to "スキャン準備完了...",
                "searching" to "電話番号を探しています...",
                "found" to "%s を検出 — 確認中 %d/2",
                "opening" to "発信画面を開いています...",
                "already_opened" to "検出: %s (📞で再発信)",
                "note" to "番号にカメラを向けると自動発信",
                "permission_needed" to "カメラの権限が必要です。",
                "cannot_start_cam" to "カメラを起動できません: %s",
                "ocr_failed" to "文字を読み取れません。カメラを静止させてください。",
                "dialer_not_found" to "通話アプリが見つかりません。",
                "dialog_title" to "🌐 言語を選択",
                "dialog_cancel" to "キャンセル"
            )
        }
        val template = map[key] ?: key
        return if (args.isNotEmpty()) {
            try { String.format(template, *args) } catch (_: Exception) { template }
        } else template
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var titleView: TextView
    private lateinit var langButton: TextView
    private lateinit var previewView: PreviewView
    private lateinit var statusText: TextView
    private lateinit var numberText: TextView
    private lateinit var callButton: ImageView
    private lateinit var scanButton: ImageView
    private lateinit var flashButton: ImageView
    private lateinit var noteText: TextView

    private lateinit var analyzerExecutor: ExecutorService
    private lateinit var detector: PhoneNumberDetector

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var scanning = true
    private var isFlashOn = false
    private var currentDetectedNumber: String? = null
    private var currentLanguage = AppLanguage.VI

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else statusText.text = getStringRes("permission_needed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadSavedLanguage()
        analyzerExecutor = Executors.newSingleThreadExecutor()
        detector = PhoneNumberDetector(
            context = this,
            onNumberFound = { number ->
                runOnUiThread {
                    currentDetectedNumber = number
                    numberText.text = number
                    callButton.visibility = View.VISIBLE
                    statusText.text = getStringRes("opening")
                }
            },
            onStatus = { message ->
                runOnUiThread { statusText.text = message }
            },
            getString = { key, args ->
                getStringRes(key, *args)
            }
        )
        buildUi()
        ensureCameraPermission()
    }

    private fun loadSavedLanguage() {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val code = prefs.getString("selected_language", "vi") ?: "vi"
        currentLanguage = AppLanguage.values().firstOrNull { it.code == code } ?: AppLanguage.VI
    }

    private fun saveLanguage(lang: AppLanguage) {
        val prefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit().putString("selected_language", lang.code).apply()
        currentLanguage = lang
        applyLanguage()
    }

    private fun getStringRes(key: String, vararg args: Any): String {
        return LocaleStrings.get(currentLanguage, key, *args)
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(44), dp(16), dp(16))
            setBackgroundColor(0xFF101820.toInt())
        }

        // Tự động co giãn theo thanh trạng thái (giờ, pin) và thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                dp(16),
                maxOf(systemBars.top + dp(10), dp(42)),
                dp(16),
                maxOf(systemBars.bottom + dp(8), dp(16))
            )
            insets
        }

        // Top Bar: Tiêu đề + Nút chuyển ngôn ngữ
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        titleView = TextView(this).apply {
            textSize = 20f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }
        topBar.addView(titleView, LinearLayout.LayoutParams(0, dp(44), 1f))

        langButton = TextView(this).apply {
            textSize = 13f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            includeFontPadding = false
            val bg = GradientDrawable().apply {
                setColor(0xFF263238.toInt())
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), 0xFF607D8B.toInt())
            }
            background = bg
            setPadding(dp(14), dp(6), dp(14), dp(6))
            isClickable = true
            isFocusable = true
            setOnClickListener { showLanguageDialog() }
        }
        topBar.addView(langButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(36)))

        root.addView(topBar, LinearLayout.LayoutParams(-1, dp(46)))

        // Camera Preview
        previewView = PreviewView(this).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
        root.addView(previewView, LinearLayout.LayoutParams(-1, 0, 1f).apply {
            topMargin = dp(8)
            bottomMargin = dp(8)
        })

        // Vùng hiển thị số và nút gọi hình chữ nhật bo góc
        val numberAndCallRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }

        numberText = TextView(this).apply {
            textSize = 21f
            setTextColor(0xFF66E39A.toInt())
            gravity = Gravity.CENTER
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setOnClickListener {
                currentDetectedNumber?.let { num -> detector.openDialer(num) }
            }
        }
        numberAndCallRow.addView(numberText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        // Nút gọi điện hình chữ nhật bo góc màu xanh tươi - icon điện thoại màu TRẮNG tinh
        callButton = ImageView(this).apply {
            setImageResource(R.drawable.ic_call_white)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            isClickable = true
            isFocusable = true
            visibility = View.INVISIBLE
            val callBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(14).toFloat()
                colors = intArrayOf(0xFF4CD964.toInt(), 0xFF28CD41.toInt())
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }
            background = callBg
            setOnClickListener {
                currentDetectedNumber?.let { num -> detector.openDialer(num) }
            }
        }
        numberAndCallRow.addView(callButton, LinearLayout.LayoutParams(dp(62), dp(50)).apply {
            marginStart = dp(10)
        })

        root.addView(numberAndCallRow, LinearLayout.LayoutParams(-1, dp(60)))

        // Trạng thái quét
        statusText = TextView(this).apply {
            textSize = 13.5f
            gravity = Gravity.CENTER
            setTextColor(0xFFCFD8DC.toInt())
        }
        root.addView(statusText, LinearLayout.LayoutParams(-1, dp(34)))

        // Hàng nút điều khiển cùng hàng (Nút Pause/Tiếp tục và Nút Flash) - Vector sắc nét, căn giữa tuyệt đối
        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        // Nút Dừng/Quét (2 gạch dọc trắng trên nền đỏ, không có ô viền vàng)
        scanButton = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (camera == null) {
                    ensureCameraPermission()
                    return@setOnClickListener
                }
                scanning = !scanning
                updateScanButtonVisual()
                if (scanning) {
                    detector.resetHistory()
                    statusText.text = getStringRes("scan_cont")
                } else {
                    statusText.text = getStringRes("paused")
                }
            }
        }
        buttonRow.addView(scanButton, LinearLayout.LayoutParams(dp(72), dp(50)).apply {
            marginEnd = dp(16)
        })

        // Nút Flash cùng hàng
        flashButton = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            isClickable = true
            isFocusable = true
            setOnClickListener {
                toggleFlash()
            }
        }
        buttonRow.addView(flashButton, LinearLayout.LayoutParams(dp(72), dp(50)).apply {
            marginStart = dp(16)
        })

        root.addView(buttonRow, LinearLayout.LayoutParams(-1, dp(62)).apply {
            topMargin = dp(4)
        })

        // Dòng chú thích ngắn gọn súc tích ở dưới cùng
        noteText = TextView(this).apply {
            textSize = 12f
            gravity = Gravity.CENTER
            setTextColor(0xFF90A4AE.toInt())
        }
        root.addView(noteText, LinearLayout.LayoutParams(-1, dp(30)))

        setContentView(root)

        // Cập nhật giao diện sau khi tất cả views đã được khởi tạo an toàn
        updateScanButtonVisual()
        updateFlashButtonVisual()
        applyLanguage()
    }

    private fun showLanguageDialog() {
        val languages = AppLanguage.values()
        val items = languages.map { "${it.flag}  ${it.displayName}" }.toTypedArray()
        val currentIndex = languages.indexOf(currentLanguage)

        AlertDialog.Builder(this)
            .setTitle(getStringRes("dialog_title"))
            .setSingleChoiceItems(items, currentIndex) { dialog, which ->
                saveLanguage(languages[which])
                dialog.dismiss()
            }
            .setNegativeButton(getStringRes("dialog_cancel"), null)
            .show()
    }

    private fun applyLanguage() {
        if (!::titleView.isInitialized || !::langButton.isInitialized ||
            !::noteText.isInitialized || !::numberText.isInitialized || !::statusText.isInitialized) {
            return
        }
        titleView.text = getStringRes("app_title")
        langButton.text = "${currentLanguage.flag} ${currentLanguage.code.uppercase()}"
        noteText.text = getStringRes("note")
        if (currentDetectedNumber == null) {
            numberText.text = getStringRes("no_number")
        }
        statusText.text = if (scanning) {
            if (currentDetectedNumber != null) {
                getStringRes("already_opened", currentDetectedNumber ?: "")
            } else {
                getStringRes("ready")
            }
        } else {
            getStringRes("paused")
        }
    }

    private fun updateScanButtonVisual() {
        if (!::scanButton.isInitialized) return
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(14).toFloat()
            if (scanning) {
                // Đang quét -> Nền ĐỎ (Ảnh 2)
                colors = intArrayOf(0xFFEF5350.toInt(), 0xFFD32F2F.toInt())
                gradientType = GradientDrawable.LINEAR_GRADIENT
            } else {
                // Đang tạm dừng -> Nền XANH LÁ (Ảnh 3)
                colors = intArrayOf(0xFF4CD964.toInt(), 0xFF28CD41.toInt())
                gradientType = GradientDrawable.LINEAR_GRADIENT
            }
        }
        scanButton.background = bg
        scanButton.setImageResource(if (scanning) R.drawable.ic_pause_white else R.drawable.ic_play_white)
    }

    private fun updateFlashButtonVisual() {
        if (!::flashButton.isInitialized) return
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(14).toFloat()
            if (isFlashOn) {
                // Bật Flash -> Màu VÀNG TIA SÉT (Ảnh 4)
                colors = intArrayOf(0xFFFFEE58.toInt(), 0xFFFFC107.toInt())
                gradientType = GradientDrawable.LINEAR_GRADIENT
            } else {
                // Tắt Flash -> Nền xám đậm bo góc
                setColor(0xFF263238.toInt())
                setStroke(dp(1), 0xFF455A64.toInt())
            }
        }
        flashButton.background = bg
        flashButton.setImageResource(if (isFlashOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off)
    }

    private fun toggleFlash() {
        isFlashOn = !isFlashOn
        if (camera == null) {
            ensureCameraPermission()
        } else {
            camera?.cameraControl?.enableTorch(isFlashOn)
        }
        updateFlashButtonVisual()
    }

    override fun onResume() {
        super.onResume()
        detector.resetHistory()
        if (scanning) {
            statusText.text = if (currentDetectedNumber != null) {
                getStringRes("already_opened", currentDetectedNumber ?: "")
            } else {
                getStringRes("ready")
            }
        }
    }

    private fun ensureCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            cameraProvider = provider
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(analyzerExecutor) { proxy ->
                if (scanning) {
                    detector.analyze(proxy)
                } else {
                    proxy.close()
                }
            }
            try {
                provider.unbindAll()
                camera = provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
                if (isFlashOn) {
                    camera?.cameraControl?.enableTorch(true)
                }
                updateScanButtonVisual()
                statusText.text = if (scanning) getStringRes("scan_cont") else getStringRes("paused")
            } catch (e: Exception) {
                statusText.text = getStringRes("cannot_start_cam", e.localizedMessage ?: "")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun stopCamera() {
        if (isFlashOn) {
            camera?.cameraControl?.enableTorch(false)
            isFlashOn = false
            updateFlashButtonVisual()
        }
        cameraProvider?.unbindAll()
        camera = null
        scanning = false
        updateScanButtonVisual()
        detector.reset()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        if (isFlashOn) {
            camera?.cameraControl?.enableTorch(false)
        }
        cameraProvider?.unbindAll()
        analyzerExecutor.shutdown()
        detector.close()
        super.onDestroy()
    }
}
