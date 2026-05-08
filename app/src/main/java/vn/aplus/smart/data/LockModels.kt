package vn.aplus.smart.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class BuildingType(val label: String) {
    All("Tất cả"),
    Home("Nhà"),
    Hotel("Khách sạn"),
    Office("Văn phòng")
}

enum class LockKind(val label: String) {
    ApartmentDoor("Khóa căn hộ"),
    HotelRoom("Khóa khách sạn"),
    OfficeDoor("Khóa văn phòng"),
    Gate("Khóa cổng")
}

enum class RiskFilter(val label: String) {
    All("Tất cả"),
    Offline("Offline"),
    LowBattery("Pin yếu"),
    Unlocked("Đang mở"),
    Alert("Có cảnh báo")
}

data class HomeInfo(
    val id: String,
    val name: String,
    val type: BuildingType,
    val address: String,
    val timezone: String = "Asia/Ho_Chi_Minh",
    val status: String = "active"
)

data class RoomInfo(
    val id: String,
    val homeId: String,
    val floor: String,
    val roomNo: String,
    val name: String,
    val status: String,
    val currentTenantName: String? = null,
    val note: String? = null
)

data class LockDevice(
    val id: String,
    val name: String,
    val roomId: String,
    val homeId: String,
    val room: String,
    val homeName: String,
    val roomNo: String,
    val buildingType: BuildingType,
    val kind: LockKind,
    val model: String,
    val serial: String,
    val firmwareVersion: String,
    val locked: Boolean,
    val online: Boolean,
    val doorOpen: Boolean,
    val battery: Int,
    val signal: Int,
    val lastSeen: String,
    val alertBadges: List<String> = emptyList(),
    val tenantName: String? = null
) {
    val risk: String?
        get() = when {
            !online -> "Offline"
            battery <= 20 -> "Pin yếu"
            doorOpen -> "Cửa mở"
            alertBadges.isNotEmpty() -> alertBadges.first()
            else -> null
        }

    val lockStatusLabel: String
        get() = if (locked) "Đã khóa" else "Đang mở"

    val doorStatusLabel: String
        get() = if (doorOpen) "Cửa đang mở" else "Cửa đã đóng"
}




enum class CredentialType(val label: String, val shortLabel: String) {
    Password("Mật khẩu", "PWD"),
    Fingerprint("Vân tay", "FP"),
    Face("Khuôn mặt", "FACE"),
    Card("Thẻ", "CARD"),
    Remote("Remote", "REMOTE"),
    PhoneNfc("NFC & thẻ điện thoại", "NFC"),
    Combination("Mở khóa kết hợp", "COMBO"),
    Admin("Quản trị phụ", "ADMIN")
}

enum class CredentialStatus(val label: String) {
    Pending("Pending"),
    Active("Active"),
    Paused("Paused"),
    Expired("Expired"),
    Revoked("Revoked"),
    Used("Used"),
    FailedSync("FailedSync")
}

enum class PermissionRole(val label: String) {
    Owner("Owner"),
    Admin("Admin"),
    Member("Member"),
    Guest("Guest"),
    Staff("Staff"),
    Tenant("Tenant")
}

enum class PasswordType(val label: String, val shortLabel: String) {
    Normal("Mã thường", "NORMAL"),
    Temporary("Mã tạm thời", "TEMP"),
    OneTime("Mã một lần", "ONE"),
    Cycle("Mã chu kỳ", "CYCLE"),
    StaffGuest("Mã nhân viên/khách", "STAFF")
}

enum class SyncState(val label: String) {
    LocalOnly("LocalOnly"),
    PendingSync("PendingSync"),
    Synced("Synced"),
    PendingRevoke("PendingRevoke"),
    SyncFailed("SyncFailed")
}

data class PasswordPolicy(
    val minLength: Int = 6,
    val maxLength: Int = 10,
    val numericOnly: Boolean = true,
    val noDuplicateOnLock: Boolean = true,
    val noSequential: Boolean = false
)

