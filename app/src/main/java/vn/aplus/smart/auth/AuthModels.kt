package vn.aplus.smart.auth

enum class AuthScreen {
    Splash,
    Login,
    Register,
    Forgot,
    Home
}

enum class ForgotStep {
    RequestAccount,
    VerifyOtp
}

data class UserSession(
    val userId: String,
    val token: String,
    val name: String,
    val role: String,
    val language: String,
    val lastLoginAt: Long
)

data class MockUser(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    var password: String,
    val role: String = "owner"
)

data class AuthUiState(
    val currentScreen: AuthScreen = AuthScreen.Splash,
    val language: String = "vi",
    val session: UserSession? = null,
    val isLoading: Boolean = false,
    val globalMessage: String? = null,
    val loginAccount: String = "admin@aplus.vn",
    val loginPassword: String = "123456",
    val loginAccountError: String? = null,
    val loginPasswordError: String? = null,
    val registerName: String = "Aplus Interior",
    val registerPhone: String = "0912345678",
    val registerEmail: String = "",
    val registerPassword: String = "123456",
    val registerConfirmPassword: String = "123456",
    val registerAcceptedTerms: Boolean = true,
    val registerNameError: String? = null,
    val registerPhoneError: String? = null,
    val registerEmailError: String? = null,
    val registerPasswordError: String? = null,
    val registerConfirmPasswordError: String? = null,
    val registerTermsError: String? = null,
    val forgotStep: ForgotStep = ForgotStep.RequestAccount,
    val forgotAccount: String = "admin@aplus.vn",
    val forgotOtp: String = "",
    val forgotNewPassword: String = "",
    val forgotConfirmPassword: String = "",
    val forgotAccountError: String? = null,
    val forgotOtpError: String? = null,
    val forgotNewPasswordError: String? = null,
    val forgotConfirmPasswordError: String? = null,
    val biometricSupported: Boolean = false
)
