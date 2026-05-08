package com.aplus.locknative.ui.foundation

/**
 * Single source of truth for app routes after Batch 0.
 *
 * UI mockups are still used as the visual reference, but navigation decisions must come from
 * this file instead of scattered string literals. Later batches should add new flows here first,
 * then wire the screen implementation to the route.
 */
object AppRoute {
    const val Login = "login"
    const val Register = "register"
    const val ForgotPassword = "forgot_password"

    const val Home = "home_dashboard"
    const val LockDetail = "lock_detail"
    const val AddKeyMenu = "add_key_menu"
    const val AddPassword = "add_password"
    const val PasswordManagement = "password_management"
    const val AddFingerprint = "add_fingerprint"
    const val AddFace = "add_face"
    const val AddCard = "add_card"
    const val AddRemoteControl = "add_remote_control"
    const val PhoneAuthorization = "phone_authorization"
    const val SubAdmin = "sub_admin"
    const val RemoteUnlock = "remote_unlock"
    const val Settings = "settings"
    const val MoreHub = "more_hub"
    const val UnlockRecords = "unlock_records"
    const val ElectricityReporting = "electricity_reporting"
    const val LockTransfer = "lock_transfer"
    const val CombinationUnlock = "combination_unlock"
    const val CycleNormallyOpen = "cycle_normally_open"
    const val NfcUnlocking = "nfc_unlocking"
    const val RoomManagement = "room_management"
    const val StaffTenantManagement = "staff_tenant_management"
    const val AlarmCenter = "alarm_center"
    const val NetworkingGateway = "networking_gateway"
    const val DeviceManagement = "device_management"
    const val ReportAnalytics = "report_analytics"
    const val ApartmentHotelPms = "apartment_hotel_pms"
    const val ProfileLanguage = "profile_language"
}

enum class MainTab(
    val label: String,
    val startRoute: String,
) {
    Home("Nhà", AppRoute.Home),
    Keys("Chìa khóa", AppRoute.LockDetail),
    Reports("Báo cáo", AppRoute.ReportAnalytics),
    Me("Tôi", AppRoute.ProfileLanguage),
}

object AplusRoutes {
    val authRoutes = setOf(
        AppRoute.Login,
        AppRoute.Register,
        AppRoute.ForgotPassword,
    )

    val keyRoutes = setOf(
        AppRoute.LockDetail,
        AppRoute.AddKeyMenu,
        AppRoute.AddPassword,
        AppRoute.PasswordManagement,
        AppRoute.AddFingerprint,
        AppRoute.AddFace,
        AppRoute.AddCard,
        AppRoute.AddRemoteControl,
        AppRoute.PhoneAuthorization,
        AppRoute.SubAdmin,
        AppRoute.RemoteUnlock,
        AppRoute.Settings,
        AppRoute.MoreHub,
        AppRoute.LockTransfer,
        AppRoute.CombinationUnlock,
        AppRoute.CycleNormallyOpen,
        AppRoute.NfcUnlocking,
        AppRoute.RoomManagement,
        AppRoute.StaffTenantManagement,
        AppRoute.AlarmCenter,
        AppRoute.NetworkingGateway,
        AppRoute.DeviceManagement,
        AppRoute.ApartmentHotelPms,
    )

    val moreHubChildren = setOf(
        AppRoute.UnlockRecords,
        AppRoute.ElectricityReporting,
        AppRoute.LockTransfer,
        AppRoute.CombinationUnlock,
        AppRoute.CycleNormallyOpen,
        AppRoute.NfcUnlocking,
        AppRoute.RoomManagement,
        AppRoute.StaffTenantManagement,
        AppRoute.AlarmCenter,
        AppRoute.NetworkingGateway,
        AppRoute.DeviceManagement,
        AppRoute.ApartmentHotelPms,
    )

    fun isAuthRoute(route: String): Boolean = route in authRoutes

    fun isKeyRoute(route: String): Boolean = route in keyRoutes

    fun routeForTab(tab: MainTab, lastKeyRoute: String): String = when (tab) {
        MainTab.Home -> AppRoute.Home
        MainTab.Keys -> lastKeyRoute.ifBlank { AppRoute.LockDetail }
        MainTab.Reports -> AppRoute.ReportAnalytics
        MainTab.Me -> AppRoute.ProfileLanguage
    }

    fun tabForRoute(route: String): MainTab = when (route) {
        AppRoute.Home -> MainTab.Home
        AppRoute.ReportAnalytics,
        AppRoute.UnlockRecords,
        AppRoute.ElectricityReporting -> MainTab.Reports
        AppRoute.ProfileLanguage -> MainTab.Me
        else -> MainTab.Keys
    }

    fun backRouteOf(route: String): String = when (route) {
        AppRoute.Register,
        AppRoute.ForgotPassword -> AppRoute.Login
        AppRoute.LockDetail -> AppRoute.Home
        AppRoute.AddKeyMenu,
        AppRoute.Settings,
        AppRoute.MoreHub -> AppRoute.LockDetail
        in moreHubChildren -> AppRoute.MoreHub
        AppRoute.ReportAnalytics,
        AppRoute.ProfileLanguage -> AppRoute.Home
        else -> AppRoute.LockDetail
    }
}
