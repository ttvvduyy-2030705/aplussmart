package com.aplus.locknative.ui

import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.aplus.locknative.R

/**
 * UI31 exact mode.
 *
 * The 31 PNG files from Hihi.zip are treated as the visual source of truth so the Android app
 * matches the uploaded mockups pixel-for-pixel. Transparent native Compose hotspots are layered on
 * top to keep the app navigable and demo catalog functions from the smart-lock catalog.
 */
@Composable
fun AplusLockApp() {
    MaterialTheme {
        val context = LocalContext.current
        var route by rememberSaveable { mutableStateOf("login") }
        var lastKeyRoute by rememberSaveable { mutableStateOf("lock_detail") }
        var isLocked by rememberSaveable { mutableStateOf(true) }
        val logs = remember {
            mutableStateListOf(
                "15:22 • join mở khóa bằng APP • ID:900",
                "15:11 • Chủ nhà cấp mật khẩu tạm",
                "14:36 • Pin báo 88%"
            )
        }

        // Account test được ghim sẵn để mỗi lần build test chỉ cần bấm Đăng nhập.
        var loginAccount by rememberSaveable { mutableStateOf("admin@aplus.vn") }
        var loginPassword by rememberSaveable { mutableStateOf("123456") }
        var registerName by rememberSaveable { mutableStateOf("Aplus Demo") }
        var registerPhone by rememberSaveable { mutableStateOf("0900000001") }
        var registerEmail by rememberSaveable { mutableStateOf("owner@aplus.vn") }
        var registerPassword by rememberSaveable { mutableStateOf("123456") }
        var registerOtp by rememberSaveable { mutableStateOf("888888") }
        var forgotAccount by rememberSaveable { mutableStateOf("admin@aplus.vn") }
        var forgotOtp by rememberSaveable { mutableStateOf("888888") }

        fun show(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        fun go(next: String) {
            route = next
            if (keyRoutes.contains(next)) lastKeyRoute = next
        }

        fun loginNow(message: String = "Đăng nhập thành công") {
            if (loginAccount.isBlank() || loginPassword.isBlank()) {
                show("Vui lòng nhập tài khoản và mật khẩu")
                return
            }
            show(message)
            go("home_dashboard")
        }

        fun doPrimaryAction(spec: Ui31Screen) {
            val message = when (spec.route) {
                "add_password" -> "Đã tạo mật khẩu tạm mock"
                "add_fingerprint" -> "Bắt đầu ghi danh vân tay mock"
                "add_face" -> "Bắt đầu ghi danh khuôn mặt mock"
                "add_card" -> "Đang chờ quẹt thẻ mock"
                "add_remote_control" -> "Đang ghép remote mock"
                "phone_authorization" -> "Đã gửi lời mời cấp quyền điện thoại"
                "sub_admin" -> "Đã tạo admin phụ mock"
                "remote_unlock" -> {
                    isLocked = false
                    logs.add(0, "Vừa xong • Mở khóa từ xa qua backend mock")
                    "Đã gửi lệnh mở khóa từ xa"
                }
                "lock_transfer" -> "Đã tạo yêu cầu chuyển quyền khóa"
                "combination_unlock" -> "Đã lưu rule mở khóa kết hợp"
                "cycle_normally_open" -> "Đã lưu lịch luôn mở"
                "nfc_unlocking" -> "Đã kích hoạt NFC mock"
                "room_management" -> "Đã thêm phòng mock"
                "staff_tenant_management" -> "Đã thêm người dùng mock"
                "alarm_center" -> "Đã xử lý cảnh báo mock"
                "networking_gateway" -> "Đang quét Gateway/BLE/Wi-Fi mock"
                "device_management" -> "Đang đồng bộ thiết bị mock"
                "report_analytics" -> "Đã làm mới báo cáo"
                "apartment_hotel_pms" -> "Đã kiểm tra kết nối PMS mock"
                "profile_language" -> "Đã lưu hồ sơ/ngôn ngữ"
                else -> "Đã chạy chức năng ${spec.title}"
            }
            show(message)
        }

        val spec = ui31Screens.firstOrNull { it.route == route } ?: ui31Screens.first()
        Surface(color = Color.Black, modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = spec.imageRes),
                    contentDescription = spec.title,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                // Ảnh mock có status bar giả 15:23. App dùng status bar thật của máy,
                // nên lớp này che phần status giả trong ảnh mà không làm lệch layout.
                FakeStatusBarMask()
                AuthNativeLayer(
                    route = spec.route,
                    loginAccount = loginAccount,
                    onLoginAccountChange = { loginAccount = it },
                    loginPassword = loginPassword,
                    onLoginPasswordChange = { loginPassword = it },
                    registerName = registerName,
                    onRegisterNameChange = { registerName = it },
                    registerPhone = registerPhone,
                    onRegisterPhoneChange = { registerPhone = it },
                    registerEmail = registerEmail,
                    onRegisterEmailChange = { registerEmail = it },
                    registerPassword = registerPassword,
                    onRegisterPasswordChange = { registerPassword = it },
                    registerOtp = registerOtp,
                    onRegisterOtpChange = { registerOtp = it },
                    forgotAccount = forgotAccount,
                    onForgotAccountChange = { forgotAccount = it },
                    forgotOtp = forgotOtp,
                    onForgotOtpChange = { forgotOtp = it },
                )
                Ui31Hotspots(
                    route = spec.route,
                    locked = isLocked,
                    onNavigate = ::go,
                    onLogin = { loginNow() },
                    onRegister = {
                        if (registerName.isBlank() || registerPhone.isBlank() || registerEmail.isBlank() || registerPassword.isBlank()) {
                            show("Vui lòng nhập đủ thông tin đăng ký")
                        } else {
                            show("Tạo tài khoản thành công")
                            go("home_dashboard")
                        }
                    },
                    onForgotSubmit = {
                        if (forgotAccount.isBlank()) {
                            show("Vui lòng nhập email hoặc số điện thoại")
                        } else {
                            show("Đã gửi mã xác thực khôi phục")
                        }
                    },
                    onQuickLogin = { method ->
                        authenticateBiometricLogin(
                            context = context,
                            methodName = method,
                            onSuccess = { loginNow("Đăng nhập nhanh bằng $method") },
                            onFallback = { loginNow("Thiết bị chưa bật $method, dùng đăng nhập nhanh mock") }
                        )
                    },
                    onBack = {
                        route = when (spec.route) {
                            "register", "forgot_password" -> "login"
                            "lock_detail" -> "home_dashboard"
                            "add_key_menu", "settings", "more_hub" -> "lock_detail"
                            "unlock_records", "electricity_reporting", "lock_transfer", "combination_unlock", "cycle_normally_open", "nfc_unlocking", "room_management", "staff_tenant_management", "alarm_center", "networking_gateway", "device_management", "apartment_hotel_pms" -> "more_hub"
                            "report_analytics", "profile_language" -> "home_dashboard"
                            else -> "lock_detail"
                        }
                    },
                    onHome = { go("home_dashboard") },
                    onKey = { go(lastKeyRoute) },
                    onReports = { go("report_analytics") },
                    onMe = { go("profile_language") },
                    onToggleLock = {
                        isLocked = !isLocked
                        val text = if (isLocked) "Đã khóa lại" else "Đã mở khóa"
                        logs.add(0, "Vừa xong • $text bằng APP")
                        show(text)
                    },
                    onPrimary = { doPrimaryAction(spec) }
                )
            }
        }
    }
}

