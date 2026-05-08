package vn.aplus.smart.auth

import android.app.Application
import androidx.biometric.BiometricManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import vn.aplus.smart.data.SessionStore

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionStore = SessionStore(application.applicationContext)
    private val repository: AuthRepository = MockAuthRepository()
    private val _uiState = MutableStateFlow(AuthUiState(biometricSupported = isBiometricReady()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    private var activeJob: Job? = null

    init {
        viewModelScope.launch {
            val session = sessionStore.sessionFlow.first()
            _uiState.value = _uiState.value.copy(
                currentScreen = if (session == null) AuthScreen.Login else AuthScreen.Home,
                session = session,
                language = session?.language ?: "vi"
            )
        }
    }

    fun toggleLanguage() {
        val newLanguage = if (_uiState.value.language == "vi") "en" else "vi"
        _uiState.value = _uiState.value.copy(language = newLanguage, globalMessage = null)
        viewModelScope.launch { sessionStore.saveLanguage(newLanguage) }
    }

    fun goToLogin() { _uiState.value = _uiState.value.copy(currentScreen = AuthScreen.Login, globalMessage = null, isLoading = false) }
    fun goToRegister() { _uiState.value = _uiState.value.copy(currentScreen = AuthScreen.Register, globalMessage = null, isLoading = false) }
    fun goToForgot() { _uiState.value = _uiState.value.copy(currentScreen = AuthScreen.Forgot, forgotStep = ForgotStep.RequestAccount, globalMessage = null, isLoading = false) }

    fun updateLoginAccount(value: String) {
        _uiState.value = _uiState.value.copy(loginAccount = value, loginAccountError = validateAccount(value, required = true), globalMessage = null)
    }

    fun updateLoginPassword(value: String) {
        _uiState.value = _uiState.value.copy(loginPassword = value, loginPasswordError = validatePassword(value), globalMessage = null)
    }

    fun login() = runSingle {
        val state = _uiState.value
        val accountError = validateAccount(state.loginAccount, required = true)
        val passwordError = validatePassword(state.loginPassword)
        if (accountError != null || passwordError != null) {
            _uiState.value = state.copy(loginAccountError = accountError, loginPasswordError = passwordError)
            return@runSingle
        }
        _uiState.value = state.copy(isLoading = true, globalMessage = null)
        repository.login(state.loginAccount, state.loginPassword, state.language)
            .onSuccess { session ->
                sessionStore.saveSession(session)
                _uiState.value = _uiState.value.copy(isLoading = false, session = session, currentScreen = AuthScreen.Home)
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, globalMessage = mapAuthError(error)) }
    }

    fun biometricLogin() = runSingle {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, globalMessage = null)
        repository.biometricLogin(state.language)
            .onSuccess { session ->
                sessionStore.saveSession(session)
                _uiState.value = _uiState.value.copy(isLoading = false, session = session, currentScreen = AuthScreen.Home)
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, globalMessage = mapAuthError(error)) }
    }

    fun updateRegisterName(value: String) {
        _uiState.value = _uiState.value.copy(registerName = value, registerNameError = if (value.trim().length < 2) t("Tên tối thiểu 2 ký tự", "Name must have at least 2 characters") else null, globalMessage = null)
    }

    fun updateRegisterPhone(value: String) {
        _uiState.value = _uiState.value.copy(registerPhone = value, registerPhoneError = validatePhone(value), globalMessage = null)
    }

    fun updateRegisterEmail(value: String) {
        _uiState.value = _uiState.value.copy(registerEmail = value, registerEmailError = validateEmailOptional(value), globalMessage = null)
    }

    fun updateRegisterPassword(value: String) {
        val confirmError = if (_uiState.value.registerConfirmPassword.isNotBlank() && _uiState.value.registerConfirmPassword != value) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null
        _uiState.value = _uiState.value.copy(registerPassword = value, registerPasswordError = validatePassword(value), registerConfirmPasswordError = confirmError, globalMessage = null)
    }

    fun updateRegisterConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(registerConfirmPassword = value, registerConfirmPasswordError = if (value != _uiState.value.registerPassword) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null, globalMessage = null)
    }

    fun toggleTerms() {
        val accepted = !_uiState.value.registerAcceptedTerms
        _uiState.value = _uiState.value.copy(registerAcceptedTerms = accepted, registerTermsError = if (accepted) null else t("Bạn cần đồng ý điều khoản", "You need to accept the terms"), globalMessage = null)
    }

    fun register() = runSingle {
        val state = _uiState.value
        val nameError = if (state.registerName.trim().length < 2) t("Tên tối thiểu 2 ký tự", "Name must have at least 2 characters") else null
        val phoneError = validatePhone(state.registerPhone)
        val emailError = validateEmailOptional(state.registerEmail)
        val passError = validatePassword(state.registerPassword)
        val confirmError = if (state.registerConfirmPassword != state.registerPassword) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null
        val termsError = if (!state.registerAcceptedTerms) t("Bạn cần đồng ý điều khoản", "You need to accept the terms") else null
        if (listOf(nameError, phoneError, emailError, passError, confirmError, termsError).any { it != null }) {
            _uiState.value = state.copy(
                registerNameError = nameError,
                registerPhoneError = phoneError,
                registerEmailError = emailError,
                registerPasswordError = passError,
                registerConfirmPasswordError = confirmError,
                registerTermsError = termsError
            )
            return@runSingle
        }
        _uiState.value = state.copy(isLoading = true, globalMessage = null)
        repository.register(state.registerName, state.registerPhone, state.registerEmail, state.registerPassword, state.language)
            .onSuccess { session ->
                sessionStore.saveSession(session)
                _uiState.value = _uiState.value.copy(isLoading = false, session = session, currentScreen = AuthScreen.Home)
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, globalMessage = mapAuthError(error)) }
    }

    fun updateForgotAccount(value: String) {
        _uiState.value = _uiState.value.copy(forgotAccount = value, forgotAccountError = validateAccount(value, required = true), globalMessage = null)
    }

    fun updateForgotOtp(value: String) {
        _uiState.value = _uiState.value.copy(forgotOtp = value, forgotOtpError = if (value.filter(Char::isDigit).length < 6) t("OTP gồm 6 số", "OTP must have 6 digits") else null, globalMessage = null)
    }

    fun updateForgotNewPassword(value: String) {
        val confirmError = if (_uiState.value.forgotConfirmPassword.isNotBlank() && _uiState.value.forgotConfirmPassword != value) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null
        _uiState.value = _uiState.value.copy(forgotNewPassword = value, forgotNewPasswordError = validatePassword(value), forgotConfirmPasswordError = confirmError, globalMessage = null)
    }

    fun updateForgotConfirmPassword(value: String) {
        _uiState.value = _uiState.value.copy(forgotConfirmPassword = value, forgotConfirmPasswordError = if (value != _uiState.value.forgotNewPassword) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null, globalMessage = null)
    }

    fun requestOtp() = runSingle {
        val state = _uiState.value
        val accountError = validateAccount(state.forgotAccount, required = true)
        if (accountError != null) {
            _uiState.value = state.copy(forgotAccountError = accountError)
            return@runSingle
        }
        _uiState.value = state.copy(isLoading = true, globalMessage = null)
        repository.requestPasswordOtp(state.forgotAccount)
            .onSuccess { otp ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    forgotStep = ForgotStep.VerifyOtp,
                    forgotOtp = otp,
                    globalMessage = t("OTP mock: $otp", "Mock OTP: $otp")
                )
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, globalMessage = mapAuthError(error)) }
    }

    fun resetPassword() = runSingle {
        val state = _uiState.value
        val otpError = if (state.forgotOtp.filter(Char::isDigit).length < 6) t("OTP gồm 6 số", "OTP must have 6 digits") else null
        val passError = validatePassword(state.forgotNewPassword)
        val confirmError = if (state.forgotConfirmPassword != state.forgotNewPassword) t("Mật khẩu nhập lại chưa khớp", "Password confirmation does not match") else null
        if (otpError != null || passError != null || confirmError != null) {
            _uiState.value = state.copy(forgotOtpError = otpError, forgotNewPasswordError = passError, forgotConfirmPasswordError = confirmError)
            return@runSingle
        }
        _uiState.value = state.copy(isLoading = true, globalMessage = null)
        repository.resetPassword(state.forgotAccount, state.forgotOtp, state.forgotNewPassword)
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentScreen = AuthScreen.Login,
                    loginAccount = state.forgotAccount,
                    loginPassword = "",
                    forgotStep = ForgotStep.RequestAccount,
                    forgotOtp = "",
                    forgotNewPassword = "",
                    forgotConfirmPassword = "",
                    globalMessage = t("Đặt lại mật khẩu thành công", "Password has been reset")
                )
            }
            .onFailure { error -> _uiState.value = _uiState.value.copy(isLoading = false, globalMessage = mapAuthError(error)) }
    }

    fun logout() = runSingle {
        val language = _uiState.value.language
        sessionStore.clearSessionKeepLanguage(language)
        _uiState.value = _uiState.value.copy(session = null, currentScreen = AuthScreen.Login, globalMessage = t("Đã đăng xuất", "Signed out"))
    }

    fun clearMessage() { _uiState.value = _uiState.value.copy(globalMessage = null) }

    private fun runSingle(block: suspend () -> Unit) {
        if (_uiState.value.isLoading) return
        activeJob?.cancel()
        activeJob = viewModelScope.launch { block() }
    }

    private fun validateAccount(value: String, required: Boolean): String? {
        if (value.isBlank()) return if (required) t("Vui lòng nhập email hoặc số điện thoại", "Enter email or phone number") else null
        val cleaned = value.trim()
        if (cleaned.contains("@")) return validateEmailRequired(cleaned)
        return validatePhone(cleaned)
    }

    private fun validateEmailRequired(value: String): String? {
        val ok = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(value.trim())
        return if (ok) null else t("Email chưa đúng định dạng", "Email format is invalid")
    }

    private fun validateEmailOptional(value: String): String? = if (value.isBlank()) null else validateEmailRequired(value)

    private fun validatePhone(value: String): String? {
        val digits = value.filter(Char::isDigit)
        return if (digits.length >= 9) null else t("Số điện thoại chưa hợp lệ", "Phone number is invalid")
    }

    private fun validatePassword(value: String): String? = if (value.length >= 6) null else t("Mật khẩu tối thiểu 6 ký tự", "Password must have at least 6 characters")

    private fun mapAuthError(error: Throwable): String {
        return when (error.message) {
            "ACCOUNT_NOT_FOUND" -> t("Tài khoản không tồn tại", "Account does not exist")
            "WRONG_PASSWORD" -> t("Mật khẩu chưa đúng", "Password is incorrect")
            "ACCOUNT_EXISTS" -> t("Tài khoản đã tồn tại", "Account already exists")
            "OTP_WRONG" -> t("OTP chưa đúng", "OTP is incorrect")
            "OTP_EXPIRED" -> t("OTP đã hết hạn", "OTP has expired")
            "OTP_NOT_REQUESTED" -> t("Bạn cần gửi OTP trước", "Please request OTP first")
            else -> t("Có lỗi xảy ra, vui lòng thử lại", "Something went wrong, please try again")
        }
    }

    private fun t(vi: String, en: String): String = if (_uiState.value.language == "vi") vi else en

    private fun isBiometricReady(): Boolean {
        return try {
            BiometricManager.from(getApplication()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
        } catch (_: Throwable) {
            false
        }
    }
}