data class LockCapability(
    val lockId: String,
    val supportsFingerprint: Boolean = true,
    val supportsFace: Boolean = true,
    val supportsCard: Boolean = true,
    val supportsNfc: Boolean = true,
    val supportsRemote: Boolean = true
) {
    fun supports(type: CredentialType): Boolean = when (type) {
        CredentialType.Fingerprint -> supportsFingerprint
        CredentialType.Face -> supportsFace
        CredentialType.Card -> supportsCard
        CredentialType.PhoneNfc -> supportsNfc
        CredentialType.Remote -> supportsRemote
        else -> true
    }
}

data class Credential(
    val id: String,
    val type: CredentialType,
    val ownerName: String,
    val ownerRole: PermissionRole,
    val lockIds: List<String>,
    val roomIds: List<String>,
    val lockName: String,
    val name: String,
    val status: CredentialStatus,
    val validFrom: String,
    val validTo: String,
    val scheduleRule: String? = null,
    val createdBy: String = "Aplus Owner",
    val createdAt: String = "Hôm nay",
    val revokedAt: String? = null,
    val riskNote: String? = null,
    val passwordType: PasswordType? = null,
    val passwordToken: String? = null,
    val passwordPolicy: String? = null,
    val maxUseCount: Int? = null,
    val usedCount: Int = 0,
    val syncState: SyncState = SyncState.Synced
)

data class AuditLog(
    val id: String,
    val actorId: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val before: String?,
    val after: String?,
    val createdAt: String
)

enum class AccessMethod(val label: String) {
    App("APP Unlock"),
    RemoteApp("REMOTE_APP"),
    Password("PASSWORD"),
    Card("CARD"),
    Fingerprint("FINGERPRINT"),
    Face("FACE"),
    Remote("REMOTE"),
    PhoneNfc("NFC_PHONE"),
    Combination("COMBINATION")
}

data class AccessRecord(
    val id: String,
    val lockId: String,
    val lockName: String,
    val userName: String,
    val method: AccessMethod,
    val result: String,
    val source: String,
    val reason: String? = null,
    val createdAt: String
)

data class CommandLog(
    val id: String,
    val lockId: String,
    val action: String,
    val method: AccessMethod,
    val state: String,
    val errorCode: String? = null,
    val createdAt: String
)

object MockLockRepository {
    val homes = listOf(
        HomeInfo("home-apartment", "Aplus Home", BuildingType.Home, "Căn hộ mẫu Aplus"),
        HomeInfo("home-hotel", "Hotel Tower", BuildingType.Hotel, "Khối khách sạn demo"),
        HomeInfo("home-office", "Văn phòng Aplus", BuildingType.Office, "Showroom & văn phòng")
    )

    val rooms = listOf(
        RoomInfo("room-520", "home-apartment", "Tầng 5", "520", "Căn hộ 520", "occupied", "Gia đình Aplus"),
        RoomInfo("room-301", "home-hotel", "Tầng 3", "301", "Phòng 301", "occupied", "Khách lưu trú"),
        RoomInfo("room-back-gate", "home-office", "Khu sau", "GATE-B", "Cổng sau", "common"),
        RoomInfo("room-showroom", "home-office", "Tầng 1", "SR-01", "Showroom chính", "open")
    )