@Composable
private fun Ui31Hotspots(
    route: String,
    locked: Boolean,
    onNavigate: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onForgotSubmit: () -> Unit,
    onQuickLogin: (String) -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onKey: () -> Unit,
    onReports: () -> Unit,
    onMe: () -> Unit,
    onToggleLock: () -> Unit,
    onPrimary: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight

        if (route != "login" && route != "register" && route != "forgot_password") {
            // Bottom tabbar from the mockups: Nhà / Chìa khóa / Báo cáo / Tôi.
            Hit(w, h, 0.04f, 0.928f, 0.27f, 0.985f, onHome)
            Hit(w, h, 0.29f, 0.928f, 0.50f, 0.985f, onKey)
            Hit(w, h, 0.53f, 0.928f, 0.74f, 0.985f, onReports)
            Hit(w, h, 0.76f, 0.928f, 0.97f, 0.985f, onMe)
        }

        if (route != "login" && route != "home_dashboard") {
            Hit(w, h, 0.00f, 0.045f, 0.12f, 0.095f, onBack)
        }

        when (route) {
            "login" -> {
                // Quên mật khẩu / Đăng nhập / Face ID / Vân tay / Tạo tài khoản mới.
                Hit(w, h, 0.70f, 0.532f, 0.92f, 0.558f) { onNavigate("forgot_password") }
                Hit(w, h, 0.09f, 0.560f, 0.91f, 0.605f, onLogin)
                Hit(w, h, 0.09f, 0.610f, 0.49f, 0.646f) { onQuickLogin("Face ID") }
                Hit(w, h, 0.51f, 0.610f, 0.91f, 0.646f) { onQuickLogin("vân tay") }
                Hit(w, h, 0.31f, 0.646f, 0.75f, 0.675f) { onNavigate("register") }
            }
            "register" -> {
                Hit(w, h, 0.09f, 0.515f, 0.91f, 0.558f, onRegister)
                Hit(w, h, 0.09f, 0.560f, 0.91f, 0.592f) { onNavigate("login") }
            }
            "forgot_password" -> {
                Hit(w, h, 0.09f, 0.448f, 0.91f, 0.492f, onForgotSubmit)
            }
            "home_dashboard" -> {
                Hit(w, h, 0.09f, 0.225f, 0.38f, 0.265f) { onNavigate("lock_detail") }
                Hit(w, h, 0.78f, 0.365f, 0.95f, 0.395f) { onNavigate("networking_gateway") }
                Hit(w, h, 0.05f, 0.392f, 0.95f, 0.462f) { onNavigate("lock_detail") }
                Hit(w, h, 0.05f, 0.475f, 0.95f, 0.545f) { onNavigate("lock_detail") }
                Hit(w, h, 0.05f, 0.558f, 0.95f, 0.628f) { onNavigate("lock_detail") }
                // Quick actions grid.
                Hit(w, h, 0.05f, 0.668f, 0.34f, 0.728f) { onNavigate("add_key_menu") }
                Hit(w, h, 0.36f, 0.668f, 0.65f, 0.728f) { onNavigate("add_fingerprint") }
                Hit(w, h, 0.67f, 0.668f, 0.96f, 0.728f) { onNavigate("add_card") }
                Hit(w, h, 0.05f, 0.738f, 0.34f, 0.798f) { onNavigate("add_face") }
                Hit(w, h, 0.36f, 0.738f, 0.65f, 0.798f) { onNavigate("unlock_records") }
                Hit(w, h, 0.67f, 0.738f, 0.96f, 0.798f) { onNavigate("alarm_center") }
            }
            "lock_detail" -> {
                Hit(w, h, 0.30f, 0.122f, 0.70f, 0.284f, onToggleLock)
                Hit(w, h, 0.16f, 0.325f, 0.38f, 0.362f, onPrimary)
                Hit(w, h, 0.56f, 0.325f, 0.84f, 0.362f) { onNavigate("remote_unlock") }

                Hit(w, h, 0.07f, 0.386f, 0.24f, 0.475f) { onNavigate("add_key_menu") }
                Hit(w, h, 0.28f, 0.386f, 0.45f, 0.475f) { onNavigate("add_password") }
                Hit(w, h, 0.48f, 0.386f, 0.66f, 0.475f) { onNavigate("add_fingerprint") }
                Hit(w, h, 0.70f, 0.386f, 0.88f, 0.475f) { onNavigate("add_face") }
                Hit(w, h, 0.07f, 0.482f, 0.24f, 0.570f) { onNavigate("add_card") }
                Hit(w, h, 0.28f, 0.482f, 0.45f, 0.570f) { onNavigate("add_remote_control") }
                Hit(w, h, 0.48f, 0.482f, 0.66f, 0.570f) { onNavigate("phone_authorization") }
                Hit(w, h, 0.70f, 0.482f, 0.90f, 0.570f) { onNavigate("sub_admin") }
                Hit(w, h, 0.07f, 0.585f, 0.24f, 0.675f) { onNavigate("settings") }
                Hit(w, h, 0.28f, 0.585f, 0.45f, 0.675f) { onNavigate("more_hub") }
            }
            "add_key_menu" -> {
                val routes = listOf("add_password", "add_fingerprint", "add_card", "add_face", "add_remote_control", "phone_authorization", "sub_admin", "remote_unlock")
                routes.forEachIndexed { i, target ->
                    val top = 0.145f + i * 0.067f
                    Hit(w, h, 0.06f, top, 0.94f, top + 0.050f) { onNavigate(target) }
                }
            }
            "more_hub" -> {
                Hit(w, h, 0.06f, 0.168f, 0.32f, 0.230f) { onNavigate("settings") }
                Hit(w, h, 0.37f, 0.168f, 0.63f, 0.230f) { onNavigate("unlock_records") }
                Hit(w, h, 0.68f, 0.168f, 0.94f, 0.230f) { onNavigate("lock_transfer") }
                Hit(w, h, 0.06f, 0.248f, 0.32f, 0.310f) { onNavigate("electricity_reporting") }
                Hit(w, h, 0.37f, 0.248f, 0.63f, 0.310f) { onNavigate("alarm_center") }
                Hit(w, h, 0.68f, 0.248f, 0.94f, 0.310f) { onNavigate("networking_gateway") }
                Hit(w, h, 0.06f, 0.328f, 0.32f, 0.390f) { onNavigate("device_management") }
                Hit(w, h, 0.37f, 0.328f, 0.63f, 0.390f) { onNavigate("report_analytics") }
                Hit(w, h, 0.68f, 0.328f, 0.94f, 0.390f) { onNavigate("apartment_hotel_pms") }
            }
            "room_management" -> {
                Hit(w, h, 0.05f, 0.085f, 0.95f, 0.125f, onPrimary)
                listOf("staff_tenant_management", "lock_detail", "apartment_hotel_pms", "device_management", "report_analytics").forEachIndexed { i, target ->
                    val top = 0.186f + i * 0.057f
                    Hit(w, h, 0.06f, top, 0.94f, top + 0.042f) { onNavigate(target) }
                }
            }
            "alarm_center" -> {
                Hit(w, h, 0.07f, 0.090f, 0.48f, 0.125f, onPrimary)
                Hit(w, h, 0.52f, 0.090f, 0.93f, 0.125f) { onNavigate("settings") }
                Hit(w, h, 0.06f, 0.165f, 0.94f, 0.225f) { onNavigate("lock_detail") }
                Hit(w, h, 0.06f, 0.235f, 0.94f, 0.295f) { onNavigate("electricity_reporting") }
                Hit(w, h, 0.06f, 0.305f, 0.94f, 0.365f) { onNavigate("remote_unlock") }
                Hit(w, h, 0.06f, 0.375f, 0.94f, 0.435f) { onNavigate("device_management") }
            }
            "report_analytics" -> {
                Hit(w, h, 0.06f, 0.105f, 0.22f, 0.135f, onPrimary)
                Hit(w, h, 0.25f, 0.105f, 0.42f, 0.135f, onPrimary)
                Hit(w, h, 0.45f, 0.105f, 0.62f, 0.135f, onPrimary)
                Hit(w, h, 0.06f, 0.615f, 0.94f, 0.660f) { onNavigate("unlock_records") }
                Hit(w, h, 0.06f, 0.667f, 0.94f, 0.712f) { onNavigate("alarm_center") }
                Hit(w, h, 0.06f, 0.718f, 0.94f, 0.763f) { onNavigate("electricity_reporting") }
            }
            "profile_language" -> {
                Hit(w, h, 0.06f, 0.120f, 0.94f, 0.185f, onPrimary)
                Hit(w, h, 0.06f, 0.223f, 0.48f, 0.278f) { onNavigate("settings") }
                Hit(w, h, 0.52f, 0.223f, 0.94f, 0.278f) { onNavigate("phone_authorization") }
                Hit(w, h, 0.06f, 0.288f, 0.48f, 0.343f) { onNavigate("sub_admin") }
                Hit(w, h, 0.52f, 0.288f, 0.94f, 0.343f) { onNavigate("device_management") }
                Hit(w, h, 0.06f, 0.353f, 0.48f, 0.408f) { onNavigate("unlock_records") }
                Hit(w, h, 0.52f, 0.353f, 0.94f, 0.408f) { onNavigate("alarm_center") }
                Hit(w, h, 0.06f, 0.418f, 0.48f, 0.473f) { onNavigate("login") }
            }
            else -> {
                // Generic catalog feature screens: red primary button + list rows.
                Hit(w, h, 0.09f, 0.265f, 0.91f, 0.300f, onPrimary)
                Hit(w, h, 0.06f, 0.330f, 0.94f, 0.378f, onPrimary)
                Hit(w, h, 0.06f, 0.385f, 0.94f, 0.433f, onPrimary)
                Hit(w, h, 0.06f, 0.440f, 0.94f, 0.488f, onPrimary)
            }
        }
    }
}


