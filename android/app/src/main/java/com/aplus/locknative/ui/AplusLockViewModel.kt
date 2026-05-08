package com.aplus.locknative.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aplus.locknative.domain.*
import com.aplus.locknative.sdk.AplusSdkProvider
import com.aplus.locknative.ui.foundation.AppRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AplusUiState(
    val route: String = AppRoute.Login,
    val loggedIn: Boolean = false,
    val loading: Boolean = false,
    val toast: String? = null,
    val locks: List<LockDevice> = emptyList(),
    val selectedLockId: String = "front-door",
    val credentials: List<AccessCredential> = emptyList(),
    val records: List<AccessRecord> = emptyList(),
    val alerts: List<LockAlert> = emptyList(),
    val rooms: List<Room> = listOf(
        Room("room-lobby", "Sảnh chính", "Aplus Tower", "Tầng 1", 1),
        Room("room-office", "Phòng làm việc", "Aplus Tower", "Tầng 2", 1),
        Room("room-203", "Phòng 203", "Hotel Block", "Tầng 2", 1),
    ),
    val members: List<Member> = listOf(
        Member("mem-owner", "Dương Tiến", "Owner", "0900 000 001", "Toàn bộ nhà"),
        Member("mem-admin", "Quản lý khách sạn", "Admin", "0900 000 002", "Hotel Block"),
        Member("mem-guest", "Khách VIP", "Guest", "0900 000 003", "Cửa chính, 3 ngày"),
    ),
)

class AplusLockViewModel : ViewModel() {
    private val sdk = AplusSdkProvider.sdk
    private val _state = MutableStateFlow(AplusUiState())
    val state: StateFlow<AplusUiState> = _state.asStateFlow()

    init {
        refreshAll()
        viewModelScope.launch {
            sdk.events().collect { event ->
                _state.update { it.copy(toast = event.title) }
                refreshAll(silent = true)
            }
        }
    }

    fun login(email: String, password: String) {
        _state.update { it.copy(loggedIn = true, route = AppRoute.Home, toast = "Đăng nhập thành công") }
        refreshAll()
    }

    fun navigate(route: String) {
        _state.update { it.copy(route = route, toast = null) }
        if (route in listOf(AppRoute.LockDetail, AppRoute.PasswordManagement, AppRoute.UnlockRecords)) refreshSelectedLock()
    }

    fun backHome() = navigate(AppRoute.Home)

    fun selectLock(lockId: String) {
        _state.update { it.copy(selectedLockId = lockId, route = AppRoute.LockDetail) }
        refreshSelectedLock()
    }

    fun lockSelected() {
        val id = _state.value.selectedLockId
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            sdk.lock(id)
            _state.update { it.copy(loading = false, toast = "Đã khóa") }
            refreshAll(silent = true)
        }
    }

    fun unlockSelected(remote: Boolean = false) {
        val id = _state.value.selectedLockId
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            sdk.unlock(id, remote)
            _state.update { it.copy(loading = false, toast = if (remote) "Đã mở khóa từ xa" else "Đã mở khóa") }
            refreshAll(silent = true)
        }
    }

    fun addCredential(type: CredentialType, ownerName: String = "Người dùng mới", label: String = "Quyền mới", schedule: String = "Luôn có hiệu lực") {
        val id = _state.value.selectedLockId
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            sdk.addCredential(id, type, ownerName, label, schedule)
            _state.update { it.copy(loading = false, toast = "Đã thêm ${type.name}") }
            refreshSelectedLock()
        }
    }

    fun clearToast() = _state.update { it.copy(toast = null) }

    private fun refreshAll(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent) _state.update { it.copy(loading = true) }
            val locks = sdk.getLocks()
            val selected = _state.value.selectedLockId.ifBlank { locks.firstOrNull()?.id.orEmpty() }
            val credentials = sdk.getCredentials(selected)
            val records = sdk.getRecords(selected)
            val alerts = sdk.getAlerts()
            _state.update {
                it.copy(
                    loading = false,
                    locks = locks,
                    selectedLockId = selected,
                    credentials = credentials,
                    records = records,
                    alerts = alerts
                )
            }
        }
    }

    private fun refreshSelectedLock() {
        viewModelScope.launch {
            val id = _state.value.selectedLockId
            _state.update { it.copy(credentials = sdk.getCredentials(id), records = sdk.getRecords(id), alerts = sdk.getAlerts()) }
        }
    }
}