    private val seedLocks = listOf(
        LockDevice(
            id = "lock-520",
            name = "Căn hộ 520",
            roomId = "room-520",
            homeId = "home-apartment",
            room = "Căn hộ 520",
            homeName = "Aplus Home",
            roomNo = "520",
            buildingType = BuildingType.Home,
            kind = LockKind.ApartmentDoor,
            model = "Aplus L520 Pro",
            serial = "APL-520-0001",
            firmwareVersion = "1.0.8",
            locked = true,
            online = true,
            doorOpen = false,
            battery = 92,
            signal = 4,
            lastSeen = "Vừa xong",
            tenantName = "Gia đình Aplus"
        ),
        LockDevice(
            id = "lock-301",
            name = "Phòng 301",
            roomId = "room-301",
            homeId = "home-hotel",
            room = "Phòng 301",
            homeName = "Hotel Tower",
            roomNo = "301",
            buildingType = BuildingType.Hotel,
            kind = LockKind.HotelRoom,
            model = "Aplus Hotel H3",
            serial = "APL-HOTEL-0301",
            firmwareVersion = "1.0.5",
            locked = true,
            online = false,
            doorOpen = false,
            battery = 46,
            signal = 2,
            lastSeen = "12 phút trước",
            alertBadges = listOf("Gateway yếu"),
            tenantName = "Khách lưu trú"
        ),
        LockDevice(
            id = "lock-back-gate",
            name = "Cổng sau",
            roomId = "room-back-gate",
            homeId = "home-office",
            room = "Cổng sau",
            homeName = "Văn phòng Aplus",
            roomNo = "GATE-B",
            buildingType = BuildingType.Office,
            kind = LockKind.Gate,
            model = "Aplus Gate G1",
            serial = "APL-GATE-0002",
            firmwareVersion = "1.1.0",
            locked = false,
            online = true,
            doorOpen = true,
            battery = 78,
            signal = 3,
            lastSeen = "Vừa xong",
            alertBadges = listOf("Đang mở")
        ),
        LockDevice(
            id = "lock-showroom",
            name = "Showroom chính",
            roomId = "room-showroom",
            homeId = "home-office",
            room = "Showroom chính",
            homeName = "Văn phòng Aplus",
            roomNo = "SR-01",
            buildingType = BuildingType.Office,
            kind = LockKind.OfficeDoor,
            model = "Aplus Office O2",
            serial = "APL-OFFICE-0007",
            firmwareVersion = "1.0.2",
            locked = true,
            online = true,
            doorOpen = false,
            battery = 18,
            signal = 4,
            lastSeen = "2 phút trước",
            alertBadges = listOf("Pin yếu")
        )
    )

    private val _locks = MutableStateFlow(seedLocks)
    val locks = _locks.asStateFlow()

    private val _records = MutableStateFlow(
        listOf(
            AccessRecord("rec-seed-1", "lock-520", "Căn hộ 520", "Aplus Owner", AccessMethod.App, "SUCCESS", "mobile", null, "08:30 hôm nay"),
            AccessRecord("rec-seed-2", "lock-301", "Phòng 301", "Khách lưu trú", AccessMethod.Card, "SUCCESS", "card", null, "09:15 hôm nay"),
            AccessRecord("rec-seed-3", "lock-back-gate", "Cổng sau", "Bảo vệ", AccessMethod.RemoteApp, "FAILED", "mobile", "DOOR_LEFT_OPEN", "10:02 hôm nay")
        )
    )
    val records = _records.asStateFlow()

    private val _commands = MutableStateFlow<List<CommandLog>>(emptyList())
    val commands = _commands.asStateFlow()