@Composable
private fun FakeStatusBarMask() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF05060A), Color(0xFF170009), Color(0xFF350014))
                )
            )
    )
}

@Composable
private fun AuthNativeLayer(
    route: String,
    loginAccount: String,
    onLoginAccountChange: (String) -> Unit,
    loginPassword: String,
    onLoginPasswordChange: (String) -> Unit,
    registerName: String,
    onRegisterNameChange: (String) -> Unit,
    registerPhone: String,
    onRegisterPhoneChange: (String) -> Unit,
    registerEmail: String,
    onRegisterEmailChange: (String) -> Unit,
    registerPassword: String,
    onRegisterPasswordChange: (String) -> Unit,
    registerOtp: String,
    onRegisterOtpChange: (String) -> Unit,
    forgotAccount: String,
    onForgotAccountChange: (String) -> Unit,
    forgotOtp: String,
    onForgotOtpChange: (String) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        when (route) {
            "login" -> {
                // Ô nhập được dựng lại thành native input đẹp: dòng label nhỏ ở trên,
                // dòng giá trị nhập thật ở dưới. Không còn dán chữ đè thô lên placeholder ảnh.
                AuthField(
                    maxW = w,
                    maxH = h,
                    left = 0.095f,
                    top = 0.418f,
                    right = 0.905f,
                    bottom = 0.463f,
                    label = "Email / Số điện thoại",
                    value = loginAccount,
                    onValueChange = onLoginAccountChange,
                    keyboardType = KeyboardType.Email,
                    iconRes = R.drawable.ic_flaticon_phone,
                )
                AuthField(
                    maxW = w,
                    maxH = h,
                    left = 0.095f,
                    top = 0.478f,
                    right = 0.905f,
                    bottom = 0.524f,
                    label = "Mật khẩu",
                    value = loginPassword,
                    onValueChange = onLoginPasswordChange,
                    keyboardType = KeyboardType.Password,
                    password = false, // giữ 123456 nhìn thấy để build test bấm đăng nhập nhanh
                    iconRes = R.drawable.ic_flaticon_lock,
                )
            }
            "register" -> {
                AuthField(w, h, 0.095f, 0.210f, 0.905f, 0.253f, "Tên công ty / người dùng", registerName, onRegisterNameChange, KeyboardType.Text, iconRes = R.drawable.ic_flaticon_admin)
                AuthField(w, h, 0.095f, 0.270f, 0.905f, 0.313f, "Số điện thoại", registerPhone, onRegisterPhoneChange, KeyboardType.Phone, iconRes = R.drawable.ic_flaticon_phone)
                AuthField(w, h, 0.095f, 0.330f, 0.905f, 0.373f, "Email", registerEmail, onRegisterEmailChange, KeyboardType.Email, iconRes = R.drawable.ic_flaticon_phone)
                AuthField(w, h, 0.095f, 0.390f, 0.905f, 0.433f, "Mật khẩu", registerPassword, onRegisterPasswordChange, KeyboardType.Password, password = true, iconRes = R.drawable.ic_flaticon_lock)
                AuthField(w, h, 0.095f, 0.450f, 0.905f, 0.493f, "Mã xác thực", registerOtp, onRegisterOtpChange, KeyboardType.Number, iconRes = R.drawable.ic_flaticon_password)
            }
            "forgot_password" -> {
                AuthField(w, h, 0.095f, 0.328f, 0.905f, 0.372f, "Email / Số điện thoại", forgotAccount, onForgotAccountChange, KeyboardType.Email, iconRes = R.drawable.ic_flaticon_phone)
                AuthField(w, h, 0.095f, 0.388f, 0.905f, 0.432f, "Mã xác thực", forgotOtp, onForgotOtpChange, KeyboardType.Number, iconRes = R.drawable.ic_flaticon_password)
            }
        }
    }
}

