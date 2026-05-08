package vn.aplus.smart.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import vn.aplus.smart.data.BuildingType
import vn.aplus.smart.data.HomeInfo
import vn.aplus.smart.data.LockDevice
import vn.aplus.smart.data.MockLockRepository
import vn.aplus.smart.data.RiskFilter
import vn.aplus.smart.data.AccessMethod
import vn.aplus.smart.data.CredentialType
import vn.aplus.smart.data.PermissionRole
import vn.aplus.smart.data.PasswordType
import vn.aplus.smart.data.RoomInfo

data class HomeUiState(
    val selectedType: BuildingType = BuildingType.All,
    val selectedRiskFilter: RiskFilter = RiskFilter.All,
    val searchQuery: String = "",
    val homes: List<HomeInfo> = emptyList(),
    val rooms: List<RoomInfo> = emptyList(),
    val allLocks: List<LockDevice> = emptyList(),
    val filteredLocks: List<LockDevice> = emptyList(),
    val offlineCount: Int = 0,
    val lowBatteryCount: Int = 0,
    val unlockedCount: Int = 0,
    val alertCount: Int = 0,
    val isRefreshing: Boolean = false
) {
    val heroLock: LockDevice?
        get() = filteredLocks.firstOrNull() ?: allLocks.firstOrNull()
}

class HomeViewModel : ViewModel() {
    private val repository = MockLockRepository
    private val selectedType = MutableStateFlow(BuildingType.All)
    private val selectedRiskFilter = MutableStateFlow(RiskFilter.All)
    private val searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        repository.locks,
        selectedType,
        selectedRiskFilter,
        searchQuery
    ) { locks, type, risk, query ->
        val normalizedQuery = query.trim().lowercase()
        val byType = if (type == BuildingType.All) locks else locks.filter { it.buildingType == type }
        val bySearch = if (normalizedQuery.isBlank()) byType else byType.filter { lock ->
            listOf(lock.name, lock.room, lock.homeName, lock.roomNo, lock.serial, lock.model, lock.tenantName.orEmpty())
                .any { it.lowercase().contains(normalizedQuery) }
        }
        val filtered = when (risk) {
            RiskFilter.All -> bySearch
            RiskFilter.Offline -> bySearch.filter { !it.online }
            RiskFilter.LowBattery -> bySearch.filter { it.battery <= 20 }
            RiskFilter.Unlocked -> bySearch.filter { !it.locked || it.doorOpen }
            RiskFilter.Alert -> bySearch.filter { it.alertBadges.isNotEmpty() || it.risk != null }
        }
        HomeUiState(
            selectedType = type,
            selectedRiskFilter = risk,
            searchQuery = query,
            homes = repository.homes,
            rooms = repository.rooms,
            allLocks = locks,
            filteredLocks = filtered,
            offlineCount = locks.count { !it.online },
            lowBatteryCount = locks.count { it.battery <= 20 },
            unlockedCount = locks.count { !it.locked || it.doorOpen },
            alertCount = locks.count { it.alertBadges.isNotEmpty() || it.risk != null }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(homes = repository.homes, rooms = repository.rooms, allLocks = repository.locks.value, filteredLocks = repository.locks.value))

    fun setBuildingFilter(type: BuildingType) { selectedType.value = type }
    fun setRiskFilter(filter: RiskFilter) { selectedRiskFilter.value = filter }
    fun setSearchQuery(value: String) { searchQuery.value = value }
    fun refresh() { repository.refreshMock() }
    fun toggleLock(lockId: String) { repository.toggleLock(lockId) }
    fun remoteUnlock(lockId: String) { repository.remoteUnlock(lockId) }
    fun addFailedRemoteUnlock(lockId: String, reason: String) { repository.addFailedRecord(lockId, AccessMethod.RemoteApp, reason) }
    val credentials = repository.credentials
    val auditLogs = repository.auditLogs

    fun findLock(lockId: String): LockDevice? = repository.findLock(lockId)
    fun capabilityFor(lockId: String) = repository.capabilityFor(lockId)
    fun addCredential(
        lockId: String,
        type: CredentialType,
        ownerName: String,
        ownerRole: PermissionRole,
        name: String,
        validFrom: String,
        validTo: String,
        scheduleRule: String? = null
    ): Pair<Boolean, String> = repository.addCredential(lockId, type, ownerName, ownerRole, name, validFrom, validTo, scheduleRule)


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
    ): Pair<Boolean, String> = repository.createPasswordCredential(lockId, ownerName, ownerRole, name, plainCode, passwordType, validFrom, validTo, scheduleRule)

    fun simulatePasswordUnlock(credentialId: String): Pair<Boolean, String> = repository.simulatePasswordUnlock(credentialId)

    fun extendCredential(credentialId: String, newValidTo: String): Pair<Boolean, String> = repository.extendCredential(credentialId, newValidTo)

    fun revokeCredential(id: String): Pair<Boolean, String> = repository.revokeCredential(id)
    fun pauseCredential(id: String): Pair<Boolean, String> = repository.setCredentialPaused(id, true)
    fun resumeCredential(id: String): Pair<Boolean, String> = repository.setCredentialPaused(id, false)
}