    private val _credentials = MutableStateFlow(
        listOf(
            Credential(
                id = "cred-pwd-520-main",
                type = CredentialType.Password,
                ownerName = "Aplus Owner",
                ownerRole = PermissionRole.Owner,
                lockIds = listOf("lock-520"),
                roomIds = listOf("room-520"),
                lockName = "Căn hộ 520",
                name = "Mã chủ nhà",
                status = CredentialStatus.Active,
                validFrom = "Luôn hiệu lực",
                validTo = "Không giới hạn",
                scheduleRule = "Full-time",
                passwordType = PasswordType.Normal,
                passwordToken = fakePasswordHash("123456"),
                passwordPolicy = "6-10 số • không trùng trên cùng khóa",
                maxUseCount = null,
                usedCount = 3,
                syncState = SyncState.Synced
            ),
            Credential(
                id = "cred-card-301-guest",
                type = CredentialType.Card,
                ownerName = "Khách lưu trú",
                ownerRole = PermissionRole.Guest,
                lockIds = listOf("lock-301"),
                roomIds = listOf("room-301"),
                lockName = "Phòng 301",
                name = "Thẻ khách sạn 301",
                status = CredentialStatus.Pending,
                validFrom = "14:00 hôm nay",
                validTo = "12:00 ngày mai",
                riskNote = "Chờ đồng bộ do Gateway yếu"
            ),
            Credential(
                id = "cred-remote-gate",
                type = CredentialType.Remote,
                ownerName = "Bảo vệ",
                ownerRole = PermissionRole.Staff,
                lockIds = listOf("lock-back-gate"),
                roomIds = listOf("room-back-gate"),
                lockName = "Cổng sau",
                name = "Remote bảo vệ",
                status = CredentialStatus.Active,
                validFrom = "Hôm nay",
                validTo = "31/12/2026"
            ),
            Credential(
                id = "cred-nfc-showroom",
                type = CredentialType.PhoneNfc,
                ownerName = "Nhân viên showroom",
                ownerRole = PermissionRole.Staff,
                lockIds = listOf("lock-showroom"),
                roomIds = listOf("room-showroom"),
                lockName = "Showroom chính",
                name = "NFC điện thoại nhân viên",
                status = CredentialStatus.Active,
                validFrom = "Hôm nay",
                validTo = "30 ngày"
            )
        )
    )
    val credentials = _credentials.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs = _auditLogs.asStateFlow()

    private val capabilities = mapOf(
        "lock-520" to LockCapability("lock-520", supportsFingerprint = true, supportsFace = true, supportsCard = true, supportsNfc = true, supportsRemote = true),
        "lock-301" to LockCapability("lock-301", supportsFingerprint = true, supportsFace = false, supportsCard = true, supportsNfc = false, supportsRemote = false),
        "lock-back-gate" to LockCapability("lock-back-gate", supportsFingerprint = false, supportsFace = false, supportsCard = true, supportsNfc = true, supportsRemote = true),
        "lock-showroom" to LockCapability("lock-showroom", supportsFingerprint = true, supportsFace = true, supportsCard = true, supportsNfc = true, supportsRemote = true)
    )

    fun capabilityFor(lockId: String): LockCapability = capabilities[lockId] ?: LockCapability(lockId)

    fun findLock(lockId: String): LockDevice? = _locks.value.firstOrNull { it.id == lockId }

    fun toggleLock(lockId: String) {
        val lockBefore = findLock(lockId) ?: return
        if (!lockBefore.online) {
            addFailedRecord(lockId, AccessMethod.App, "OFFLINE")
            return
        }
        _commands.update { it + CommandLog("cmd-${System.currentTimeMillis()}", lockId, if (lockBefore.locked) "UNLOCK" else "LOCK", AccessMethod.App, "SUCCESS", null, "Vừa xong") }
        _locks.update { current ->
            current.map { lock ->
                if (lock.id == lockId && lock.online) {
                    val nextLocked = !lock.locked
                    lock.copy(
                        locked = nextLocked,
                        doorOpen = !nextLocked,
                        lastSeen = "Vừa thao tác",
                        alertBadges = lock.alertBadges.filterNot { it == "Đang mở" } + if (!nextLocked) listOf("Đang mở") else emptyList()
                    )
                } else lock
            }
        }
        val lockAfter = findLock(lockId) ?: lockBefore
        _records.update { current ->
            listOf(AccessRecord("rec-${System.currentTimeMillis()}", lockId, lockAfter.name, "Aplus Owner", AccessMethod.App, "SUCCESS", "mobile", null, "Vừa xong")) + current
        }
    }