@Composable
private fun AuthField(
    maxW: Dp,
    maxH: Dp,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    password: Boolean = false,
    @DrawableRes iconRes: Int? = null,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .offset(x = maxW * left, y = maxH * top)
            .size(width = maxW * (right - left), height = maxH * (bottom - top))
            // Che toàn bộ ô trắng trong ảnh gốc bằng ô nhập native màu xám/đen,
            // chữ trắng, bo góc và có viền để nhìn giống component app thật.
            .background(Color(0xFF2A2F3A), shape)
            .border(width = 1.dp, color = Color(0xFF525A68), shape = shape)
            .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color(0xFFE8ECF5),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = Color(0xFFB7BECC),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                    cursorBrush = Brush.verticalGradient(listOf(Color(0xFFF20D32), Color(0xFFF20D32))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

private fun authenticateBiometricLogin(
    context: Context,
    methodName: String,
    onSuccess: () -> Unit,
    onFallback: () -> Unit,
) {
    val activity = context.findFragmentActivity()
    if (activity == null) {
        onFallback()
        return
    }

    val biometricManager = BiometricManager.from(context)
    val canUseBiometric = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    if (canUseBiometric != BiometricManager.BIOMETRIC_SUCCESS) {
        Toast.makeText(context, "Thiết bị chưa sẵn sàng $methodName", Toast.LENGTH_SHORT).show()
        onFallback()
        return
    }

    val executor = ContextCompat.getMainExecutor(context)
    val prompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                    Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(context, "Xác thực không khớp", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Đăng nhập nhanh Aplus Lock")
        .setSubtitle("Xác thực bằng $methodName")
        .setNegativeButtonText("Hủy")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        .build()
    prompt.authenticate(promptInfo)
}

private fun Context.findFragmentActivity(): FragmentActivity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is FragmentActivity) return current
        current = current.baseContext
    }
    return null
}

