package vn.aplus.smart.auth

import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(account: String, password: String, language: String): Result<UserSession>
    suspend fun register(name: String, phone: String, email: String, password: String, language: String): Result<UserSession>
    suspend fun requestPasswordOtp(account: String): Result<String>
    suspend fun resetPassword(account: String, otp: String, newPassword: String): Result<Unit>
    suspend fun biometricLogin(language: String): Result<UserSession>
}

class MockAuthRepository : AuthRepository {
    private val users = mutableListOf(
        MockUser(
            id = "user_owner_001",
            name = "Aplus Interior",
            phone = "0912345678",
            email = "admin@aplus.vn",
            password = "123456",
            role = "owner"
        ),
        MockUser(
            id = "user_demo_phone",
            name = "Aplus Demo",
            phone = "0987654321",
            email = "demo@aplus.vn",
            password = "123456",
            role = "admin"
        )
    )

    private var lastOtpAccount: String? = null
    private var lastOtpCode: String = "123456"
    private var lastOtpExpiresAt: Long = 0L

    override suspend fun login(account: String, password: String, language: String): Result<UserSession> {
        delay(520)
        val user = findUser(account) ?: return Result.failure(IllegalArgumentException("ACCOUNT_NOT_FOUND"))
        if (user.password != password) return Result.failure(IllegalArgumentException("WRONG_PASSWORD"))
        return Result.success(user.toSession(language))
    }

    override suspend fun register(
        name: String,
        phone: String,
        email: String,
        password: String,
        language: String
    ): Result<UserSession> {
        delay(620)
        val normalizedPhone = normalizePhone(phone)
        val normalizedEmail = email.trim().lowercase()
        val exists = users.any { normalizePhone(it.phone) == normalizedPhone || (normalizedEmail.isNotBlank() && it.email.lowercase() == normalizedEmail) }
        if (exists) return Result.failure(IllegalArgumentException("ACCOUNT_EXISTS"))
        val user = MockUser(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            phone = normalizedPhone,
            email = normalizedEmail,
            password = password,
            role = "owner"
        )
        users += user
        return Result.success(user.toSession(language))
    }

    override suspend fun requestPasswordOtp(account: String): Result<String> {
        delay(450)
        findUser(account) ?: return Result.failure(IllegalArgumentException("ACCOUNT_NOT_FOUND"))
        lastOtpAccount = account.trim()
        lastOtpCode = "123456"
        lastOtpExpiresAt = System.currentTimeMillis() + 5 * 60 * 1000
        return Result.success(lastOtpCode)
    }

    override suspend fun resetPassword(account: String, otp: String, newPassword: String): Result<Unit> {
        delay(520)
        val user = findUser(account) ?: return Result.failure(IllegalArgumentException("ACCOUNT_NOT_FOUND"))
        if (lastOtpAccount == null || normalizeComparable(lastOtpAccount!!) != normalizeComparable(account)) {
            return Result.failure(IllegalArgumentException("OTP_NOT_REQUESTED"))
        }
        if (System.currentTimeMillis() > lastOtpExpiresAt) return Result.failure(IllegalArgumentException("OTP_EXPIRED"))
        if (otp.filter(Char::isDigit) != lastOtpCode) return Result.failure(IllegalArgumentException("OTP_WRONG"))
        user.password = newPassword
        lastOtpAccount = null
        return Result.success(Unit)
    }

    override suspend fun biometricLogin(language: String): Result<UserSession> {
        delay(420)
        val user = users.first()
        return Result.success(user.toSession(language))
    }

    private fun MockUser.toSession(language: String): UserSession = UserSession(
        userId = id,
        token = "mock-token-${System.currentTimeMillis()}",
        name = name,
        role = role,
        language = language,
        lastLoginAt = System.currentTimeMillis()
    )

    private fun findUser(account: String): MockUser? {
        val normalized = normalizeComparable(account)
        return users.firstOrNull { user ->
            normalizeComparable(user.email) == normalized || normalizeComparable(user.phone) == normalized
        }
    }

    private fun normalizeComparable(value: String): String {
        val cleaned = value.trim().lowercase()
        return if (cleaned.contains("@")) cleaned else normalizePhone(cleaned)
    }

    private fun normalizePhone(value: String): String = value.filter { it.isDigit() }.let { digits ->
        when {
            digits.startsWith("84") && digits.length >= 10 -> "0" + digits.drop(2)
            digits.startsWith("0") -> digits
            digits.length >= 9 -> "0$digits"
            else -> digits
        }
    }
}