    fun remoteUnlock(lockId: String) {
        val lockBefore = findLock(lockId) ?: return
        if (!lockBefore.online) {
            addFailedRecord(lockId, AccessMethod.RemoteApp, "OFFLINE")
            return
        }
        _commands.update { it + CommandLog("cmd-${System.currentTimeMillis()}", lockId, "REMOTE_UNLOCK", AccessMethod.RemoteApp, "SUCCESS", null, "Vừa xong") }
        _locks.update { current ->
            current.map { lock ->
                if (lock.id == lockId && lock.online) {
                    lock.copy(
                        locked = false,
                        doorOpen = true,
                        lastSeen = "Remote unlock vừa xong",
                        alertBadges = lock.alertBadges.filterNot { it == "Đang mở" } + listOf("Đang mở")
                    )
                } else lock
            }
        }
        _records.update { current ->
            listOf(AccessRecord("rec-${System.currentTimeMillis()}", lockId, lockBefore.name, "Aplus Owner", AccessMethod.RemoteApp, "SUCCESS", "mobile", null, "Vừa xong")) + current
        }
    }

    fun addFailedRecord(lockId: String, method: AccessMethod, reason: String) {
        val lock = findLock(lockId) ?: return
        _commands.update { it + CommandLog("cmd-${System.currentTimeMillis()}", lockId, method.name.uppercase(), method, "FAILED", reason, "Vừa xong") }
        _records.update { current ->
            listOf(AccessRecord("rec-${System.currentTimeMillis()}", lockId, lock.name, "Aplus Owner", method, "FAILED", "mobile", reason, "Vừa xong")) + current
        }
    }

    private fun fakePasswordHash(code: String): String = "mockhash-${code.reversed()}-${code.length}"

    private fun isCredentialUsable(status: CredentialStatus): Boolean = status == CredentialStatus.Active || status == CredentialStatus.Pending