@Composable
private fun Hit(
    maxW: Dp,
    maxH: Dp,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .offset(x = maxW * left, y = maxH * top)
            .size(width = maxW * (right - left), height = maxH * (bottom - top))
            .background(Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}

data class Ui31Screen(
    val index: Int,
    val route: String,
    val title: String,
    @DrawableRes val imageRes: Int,
)

private val keyRoutes = setOf(
    "lock_detail",
    "add_key_menu",
    "add_password",
    "password_management",
    "add_fingerprint",
    "add_face",
    "add_card",
    "add_remote_control",
    "phone_authorization",
    "sub_admin",
    "remote_unlock",
    "settings",
    "more_hub",
    "lock_transfer",
    "combination_unlock",
    "cycle_normally_open",
    "nfc_unlocking",
    "room_management",
    "staff_tenant_management",
    "alarm_center",
    "networking_gateway",
    "device_management",
    "apartment_hotel_pms",
)

val ui31Screens = listOf(
    Ui31Screen(0, "login", "Đăng nhập", R.drawable.screen_00_login),
    Ui31Screen(1, "register", "Tạo tài khoản", R.drawable.screen_01_register),
    Ui31Screen(2, "forgot_password", "Khôi phục mật khẩu", R.drawable.screen_02_forgot_password),
    Ui31Screen(3, "home_dashboard", "Nhà", R.drawable.screen_03_home_dashboard),
    Ui31Screen(4, "lock_detail", "Chìa khóa", R.drawable.screen_04_lock_detail),
    Ui31Screen(5, "add_key_menu", "Thêm phương thức", R.drawable.screen_05_add_key_menu),
    Ui31Screen(6, "add_password", "Tạo mật khẩu", R.drawable.screen_06_add_password),
    Ui31Screen(7, "password_management", "Quản lý mật khẩu", R.drawable.screen_07_password_management),
    Ui31Screen(8, "add_fingerprint", "Thêm vân tay", R.drawable.screen_08_add_fingerprint),
    Ui31Screen(9, "add_face", "Thêm khuôn mặt", R.drawable.screen_09_add_face),
    Ui31Screen(10, "add_card", "Thêm thẻ", R.drawable.screen_10_add_card),
    Ui31Screen(11, "add_remote_control", "Thêm remote", R.drawable.screen_11_add_remote_control),
    Ui31Screen(12, "phone_authorization", "Quyền điện thoại", R.drawable.screen_12_phone_authorization),
    Ui31Screen(13, "sub_admin", "Quản lý phụ", R.drawable.screen_13_sub_admin),
    Ui31Screen(14, "remote_unlock", "Mở khóa từ xa", R.drawable.screen_14_remote_unlock),
    Ui31Screen(15, "settings", "Cài đặt", R.drawable.screen_15_settings),
    Ui31Screen(16, "more_hub", "More", R.drawable.screen_16_more_hub),
    Ui31Screen(17, "unlock_records", "Lịch sử mở khóa", R.drawable.screen_17_unlock_records),
    Ui31Screen(18, "electricity_reporting", "Pin & điện năng", R.drawable.screen_18_electricity_reporting),
    Ui31Screen(19, "lock_transfer", "Chuyển khóa", R.drawable.screen_19_lock_transfer),
    Ui31Screen(20, "combination_unlock", "Mở khóa kết hợp", R.drawable.screen_20_combination_unlock),
    Ui31Screen(21, "cycle_normally_open", "Lịch luôn mở", R.drawable.screen_21_cycle_normally_open),
    Ui31Screen(22, "nfc_unlocking", "NFC mở khóa", R.drawable.screen_22_nfc_unlocking),
    Ui31Screen(23, "room_management", "Quản lý phòng", R.drawable.screen_23_room_management),
    Ui31Screen(24, "staff_tenant_management", "Nhân sự & khách thuê", R.drawable.screen_24_staff_tenant_management),
    Ui31Screen(25, "alarm_center", "Trung tâm cảnh báo", R.drawable.screen_25_alarm_center),
    Ui31Screen(26, "networking_gateway", "Gateway & kết nối", R.drawable.screen_26_networking_gateway),
    Ui31Screen(27, "device_management", "Quản lý thiết bị", R.drawable.screen_27_device_management),
    Ui31Screen(28, "report_analytics", "Báo cáo dữ liệu", R.drawable.screen_28_report_analytics),
    Ui31Screen(29, "apartment_hotel_pms", "Apartment / Hotel PMS", R.drawable.screen_29_apartment_hotel_pms),
    Ui31Screen(30, "profile_language", "Tài khoản & ngôn ngữ", R.drawable.screen_30_profile_language),
)
