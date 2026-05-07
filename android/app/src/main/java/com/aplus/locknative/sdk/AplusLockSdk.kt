package com.aplus.locknative.sdk

import com.aplus.locknative.domain.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

interface SmartLockAdapter {
    suspend fun listLocks(): List<LockDevice>
    suspend fun listCredentials(lockId: String): List<AccessCredential>
    suspend fun listRecords(lockId: String? = null): List<AccessRecord>
    suspend fun listAlerts(): List<LockAlert>
    suspend fun lock(lockId: String): LockDevice
    suspend fun unlock(lockId: String, remote: Boolean = false): LockDevice
    suspend fun addCredential(lockId: String, type: CredentialType, ownerName: String, label: String, schedule: String): AccessCredential
    suspend fun revokeCredential(credentialId: String)
    fun realtimeEvents(): Flow<RealtimeEvent>
}

class AplusLockSdk(private val adapter: SmartLockAdapter) {
    suspend fun getLocks() = adapter.listLocks()
    suspend fun getCredentials(lockId: String) = adapter.listCredentials(lockId)
    suspend fun getRecords(lockId: String? = null) = adapter.listRecords(lockId)
    suspend fun getAlerts() = adapter.listAlerts()
    suspend fun lock(lockId: String) = adapter.lock(lockId)
    suspend fun unlock(lockId: String, remote: Boolean = false) = adapter.unlock(lockId, remote)
    suspend fun addCredential(lockId: String, type: CredentialType, ownerName: String, label: String, schedule: String) =
        adapter.addCredential(lockId, type, ownerName, label, schedule)
    suspend fun revokeCredential(credentialId: String) = adapter.revokeCredential(credentialId)
    fun events() = adapter.realtimeEvents()
}

class MockSmartLockAdapter : SmartLockAdapter {
    private val events = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = 16)
    private val locks = MutableStateFlow(
        listOf(
            LockDevice("front-door", "Cửa chính", "Sảnh chính", LockMode.Apartment, true, true, 86, 92, false, "1.0.8"),
            LockDevice("office-door", "Phòng làm việc", "Tầng 2", LockMode.Office, false, true, 64, 78, false, "1.0.7"),
            LockDevice("hotel-203", "Phòng 203", "Khách sạn", LockMode.Hotel, true, false, 24, 35, false, "1.0.4")
        )
    )

    private val credentials = MutableStateFlow(
        listOf(
            AccessCredential("pwd-1", "front-door", CredentialType.Password, "Khách VIP", "Mã 3 ngày", true, "09:00 - 22:00, T2-CN"),
            AccessCredential("fp-1", "front-door", CredentialType.Fingerprint, "Dương Tiến", "Vân tay chính", true, "Luôn có hiệu lực"),
            AccessCredential("card-1", "hotel-203", CredentialType.Card, "Room 203", "Thẻ khách sạn", true, "Theo lịch check-in")
        )
    )

    private val records = MutableStateFlow(
        listOf(
            AccessRecord("rec-1", "front-door", "Dương Tiến mở khóa bằng App", CredentialType.Phone, "09:12 hôm nay", true),
            AccessRecord("rec-2", "front-door", "Khách VIP nhập mật khẩu", CredentialType.Password, "08:40 hôm nay", true),
            AccessRecord("rec-3", "hotel-203", "Phòng 203 quẹt thẻ", CredentialType.Card, "Tối qua", true)
        )
    )

    private val alerts = MutableStateFlow(
        listOf(
            LockAlert("alert-1", "hotel-203", "Khóa offline", "Gateway mất kết nối với Phòng 203", AlertSeverity.Warning, true),
            LockAlert("alert-2", "hotel-203", "Pin yếu", "Pin còn 24%, nên thay trong tuần này", AlertSeverity.Warning, true)
        )
    )

    override suspend fun listLocks(): List<LockDevice> = locks.value
    override suspend fun listCredentials(lockId: String): List<AccessCredential> = credentials.value.filter { it.lockId == lockId }
    override suspend fun listRecords(lockId: String?): List<AccessRecord> = records.value.filter { lockId == null || it.lockId == lockId }
    override suspend fun listAlerts(): List<LockAlert> = alerts.value

    override suspend fun lock(lockId: String): LockDevice {
        delay(220)
        return updateLock(lockId, isLocked = true, eventTitle = "Đã khóa cửa", eventBody = "Lệnh khóa đã thực thi")
    }

    override suspend fun unlock(lockId: String, remote: Boolean): LockDevice {
        delay(if (remote) 420 else 260)
        return updateLock(lockId, isLocked = false, eventTitle = if (remote) "Mở khóa từ xa" else "Đã mở khóa", eventBody = "Thiết bị xác nhận thành công")
    }

    override suspend fun addCredential(lockId: String, type: CredentialType, ownerName: String, label: String, schedule: String): AccessCredential {
        delay(240)
        val credential = AccessCredential(
            id = "${type.name.lowercase()}-${System.currentTimeMillis()}",
            lockId = lockId,
            type = type,
            ownerName = ownerName.ifBlank { "Người dùng mới" },
            label = label.ifBlank { type.name },
            active = true,
            schedule = schedule.ifBlank { "Luôn có hiệu lực" }
        )
        credentials.update { it + credential }
        records.update { current -> listOf(AccessRecord("rec-${System.currentTimeMillis()}", lockId, "Thêm ${type.name} cho ${credential.ownerName}", type, "Vừa xong", true)) + current }
        events.tryEmit(RealtimeEvent("credential.added", lockId, "Đã thêm quyền", credential.label))
        return credential
    }

    override suspend fun revokeCredential(credentialId: String) {
        delay(160)
        credentials.update { list -> list.map { if (it.id == credentialId) it.copy(active = false) else it } }
        events.tryEmit(RealtimeEvent("credential.revoked", "", "Đã thu hồi quyền", credentialId))
    }

    override fun realtimeEvents(): Flow<RealtimeEvent> = events.asSharedFlow()

    private fun updateLock(lockId: String, isLocked: Boolean, eventTitle: String, eventBody: String): LockDevice {
        var updated: LockDevice? = null
        locks.update { list ->
            list.map { lock ->
                if (lock.id == lockId) lock.copy(isLocked = isLocked).also { updated = it } else lock
            }
        }
        val lock = updated ?: locks.value.first()
        records.update { current ->
            listOf(AccessRecord("rec-${System.currentTimeMillis()}", lockId, eventTitle, CredentialType.Phone, "Vừa xong", true)) + current
        }
        events.tryEmit(RealtimeEvent("lock.status", lockId, eventTitle, eventBody))
        return lock
    }
}

object AplusSdkProvider {
    val sdk: AplusLockSdk by lazy { AplusLockSdk(MockSmartLockAdapter()) }
}