    fun createPasswordCredential(
        lockId: String,
        ownerName: String,
        ownerRole: PermissionRole,
        name: String,
        plainCode: String,
        passwordType: PasswordType,
        validFrom: String,
        validTo: String,
        scheduleRule: String? = null
    ): Pair<Boolean, String> {
        val lock = findLock(lockId) ?: return false to "Không tìm thấy khóa áp dụng."
        val code = plainCode.trim()
        val policy = PasswordPolicy()
        if (ownerName.isBlank()) return false to "Cần nhập người nhận."
        if (name.isBlank()) return false to "Cần nhập tên mã."
        if (code.length !in policy.minLength..policy.maxLength) return false to "Mã phải có 6-10 số."
        if (policy.numericOnly && code.any { !it.isDigit() }) return false to "Mã chỉ được chứa chữ số."
        if (validFrom.isBlank() || validTo.isBlank()) return false to "Cần nhập thời gian bắt đầu và kết thúc."
        if (validTo.contains("trước", ignoreCase = true)) return false to "Thời gian kết thúc không hợp lệ."
        val codeHash = fakePasswordHash(code)
        val duplicatedCode = _credentials.value.any { credential ->
            credential.type == CredentialType.Password &&
                credential.lockIds.contains(lockId) &&
                credential.passwordToken == codeHash &&
                credential.status !in listOf(CredentialStatus.Revoked, CredentialStatus.Expired, CredentialStatus.Used)
        }
        if (duplicatedCode) return false to "Mã đã tồn tại trên khóa này trong thời gian hiệu lực."
        val duplicatedName = _credentials.value.any { credential ->
            credential.type == CredentialType.Password &&
                credential.lockIds.contains(lockId) &&
                credential.name.equals(name.trim(), ignoreCase = true) &&
                credential.status !in listOf(CredentialStatus.Revoked, CredentialStatus.Expired, CredentialStatus.Used)
        }
        if (duplicatedName) return false to "Tên mã đã tồn tại trên khóa này."

        val now = System.currentTimeMillis()
        val syncState = if (lock.online) SyncState.Synced else SyncState.PendingSync
        val status = if (lock.online) CredentialStatus.Active else CredentialStatus.Pending
        val maxUse = when (passwordType) {
            PasswordType.OneTime -> 1
            else -> null
        }
        val credential = Credential(
            id = "cred-pwd-$now",
            type = CredentialType.Password,
            ownerName = ownerName.trim(),
            ownerRole = ownerRole,
            lockIds = listOf(lock.id),
            roomIds = listOf(lock.roomId),
            lockName = lock.name,
            name = name.trim(),
            status = status,
            validFrom = validFrom,
            validTo = validTo,
            scheduleRule = scheduleRule ?: passwordType.label,
            createdBy = "Aplus Owner",
            createdAt = "Vừa xong",
            riskNote = if (syncState == SyncState.PendingSync) "Khóa offline: mã đang PendingSync, cần thiết bị online để đồng bộ." else null,
            passwordType = passwordType,
            passwordToken = codeHash,
            passwordPolicy = "${policy.minLength}-${policy.maxLength} số • numericOnly=${policy.numericOnly} • noDuplicateOnLock=${policy.noDuplicateOnLock}",
            maxUseCount = maxUse,
            usedCount = 0,
            syncState = syncState
        )
        _credentials.update { listOf(credential) + it }
        _auditLogs.update { logs ->
            listOf(AuditLog("audit-$now", "user-owner", "CREATE_PASSWORD_${passwordType.name.uppercase()}", "Credential", credential.id, null, "${credential.status.label}/${credential.syncState.label}", "Vừa xong")) + logs
        }
        _records.update { current ->
            listOf(AccessRecord("rec-$now", lock.id, lock.name, "Aplus Owner", AccessMethod.Password, "AUDIT", "credential", "CREATE_PASSWORD_${passwordType.name.uppercase()}", "Vừa xong")) + current
        }
        val masked = code.takeLast(2).padStart(code.length, '*')
        return true to "Đã tạo ${passwordType.label}: $masked (${status.label}/${syncState.label})."
    }

    fun simulatePasswordUnlock(credentialId: String): Pair<Boolean, String> {
        val credential = _credentials.value.firstOrNull { it.id == credentialId && it.type == CredentialType.Password } ?: return false to "Không tìm thấy mã mật khẩu."
        val lockId = credential.lockIds.firstOrNull() ?: return false to "Mã chưa gắn khóa."
        val lock = findLock(lockId) ?: return false to "Không tìm thấy khóa."
        val now = System.currentTimeMillis()
        fun fail(reason: String): Pair<Boolean, String> {
            _records.update { current ->
                listOf(AccessRecord("rec-$now", lock.id, lock.name, credential.ownerName, AccessMethod.Password, "FAILED", "password", reason, "Vừa xong")) + current
            }
            return false to reason
        }
        if (!isCredentialUsable(credential.status)) return fail("Mã không còn hiệu lực: ${credential.status.label}.")
        if (credential.syncState != SyncState.Synced) return fail("Mã chưa đồng bộ xuống khóa: ${credential.syncState.label}.")
        if (!lock.online) return fail("Khóa offline, không xác nhận được mã.")
        if (credential.passwordType == PasswordType.OneTime && credential.usedCount >= 1) return fail("Mã một lần đã được sử dụng.")
        if (credential.passwordType == PasswordType.Cycle && credential.scheduleRule?.contains("Ngoài giờ", ignoreCase = true) == true) return fail("Mã chu kỳ đang ngoài khung giờ cho phép.")

        val nextStatus = if (credential.passwordType == PasswordType.OneTime) CredentialStatus.Used else credential.status
        _credentials.update { current -> current.map { if (it.id == credentialId) it.copy(status = nextStatus, usedCount = it.usedCount + 1) else it } }
        _locks.update { current -> current.map { if (it.id == lock.id) it.copy(locked = false, doorOpen = true, lastSeen = "Vừa mở bằng mật khẩu", alertBadges = it.alertBadges.filterNot { badge -> badge == "Đang mở" } + "Đang mở") else it } }
        _records.update { current ->
            listOf(AccessRecord("rec-$now", lock.id, lock.name, credential.ownerName, AccessMethod.Password, "SUCCESS", "password", credential.passwordType?.label, "Vừa xong")) + current
        }
        return true to if (nextStatus == CredentialStatus.Used) "Mở thành công. Mã một lần đã chuyển Used." else "Mở thành công bằng ${credential.name}."
    }

