package com.aplus.locknative.domain

enum class LockMode { Home, Hotel, Apartment, Office, Campus }
enum class CredentialType { Password, Fingerprint, Face, Card, Remote, Phone, Nfc, Admin }
enum class AlertSeverity { Info, Warning, Critical }

data class LockDevice(
    val id: String,
    val name: String,
    val room: String,
    val mode: LockMode,
    val isLocked: Boolean,
    val isOnline: Boolean,
    val battery: Int,
    val signal: Int,
    val doorOpen: Boolean,
    val firmware: String,
)

data class AccessCredential(
    val id: String,
    val lockId: String,
    val type: CredentialType,
    val ownerName: String,
    val label: String,
    val active: Boolean,
    val schedule: String,
)

data class AccessRecord(
    val id: String,
    val lockId: String,
    val title: String,
    val method: CredentialType,
    val timeText: String,
    val success: Boolean,
)

data class LockAlert(
    val id: String,
    val lockId: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val unread: Boolean,
)

data class Room(
    val id: String,
    val name: String,
    val building: String,
    val floor: String,
    val lockCount: Int,
)

data class Member(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val accessScope: String,
)

data class RealtimeEvent(
    val type: String,
    val lockId: String,
    val title: String,
    val body: String,
)