    fun extendCredential(credentialId: String, newValidTo: String): Pair<Boolean, String> {
        val credential = _credentials.value.firstOrNull { it.id == credentialId } ?: return false to "Không tìm thấy credential."
        if (credential.status == CredentialStatus.Revoked || credential.status == CredentialStatus.Used) return false to "Credential không thể gia hạn vì trạng thái ${credential.status.label}."
        val now = System.currentTimeMillis()
        _credentials.update { current -> current.map { if (it.id == credentialId) it.copy(validTo = newValidTo.ifBlank { "Gia hạn 7 ngày" }, status = CredentialStatus.Active) else it } }
        _auditLogs.update { logs -> listOf(AuditLog("audit-$now", "user-owner", "EXTEND_CREDENTIAL", "Credential", credential.id, credential.validTo, newValidTo, "Vừa xong")) + logs }
        return true to "Đã gia hạn ${credential.name}."
    }

    fun addCredential(
        lockId: String,
        type: CredentialType,
        ownerName: String,
        ownerRole: PermissionRole,
        name: String,
        validFrom: String,
        validTo: String,
        scheduleRule: String? = null
    ): Pair<Boolean, String> {
        val lock = findLock(lockId) ?: return false to "Không tìm thấy khóa."
        val capability = capabilityFor(lockId)
        if (!capability.supports(type)) {
            return false to "${lock.name} không hỗ trợ ${type.label}."
        }
        if (ownerName.isBlank()) {
            return false to "Cần chọn người sở hữu credential."
        }
        if (name.isBlank()) {
            return false to "Cần đặt tên credential."
        }
        val duplicatedName = _credentials.value.any { credential ->
            credential.status != CredentialStatus.Revoked && credential.lockIds.contains(lockId) && credential.name.equals(name.trim(), ignoreCase = true) && credential.type == type
        }
        if (duplicatedName) return false to "Credential cùng tên và cùng loại đã tồn tại trên khóa này."

        val status = if (lock.online) CredentialStatus.Active else CredentialStatus.Pending
        val now = System.currentTimeMillis()
        val credential = Credential(
            id = "cred-$now",
            type = type,
            ownerName = ownerName.trim(),
            ownerRole = ownerRole,
            lockIds = listOf(lock.id),
            roomIds = listOf(lock.roomId),
            lockName = lock.name,
            name = name.trim(),
            status = status,
            validFrom = validFrom.ifBlank { "Hôm nay" },
            validTo = validTo.ifBlank { "Không giới hạn" },
            scheduleRule = scheduleRule,
            riskNote = if (status == CredentialStatus.Pending) "PendingSync: khóa offline hoặc Gateway yếu" else null
        )
        _credentials.update { listOf(credential) + it }
        _auditLogs.update { logs ->
            listOf(AuditLog("audit-$now", "user-owner", "CREATE_${type.name.uppercase()}", "Credential", credential.id, null, credential.status.label, "Vừa xong")) + logs
        }
        _records.update { current ->
            listOf(AccessRecord("rec-$now", lock.id, lock.name, "Aplus Owner", methodForCredential(type), "AUDIT", "credential", "CREATE_${type.name.uppercase()}", "Vừa xong")) + current
        }
        return true to "Đã tạo ${type.label}: ${credential.name} (${credential.status.label})."
    }

    fun revokeCredential(credentialId: String): Pair<Boolean, String> {
        val credential = _credentials.value.firstOrNull { it.id == credentialId } ?: return false to "Không tìm thấy credential."
        if (credential.status == CredentialStatus.Revoked) return false to "Credential đã bị thu hồi."
        val now = System.currentTimeMillis()
        val firstLock = credential.lockIds.firstOrNull()?.let { findLock(it) }
        val revokeSyncState = if (firstLock?.online == false) SyncState.PendingRevoke else SyncState.Synced
        _credentials.update { current ->
            current.map { if (it.id == credentialId) it.copy(status = CredentialStatus.Revoked, revokedAt = "Vừa xong", riskNote = if (revokeSyncState == SyncState.PendingRevoke) "PendingRevoke: khóa offline, cần online để đồng bộ thu hồi" else "Đã thu hồi, không xóa cứng để giữ lịch sử", syncState = revokeSyncState) else it }
        }
        _auditLogs.update { logs ->
            listOf(AuditLog("audit-$now", "user-owner", "REVOKE_${credential.type.name.uppercase()}", "Credential", credential.id, credential.status.label, CredentialStatus.Revoked.label, "Vừa xong")) + logs
        }
        credential.lockIds.firstOrNull()?.let { lockId ->
            val lock = findLock(lockId)
            _records.update { current ->
                listOf(AccessRecord("rec-$now", lockId, lock?.name ?: credential.lockName, "Aplus Owner", methodForCredential(credential.type), "AUDIT", "credential", "REVOKE_${credential.type.name.uppercase()}", "Vừa xong")) + current
            }
        }
        return true to "Đã thu hồi ${credential.name}."
    }

    fun setCredentialPaused(credentialId: String, paused: Boolean): Pair<Boolean, String> {
        val credential = _credentials.value.firstOrNull { it.id == credentialId } ?: return false to "Không tìm thấy credential."
        if (credential.status == CredentialStatus.Revoked) return false to "Credential đã thu hồi, không thể bật/tắt."
        val newStatus = if (paused) CredentialStatus.Paused else CredentialStatus.Active
        val now = System.currentTimeMillis()
        _credentials.update { current -> current.map { if (it.id == credentialId) it.copy(status = newStatus) else it } }
        _auditLogs.update { logs ->
            listOf(AuditLog("audit-$now", "user-owner", if (paused) "PAUSE_CREDENTIAL" else "RESUME_CREDENTIAL", "Credential", credential.id, credential.status.label, newStatus.label, "Vừa xong")) + logs
        }
        return true to if (paused) "Đã tạm dừng ${credential.name}." else "Đã bật lại ${credential.name}."
    }

    private fun methodForCredential(type: CredentialType): AccessMethod = when (type) {
        CredentialType.Password -> AccessMethod.Password
        CredentialType.Fingerprint -> AccessMethod.Fingerprint
        CredentialType.Face -> AccessMethod.Face
        CredentialType.Card -> AccessMethod.Card
        CredentialType.Remote -> AccessMethod.Remote
        CredentialType.PhoneNfc -> AccessMethod.PhoneNfc
        CredentialType.Combination -> AccessMethod.Combination
        CredentialType.Admin -> AccessMethod.App
    }

    fun refreshMock() {
        _locks.update { current ->
            current.mapIndexed { index, lock ->
                val nextBattery = (lock.battery - if (index == 0) 0 else 1).coerceAtLeast(5)
                lock.copy(
                    battery = nextBattery,
                    lastSeen = if (lock.online) "Vừa cập nhật" else lock.lastSeen,
                    alertBadges = if (nextBattery <= 20 && "Pin yếu" !in lock.alertBadges) lock.alertBadges + "Pin yếu" else lock.alertBadges
                )
            }
        }
    }
}
