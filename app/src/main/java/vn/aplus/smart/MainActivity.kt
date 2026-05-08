package vn.aplus.smart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import vn.aplus.smart.auth.AuthScreen
import vn.aplus.smart.auth.AuthUiState
import vn.aplus.smart.auth.AuthViewModel
import vn.aplus.smart.auth.ForgotStep
import vn.aplus.smart.data.BuildingType
import vn.aplus.smart.data.Credential
import vn.aplus.smart.data.CredentialStatus
import vn.aplus.smart.data.CredentialType
import vn.aplus.smart.data.PermissionRole
import vn.aplus.smart.data.PasswordType
import vn.aplus.smart.data.RoomInfo
import vn.aplus.smart.data.PairingMethod
import vn.aplus.smart.data.GatewayInfo
import vn.aplus.smart.data.DiscoveredDevice
import vn.aplus.smart.data.SyncState
import vn.aplus.smart.data.LockDevice
import vn.aplus.smart.data.RiskFilter
import vn.aplus.smart.home.HomeUiState
import vn.aplus.smart.home.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent { AplusBatch12App() }
    }
}

private enum class AppRoute {
    Login, Register, Forgot, Home, LockDetail, UnlockHub, RemoteUnlock,
    PasswordManager, CardManager, NfcPhoneCard, AddFace, AddRemote, AddCard, AddPassword, AddFingerprint, CombinationUnlock,
    PairingGateway, MoreHub, FeaturePlaceholder, Placeholder
}
private enum class MainTab(val label: String, val icon: Int) {
    Home("Nhà", R.drawable.ic_home),
    Keys("Chìa khóa", R.drawable.ic_keypad),
    Reports("Báo cáo", R.drawable.ic_report),
    Me("Tôi", R.drawable.ic_user)
}

private enum class CommandPhase { Idle, Pending, Sending, Success, Failed, Timeout }

private data class QuickAction(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val targetRoute: String,
    val uiCode: String
)

private val lockDetailQuickActions = listOf(
    QuickAction("Key", "Thêm quyền mở khóa", R.drawable.ic_keypad, AppRoute.UnlockHub.name, "UI-16"),
    QuickAction("Add pwd", "Thêm mật khẩu", R.drawable.ic_lock, AppRoute.AddPassword.name, "UI-26"),
    QuickAction("Add fingerprint", "Thêm vân tay", R.drawable.ic_fingerprint, AppRoute.AddFingerprint.name, "UI-27"),
    QuickAction("Add face", "Thêm khuôn mặt", R.drawable.ic_face, AppRoute.AddFace.name, "UI-23"),
    QuickAction("Adding card", "Thêm thẻ", R.drawable.ic_card, AppRoute.AddCard.name, "UI-25"),
    QuickAction("Add remote", "Thêm remote", R.drawable.ic_remote, AppRoute.AddRemote.name, "UI-24"),
    QuickAction("NFC/Phone", "NFC & thẻ điện thoại", R.drawable.ic_nfc, AppRoute.NfcPhoneCard.name, "UI-15"),
    QuickAction("Sub admin", "Quản trị phụ", R.drawable.ic_user, AppRoute.FeaturePlaceholder.name, "UI-13"),
    QuickAction("Settings", "Cài đặt khóa", R.drawable.ic_globe, AppRoute.FeaturePlaceholder.name, "UI-29"),
    QuickAction("More", "More Hub", R.drawable.ic_more, AppRoute.MoreHub.name, "UI-06")
)

private val batch4CredentialActions = listOf(
    QuickAction("Thêm mật khẩu", "Password credential", R.drawable.ic_lock, AppRoute.AddPassword.name, "UI-26"),
    QuickAction("Thêm vân tay", "Fingerprint credential", R.drawable.ic_fingerprint, AppRoute.AddFingerprint.name, "UI-27"),
    QuickAction("Thêm khuôn mặt", "Face credential", R.drawable.ic_face, AppRoute.AddFace.name, "UI-23"),
    QuickAction("Thêm thẻ", "Card credential", R.drawable.ic_card, AppRoute.AddCard.name, "UI-25"),
    QuickAction("Thêm remote", "Remote control", R.drawable.ic_remote, AppRoute.AddRemote.name, "UI-24"),
    QuickAction("NFC/Phone", "NFC & thẻ điện thoại", R.drawable.ic_nfc, AppRoute.NfcPhoneCard.name, "UI-15"),
    QuickAction("Mở kết hợp", "Combination rule", R.drawable.ic_combo, AppRoute.CombinationUnlock.name, "UI-28")
)

@Stable
private data class UiScaleConfig(
    val horizontal: Dp,
    val topGap: Dp,
    val gapXs: Dp,
    val gapSm: Dp,
    val gapMd: Dp,
    val cardPadding: Dp,
    val cardCorner: Dp,
    val headerHeight: Dp,
    val bottomBarHeight: Dp,
    val inputHeight: Dp,
    val buttonHeight: Dp,
    val logoSize: Dp,
    val title: TextUnit,
    val screenTitle: TextUnit,
    val body: TextUnit,
    val caption: TextUnit,
    val label: TextUnit,
    val button: TextUnit,
    val icon: Dp
)

private fun uiScale(maxHeight: Dp, maxWidth: Dp): UiScaleConfig {
    val tiny = maxHeight < 650.dp || maxWidth < 365.dp
    val compact = maxHeight < 760.dp || maxWidth < 390.dp
    return when {
        tiny -> UiScaleConfig(
            horizontal = 16.dp, topGap = 4.dp, gapXs = 4.dp, gapSm = 7.dp, gapMd = 11.dp,
            cardPadding = 14.dp, cardCorner = 22.dp, headerHeight = 48.dp, bottomBarHeight = 58.dp,
            inputHeight = 43.dp, buttonHeight = 44.dp, logoSize = 58.dp,
            title = 18.sp, screenTitle = 20.sp, body = 12.sp, caption = 9.2.sp, label = 10.sp, button = 14.sp, icon = 18.dp
        )
        compact -> UiScaleConfig(
            horizontal = 18.dp, topGap = 8.dp, gapXs = 5.dp, gapSm = 9.dp, gapMd = 14.dp,
            cardPadding = 16.dp, cardCorner = 24.dp, headerHeight = 54.dp, bottomBarHeight = 62.dp,
            inputHeight = 48.dp, buttonHeight = 48.dp, logoSize = 72.dp,
            title = 20.sp, screenTitle = 23.sp, body = 13.sp, caption = 10.sp, label = 10.5.sp, button = 15.sp, icon = 19.dp
        )
        else -> UiScaleConfig(
            horizontal = 22.dp, topGap = 12.dp, gapXs = 6.dp, gapSm = 11.dp, gapMd = 17.dp,
            cardPadding = 18.dp, cardCorner = 28.dp, headerHeight = 62.dp, bottomBarHeight = 68.dp,
            inputHeight = 54.dp, buttonHeight = 52.dp, logoSize = 88.dp,
            title = 24.sp, screenTitle = 27.sp, body = 14.sp, caption = 11.sp, label = 11.5.sp, button = 16.sp, icon = 21.dp
        )
    }
}

private object AplusTheme {
    val Background = Color(0xFF05060A)
    val Card = Color(0xFF101722)
    val CardDark = Color(0xFF090B0F)
    val Field = Color(0xFF0B0D10)
    val Text = Color(0xFFF7F8FB)
    val Muted = Color(0xFFAAB0BE)
    val Subtle = Color(0xFF6F7786)
    val Red = Color(0xFFF1153D)
    val RedDark = Color(0xFF650018)
    val Green = Color(0xFF23D66F)
    val Yellow = Color(0xFFFFC24B)
    val Blue = Color(0xFF4DA3FF)
    val Stroke = Color.White.copy(alpha = 0.075f)
    val Error = Color(0xFFFF5A6F)
    val WhitePanel = Color(0xFFF7F8FC)
    val InkOnWhite = Color(0xFF111827)
}

@Composable
private fun AplusBatch12App(
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()

    Surface(color = AplusTheme.Background) {
        AplusBackground {
            when (authState.currentScreen) {
                AuthScreen.Splash -> SplashScreen()
                AuthScreen.Login -> LoginScreen(state = authState, vm = authViewModel)
                AuthScreen.Register -> RegisterScreen(state = authState, vm = authViewModel)
                AuthScreen.Forgot -> ForgotScreen(state = authState, vm = authViewModel)
                AuthScreen.Home -> AuthenticatedApp(
                    authState = authState,
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedApp(
    authState: AuthUiState,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val credentials by homeViewModel.credentials.collectAsState()
    var route by rememberSaveable { mutableStateOf(AppRoute.Home.name) }
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home.name) }
    var selectedLockId by rememberSaveable { mutableStateOf("lock-520") }
    var lastMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var pairingBackRoute by rememberSaveable { mutableStateOf(AppRoute.Home.name) }
    var featureBackRoute by rememberSaveable { mutableStateOf(AppRoute.UnlockHub.name) }
    var featureTitle by rememberSaveable { mutableStateOf("Màn chức năng") }
    var featureCode by rememberSaveable { mutableStateOf("UI") }

    fun openHome() {
        selectedTab = MainTab.Home.name
        route = AppRoute.Home.name
    }

    fun openPlaceholder(message: String) {
        lastMessage = message
        route = AppRoute.Placeholder.name
    }

    fun openFeature(title: String, code: String, backRoute: AppRoute = AppRoute.UnlockHub) {
        featureBackRoute = backRoute.name
        featureTitle = title
        featureCode = code
        route = AppRoute.FeaturePlaceholder.name
    }

    fun openPairing(backRoute: AppRoute = AppRoute.Home) {
        pairingBackRoute = backRoute.name
        route = AppRoute.PairingGateway.name
    }

    fun handleQuickAction(action: QuickAction) {
        val nextRoute = runCatching { AppRoute.valueOf(action.targetRoute) }.getOrNull()
        if (nextRoute == null || nextRoute == AppRoute.FeaturePlaceholder) {
            openFeature(action.subtitle, action.uiCode)
        } else {
            route = nextRoute.name
        }
    }

    fun handleMainTab(tab: MainTab) {
        selectedTab = tab.name
        route = when (tab) {
            MainTab.Home -> AppRoute.Home.name
            MainTab.Keys -> AppRoute.UnlockHub.name
            else -> AppRoute.Placeholder.name
        }
    }

    when (AppRoute.valueOf(route)) {
        AppRoute.Login, AppRoute.Register, AppRoute.Forgot -> {
            openHome()
            HomeDashboardScreen(
                state = homeState,
                onFilterChange = homeViewModel::setBuildingFilter,
                onSearchChange = homeViewModel::setSearchQuery,
                onRiskFilterChange = homeViewModel::setRiskFilter,
                onRefresh = homeViewModel::refresh,
                onAddLock = { openPairing(AppRoute.Home) },
                onQuickAccess = { target -> if (target == AppRoute.PairingGateway) openPairing(AppRoute.Home) else route = target.name },
                onOpenLock = { id -> selectedLockId = id; route = AppRoute.LockDetail.name },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab,
                message = lastMessage,
                onClearMessage = { lastMessage = null }
            )
        }
        AppRoute.Home -> HomeDashboardScreen(
            state = homeState,
            onFilterChange = homeViewModel::setBuildingFilter,
            onSearchChange = homeViewModel::setSearchQuery,
            onRiskFilterChange = homeViewModel::setRiskFilter,
            onRefresh = homeViewModel::refresh,
            onAddLock = { openPairing(AppRoute.Home) },
            onQuickAccess = { target -> if (target == AppRoute.PairingGateway) openPairing(AppRoute.Home) else route = target.name },
            onOpenLock = { id -> selectedLockId = id; route = AppRoute.LockDetail.name },
            selectedTab = MainTab.valueOf(selectedTab),
            onTab = ::handleMainTab,
            message = lastMessage,
            onClearMessage = { lastMessage = null }
        )
        AppRoute.LockDetail -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            if (selectedLock == null) {
                openHome()
            } else {
                LockDetailScreen(
                    lock = selectedLock,
                    onBack = { openHome() },
                    onToggleLock = { homeViewModel.toggleLock(selectedLock.id) },
                    onOpenUnlockHub = { route = AppRoute.UnlockHub.name },
                    onOpenRemoteUnlock = { route = AppRoute.RemoteUnlock.name },
                    onQuickAction = ::handleQuickAction,
                    selectedTab = MainTab.valueOf(selectedTab),
                    onTab = ::handleMainTab
                )
            }
        }
        AppRoute.UnlockHub -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            UnlockPermissionHubScreen(
                lock = selectedLock,
                credentials = credentials,
                message = lastMessage,
                onClearMessage = { lastMessage = null },
                onBack = { route = AppRoute.LockDetail.name },
                onAction = ::handleQuickAction,
                onRevoke = { id -> lastMessage = homeViewModel.revokeCredential(id).second },
                onPause = { id -> lastMessage = homeViewModel.pauseCredential(id).second },
                onResume = { id -> lastMessage = homeViewModel.resumeCredential(id).second },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.PasswordManager -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            PasswordManagerScreen(
                lock = selectedLock,
                credentials = credentials,
                message = lastMessage,
                onClearMessage = { lastMessage = null },
                onBack = { route = AppRoute.UnlockHub.name },
                onAdd = { route = AppRoute.AddPassword.name },
                onRevoke = { id -> lastMessage = homeViewModel.revokeCredential(id).second },
                onPause = { id -> lastMessage = homeViewModel.pauseCredential(id).second },
                onResume = { id -> lastMessage = homeViewModel.resumeCredential(id).second },
                onExtend = { id -> lastMessage = homeViewModel.extendCredential(id, dateTimePlusHoursForUi(24 * 7)).second },
                onMockUnlock = { id -> lastMessage = homeViewModel.simulatePasswordUnlock(id).second },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.CardManager -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            CredentialListScreen(
                title = "Quản lý thẻ",
                uiCode = "UI-09",
                typeFilter = CredentialType.Card,
                lock = selectedLock,
                credentials = credentials,
                onBack = { route = AppRoute.UnlockHub.name },
                onAdd = { route = AppRoute.AddCard.name },
                onRevoke = { id -> lastMessage = homeViewModel.revokeCredential(id).second },
                onPause = { id -> lastMessage = homeViewModel.pauseCredential(id).second },
                onResume = { id -> lastMessage = homeViewModel.resumeCredential(id).second },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.NfcPhoneCard -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            SimpleCredentialCreateScreen(
                title = "NFC & thẻ điện thoại",
                uiCode = "UI-15",
                credentialType = CredentialType.PhoneNfc,
                defaultName = "NFC điện thoại",
                lock = selectedLock,
                existing = credentials,
                onBack = { route = AppRoute.UnlockHub.name },
                onSubmit = { lockId, type, owner, role, name, from, to, schedule ->
                    val result = homeViewModel.addCredential(lockId, type, owner, role, name, from, to, schedule)
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.AddFace -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            EnrollmentScreen(
                title = "Thêm khuôn mặt",
                uiCode = "UI-23",
                credentialType = CredentialType.Face,
                lock = selectedLock,
                capabilitySupported = selectedLock?.let { homeViewModel.capabilityFor(it.id).supports(CredentialType.Face) } ?: false,
                onBack = { route = AppRoute.UnlockHub.name },
                onComplete = { lockId, owner, name ->
                    val result = homeViewModel.addCredential(lockId, CredentialType.Face, owner, PermissionRole.Member, name, "Hôm nay", "Không giới hạn", "Face template token mock")
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.AddRemote -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            SimpleCredentialCreateScreen(
                title = "Thêm remote",
                uiCode = "UI-24",
                credentialType = CredentialType.Remote,
                defaultName = "Remote điều khiển",
                lock = selectedLock,
                existing = credentials,
                onBack = { route = AppRoute.UnlockHub.name },
                onSubmit = { lockId, type, owner, role, name, from, to, schedule ->
                    val result = homeViewModel.addCredential(lockId, type, owner, role, name, from, to, schedule ?: "Remote serial mock")
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.AddCard -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            SimpleCredentialCreateScreen(
                title = "Thêm thẻ",
                uiCode = "UI-25",
                credentialType = CredentialType.Card,
                defaultName = "Thẻ mở cửa",
                lock = selectedLock,
                existing = credentials,
                onBack = { route = AppRoute.CardManager.name },
                onSubmit = { lockId, type, owner, role, name, from, to, schedule ->
                    val result = homeViewModel.addCredential(lockId, type, owner, role, name, from, to, schedule ?: "CardId mock từ đầu đọc")
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.AddPassword -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            AddPasswordScreen(
                lock = selectedLock,
                existing = credentials,
                onBack = { route = AppRoute.PasswordManager.name },
                onSubmit = { lockId, owner, role, name, code, type, from, to, schedule ->
                    val result = homeViewModel.createPasswordCredential(lockId, owner, role, name, code, type, from, to, schedule)
                    lastMessage = result.second
                    if (result.first) route = AppRoute.PasswordManager.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.AddFingerprint -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            EnrollmentScreen(
                title = "Thêm vân tay",
                uiCode = "UI-27",
                credentialType = CredentialType.Fingerprint,
                lock = selectedLock,
                capabilitySupported = selectedLock?.let { homeViewModel.capabilityFor(it.id).supports(CredentialType.Fingerprint) } ?: false,
                onBack = { route = AppRoute.UnlockHub.name },
                onComplete = { lockId, owner, name ->
                    val result = homeViewModel.addCredential(lockId, CredentialType.Fingerprint, owner, PermissionRole.Member, name, "Hôm nay", "Không giới hạn", "3-step fingerprint enrollment mock")
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.CombinationUnlock -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            SimpleCredentialCreateScreen(
                title = "Mở khóa kết hợp",
                uiCode = "UI-28",
                credentialType = CredentialType.Combination,
                defaultName = "PIN + thẻ",
                lock = selectedLock,
                existing = credentials,
                onBack = { route = AppRoute.UnlockHub.name },
                onSubmit = { lockId, type, owner, role, name, from, to, schedule ->
                    val result = homeViewModel.addCredential(lockId, type, owner, role, name, from, to, schedule ?: "Rule: PIN + Card")
                    lastMessage = result.second
                    if (result.first) route = AppRoute.UnlockHub.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.MoreHub -> {
            MoreHubScreen(
                onBack = { route = AppRoute.LockDetail.name },
                onOpenGateway = { openPairing(AppRoute.MoreHub) },
                onOpenFeature = { title, code -> openFeature(title, code, AppRoute.MoreHub) },
                onOpenRoute = { target -> route = target.name },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.PairingGateway -> {
            PairingGatewayScreen(
                homes = homeState.homes,
                rooms = homeState.rooms,
                discoveredDevices = homeViewModel.discoveredDevices,
                gateways = homeViewModel.gateways,
                onBack = { route = pairingBackRoute },
                onPair = { method, serial, model, lockName, roomId, ssid, password, gatewayId ->
                    val result = homeViewModel.addPairedLock(method, serial, model, lockName, roomId, ssid, password, gatewayId)
                    lastMessage = result.second
                    if (result.first) route = AppRoute.Home.name
                },
                selectedTab = MainTab.valueOf(selectedTab),
                onTab = ::handleMainTab
            )
        }
        AppRoute.RemoteUnlock -> {
            val selectedLock = homeState.allLocks.firstOrNull { it.id == selectedLockId } ?: homeViewModel.findLock(selectedLockId)
            if (selectedLock == null) {
                openHome()
            } else {
                RemoteUnlockScreen(
                    lock = selectedLock,
                    onBack = { route = AppRoute.LockDetail.name },
                    onCommandSuccess = { homeViewModel.remoteUnlock(selectedLock.id) },
                    onCommandFailed = { reason -> homeViewModel.addFailedRemoteUnlock(selectedLock.id, reason) },
                    selectedTab = MainTab.valueOf(selectedTab),
                    onTab = ::handleMainTab
                )
            }
        }
        AppRoute.FeaturePlaceholder -> FeaturePlaceholderScreen(
            title = featureTitle,
            uiCode = featureCode,
            onBack = { route = featureBackRoute },
            selectedTab = MainTab.valueOf(selectedTab),
            onTab = ::handleMainTab
        )
        AppRoute.Placeholder -> PlaceholderScreen(
            tab = MainTab.valueOf(selectedTab),
            authState = authState,
            authViewModel = authViewModel,
            onBackHome = { openHome() },
            selectedTab = MainTab.valueOf(selectedTab),
            onTab = ::handleMainTab,
            message = lastMessage
        )
    }
}

@Composable
private fun AplusBackground(content: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(AplusTheme.Background)
            .background(
                Brush.radialGradient(
                    listOf(AplusTheme.RedDark.copy(alpha = 0.94f), Color.Transparent),
                    center = Offset(990f, 110f),
                    radius = 920f
                )
            )
            .background(
                Brush.radialGradient(
                    listOf(AplusTheme.RedDark.copy(alpha = 0.42f), Color.Transparent),
                    center = Offset(260f, 1780f),
                    radius = 850f
                )
            )
    ) { content() }
}

@Composable
private fun BaseScreen(
    title: String? = null,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBack: (() -> Unit)? = null,
    rightIcon: Int? = null,
    onRight: (() -> Unit)? = null,
    selectedTab: MainTab? = null,
    onTab: ((MainTab) -> Unit)? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    floatingAction: (@Composable (UiScaleConfig) -> Unit)? = null,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.(UiScaleConfig) -> Unit
) {
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        val spec = uiScale(maxHeight, maxWidth)
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = spec.horizontal)
            ) {
                if (title != null) {
                    AplusScreenHeader(
                        title = title,
                        subtitle = subtitle.orEmpty(),
                        showBack = showBack,
                        onBack = onBack,
                        rightIcon = rightIcon,
                        onRight = onRight,
                        spec = spec
                    )
                }

                errorMessage?.let {
                    AplusErrorBanner(message = it, spec = spec)
                    Spacer(Modifier.height(spec.gapSm))
                }

                val bodyModifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(bottom = if (selectedTab == null) spec.gapMd else spec.gapSm)

                Column(
                    modifier = bodyModifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = { content(spec) }
                )

                selectedTab?.let { tab ->
                    AplusBottomTab(selected = tab, onTab = { onTab?.invoke(it) }, spec = spec)
                }
            }

            floatingAction?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = spec.horizontal, bottom = if (selectedTab == null) 22.dp else spec.bottomBarHeight + 18.dp)
                ) { it(spec) }
            }

            if (isLoading) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) { Text("Đang xử lý...", color = AplusTheme.Text, fontWeight = FontWeight.Bold, fontSize = spec.body) }
            }
        }
    }
}


@Composable
private fun SplashScreen() {
    BaseScreen(scrollable = false) { spec ->
        Spacer(Modifier.height(spec.gapMd + 70.dp))
        AplusLogo(spec)
        Spacer(Modifier.height(spec.gapMd))
        CircularProgressIndicator(color = AplusTheme.Red, strokeWidth = 3.dp)
    }
}

@Composable
private fun LoginScreen(state: AuthUiState, vm: AuthViewModel) {
    BaseScreen(scrollable = true, isLoading = state.isLoading) { spec ->
        Spacer(Modifier.height(spec.topGap))
        AplusLogo(spec)
        Spacer(Modifier.height(spec.gapSm))

        AplusCard(spec) {
            Text("Đăng nhập", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text(
                "Kết nối nhà, khách sạn và văn phòng trong một ứng dụng.",
                color = AplusTheme.Muted,
                fontSize = spec.body,
                lineHeight = (spec.body.value + 4).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(spec.gapMd))

            AplusTextField(
                value = state.loginAccount,
                onValueChange = vm::updateLoginAccount,
                label = "Email / Số điện thoại",
                icon = R.drawable.ic_phone,
                spec = spec,
                keyboardType = KeyboardType.Email,
                error = state.loginAccountError,
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(
                value = state.loginPassword,
                onValueChange = vm::updateLoginPassword,
                label = "Mật khẩu",
                icon = R.drawable.ic_lock,
                spec = spec,
                keyboardType = KeyboardType.Password,
                password = true,
                error = state.loginPasswordError,
                enabled = !state.isLoading
            )
            Spacer(Modifier.height(spec.gapXs))

            Text(
                "Quên mật khẩu?",
                color = Color(0xFFFF6D83),
                fontSize = spec.body,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(enabled = !state.isLoading) { vm.goToForgot() }
                    .padding(vertical = spec.gapXs)
            )

            AuthMessage(state.globalMessage, spec)
            Spacer(Modifier.height(spec.gapXs))
            AplusButton("Đăng nhập", R.drawable.ic_lock, onClick = vm::login, spec = spec, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                AplusPillButton("Face ID", R.drawable.ic_face, Modifier.weight(1f), spec, enabled = !state.isLoading, onClick = vm::biometricLogin)
                AplusPillButton("Vân tay", R.drawable.ic_fingerprint, Modifier.weight(1f), spec, enabled = !state.isLoading, onClick = vm::biometricLogin)
            }
            Spacer(Modifier.height(spec.gapSm))
            Text("Tài khoản test: admin@aplus.vn / 123456", color = AplusTheme.Muted, fontSize = spec.label, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(spec.gapSm))
            Row(Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản?", color = AplusTheme.Muted, fontSize = spec.body)
                Spacer(Modifier.width(6.dp))
                Text("Tạo tài khoản mới", color = AplusTheme.Text, fontWeight = FontWeight.Bold, fontSize = spec.body, modifier = Modifier.clickable(enabled = !state.isLoading) { vm.goToRegister() })
            }
        }

        Spacer(Modifier.height(spec.gapSm))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            MiniInfoCard("Session thật", "DataStore local", R.drawable.ic_shield, Modifier.weight(1f), spec)
            MiniInfoCard("Đa ngôn ngữ", "Việt / English", R.drawable.ic_globe, Modifier.weight(1f).clickable { vm.toggleLanguage() }, spec)
            MiniInfoCard("Validate", "Không reset form", R.drawable.ic_report, Modifier.weight(1f), spec)
        }
    }
}

@Composable
private fun RegisterScreen(state: AuthUiState, vm: AuthViewModel) {
    BaseScreen(title = "Tạo tài khoản", subtitle = "Aplus Lock", showBack = true, onBack = vm::goToLogin, rightIcon = R.drawable.ic_globe, onRight = vm::toggleLanguage, isLoading = state.isLoading) { spec ->
        Spacer(Modifier.height(spec.gapSm))
        AplusCard(spec) {
            Text("Bắt đầu sử dụng", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Tài khoản dùng để đồng bộ nhà, phòng, khóa và quyền truy cập.", color = AplusTheme.Muted, fontSize = spec.body, maxLines = 2)
            Spacer(Modifier.height(spec.gapMd))

            AplusTextField(state.registerName, vm::updateRegisterName, "Tên công ty / người dùng", R.drawable.ic_user, spec, error = state.registerNameError, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(state.registerPhone, vm::updateRegisterPhone, "Số điện thoại", R.drawable.ic_phone, spec, KeyboardType.Phone, error = state.registerPhoneError, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(state.registerEmail, vm::updateRegisterEmail, "Email", R.drawable.ic_report, spec, KeyboardType.Email, error = state.registerEmailError, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(state.registerPassword, vm::updateRegisterPassword, "Mật khẩu", R.drawable.ic_lock, spec, KeyboardType.Password, password = true, error = state.registerPasswordError, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(state.registerConfirmPassword, vm::updateRegisterConfirmPassword, "Nhập lại mật khẩu", R.drawable.ic_lock, spec, KeyboardType.Password, password = true, error = state.registerConfirmPasswordError, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            TermsRow(state.registerAcceptedTerms, "Tôi đồng ý điều khoản sử dụng Aplus", { vm.toggleTerms() }, spec, error = state.registerTermsError, enabled = !state.isLoading)
            AuthMessage(state.globalMessage, spec)
            Spacer(Modifier.height(spec.gapMd))
            AplusButton("Tạo tài khoản", R.drawable.ic_user, onClick = vm::register, spec = spec, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusSecondaryButton("Đăng nhập bằng tài khoản có sẵn", R.drawable.ic_chevron_left, vm::goToLogin, spec)
        }
        Spacer(Modifier.height(spec.gapSm))
        SecurityNote("Quyền riêng tư", "Dữ liệu khóa được mã hóa; Batch 01 đang dùng repository mock và session DataStore.", spec)
    }
}

@Composable
private fun ForgotScreen(state: AuthUiState, vm: AuthViewModel) {
    BaseScreen(title = "Khôi phục mật khẩu", subtitle = "Xác minh an toàn", showBack = true, onBack = vm::goToLogin, rightIcon = R.drawable.ic_lock, isLoading = state.isLoading) { spec ->
        Spacer(Modifier.height(spec.gapMd))
        AplusCard(spec) {
            Box(
                modifier = Modifier
                    .size(if (spec.logoSize < 70.dp) 70.dp else 88.dp)
                    .clip(CircleShape)
                    .background(AplusTheme.Red.copy(alpha = 0.12f))
                    .border(BorderStroke(1.dp, AplusTheme.Red.copy(alpha = 0.45f)), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) { AplusIcon(R.drawable.ic_lock, AplusTheme.Red, spec.icon + 14.dp) }
            Spacer(Modifier.height(spec.gapMd))
            Text("Đặt lại mật khẩu", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Text("Nhập email hoặc số điện thoại đã đăng ký để nhận OTP mock.", color = AplusTheme.Muted, fontSize = spec.body, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            AplusTextField(state.forgotAccount, vm::updateForgotAccount, "Email / Số điện thoại", R.drawable.ic_phone, spec, KeyboardType.Email, error = state.forgotAccountError, enabled = !state.isLoading && state.forgotStep == ForgotStep.RequestAccount)
            if (state.forgotStep == ForgotStep.VerifyOtp) {
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(state.forgotOtp, vm::updateForgotOtp, "Mã OTP", R.drawable.ic_keypad, spec, KeyboardType.Number, error = state.forgotOtpError, enabled = !state.isLoading)
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(state.forgotNewPassword, vm::updateForgotNewPassword, "Mật khẩu mới", R.drawable.ic_lock, spec, KeyboardType.Password, password = true, error = state.forgotNewPasswordError, enabled = !state.isLoading)
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(state.forgotConfirmPassword, vm::updateForgotConfirmPassword, "Nhập lại mật khẩu", R.drawable.ic_lock, spec, KeyboardType.Password, password = true, error = state.forgotConfirmPasswordError, enabled = !state.isLoading)
                Spacer(Modifier.height(spec.gapXs))
                Text("OTP mock mặc định: 123456", color = AplusTheme.Muted, fontSize = spec.label, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
            AuthMessage(state.globalMessage, spec)
            Spacer(Modifier.height(spec.gapMd))
            val title = if (state.forgotStep == ForgotStep.RequestAccount) "Gửi OTP" else "Đặt lại mật khẩu"
            AplusButton(title, R.drawable.ic_report, onClick = { if (state.forgotStep == ForgotStep.RequestAccount) vm.requestOtp() else vm.resetPassword() }, spec = spec, enabled = !state.isLoading)
            Spacer(Modifier.height(spec.gapSm))
            AplusSecondaryButton("Quay lại đăng nhập", R.drawable.ic_chevron_left, vm::goToLogin, spec)
        }
    }
}

@Composable
private fun HomeDashboardScreen(
    state: HomeUiState,
    onFilterChange: (BuildingType) -> Unit,
    onSearchChange: (String) -> Unit,
    onRiskFilterChange: (RiskFilter) -> Unit,
    onRefresh: () -> Unit,
    onAddLock: () -> Unit,
    onQuickAccess: (AppRoute) -> Unit,
    onOpenLock: (String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit,
    message: String?,
    onClearMessage: () -> Unit
) {
    BaseScreen(
        title = "Aplus Lock",
        subtitle = "Home Dashboard",
        rightIcon = R.drawable.ic_globe,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true,
        floatingAction = { spec -> FloatingScannerButton(spec, onClick = onAddLock) }
    ) { spec ->
        if (!message.isNullOrBlank()) {
            AplusMessageBanner(message, spec, onClick = onClearMessage)
            Spacer(Modifier.height(spec.gapSm))
        }

        state.heroLock?.let { hero ->
            HeroLockCard(lock = hero, onOpen = { onOpenLock(hero.id) }, spec = spec)
            Spacer(Modifier.height(spec.gapSm))
        }

        DashboardSummaryRow(state = state, spec = spec)
        Spacer(Modifier.height(spec.gapSm))

        SearchBox(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            placeholder = "Tìm khóa, phòng, khách thuê, serial...",
            spec = spec
        )
        Spacer(Modifier.height(spec.gapSm))

        FilterRow(selected = state.selectedType, onSelect = onFilterChange, spec = spec)
        Spacer(Modifier.height(spec.gapSm))

        RiskFilterRow(selected = state.selectedRiskFilter, onSelect = onRiskFilterChange, spec = spec)
        Spacer(Modifier.height(spec.gapSm))

        SectionHeader("Thiết bị của bạn", "${state.filteredLocks.size}/${state.allLocks.size} khóa", spec)
        if (state.filteredLocks.isEmpty()) {
            EmptyState(
                title = "Không có khóa phù hợp",
                body = "Bộ lọc hiện tại không có dữ liệu. Bạn có thể đổi bộ lọc hoặc bấm + để mở UI-12 Kết nối Gateway / thêm khóa.",
                spec = spec
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                state.filteredLocks.forEach { lock ->
                    LockListItem(lock = lock, onClick = { onOpenLock(lock.id) }, spec = spec)
                }
            }
        }

        Spacer(Modifier.height(spec.gapMd))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            AplusPillButton("Làm mới", R.drawable.ic_report, Modifier.weight(1f), spec, onClick = onRefresh)
            AplusPillButton("Thêm khóa", R.drawable.ic_keypad, Modifier.weight(1f), spec, onClick = onAddLock)
        }

        Spacer(Modifier.height(spec.gapMd))
        SectionHeader("Tác vụ nhanh", "Bấm trực tiếp để mở chức năng", spec)
        QuickAccessGrid(spec = spec, onOpen = onQuickAccess)
    }
}

@Composable
private fun LockDetailScreen(
    lock: LockDevice,
    onBack: () -> Unit,
    onToggleLock: () -> Unit,
    onOpenUnlockHub: () -> Unit,
    onOpenRemoteUnlock: () -> Unit,
    onQuickAction: (QuickAction) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var showConfirm by rememberSaveable(lock.id) { mutableStateOf(false) }
    var commandPhase by rememberSaveable(lock.id) { mutableStateOf(CommandPhase.Idle.name) }
    var commandMessage by rememberSaveable(lock.id) { mutableStateOf<String?>(null) }
    val phase = CommandPhase.valueOf(commandPhase)

    LaunchedEffect(commandPhase, lock.id) {
        when (CommandPhase.valueOf(commandPhase)) {
            CommandPhase.Pending -> { delay(450); commandPhase = CommandPhase.Sending.name }
            CommandPhase.Sending -> {
                delay(650)
                if (lock.online) {
                    onToggleLock()
                    commandMessage = "Command success: đã ghi AccessRecord và đồng bộ Home."
                    commandPhase = CommandPhase.Success.name
                } else {
                    commandMessage = "Khóa offline: command bị chặn, trạng thái khóa giữ nguyên."
                    commandPhase = CommandPhase.Failed.name
                }
            }
            CommandPhase.Success, CommandPhase.Failed -> { delay(1600); commandPhase = CommandPhase.Idle.name }
            else -> Unit
        }
    }

    if (showConfirm) {
        ConfirmDialog(
            title = if (lock.locked) "Xác thực mở khóa" else "Xác thực khóa lại",
            message = if (lock.online) "Thao tác sẽ đi qua command lifecycle: pending → sending → success/failed. UI không tự đổi trạng thái trước khi command thành công." else "Khóa đang offline nên không thể thao tác. Hãy kiểm tra Gateway/Wi‑Fi/Bluetooth.",
            confirmText = if (lock.locked) "Xác nhận" else "Khóa lại",
            onConfirm = {
                showConfirm = false
                commandPhase = if (lock.online) CommandPhase.Pending.name else CommandPhase.Failed.name
                if (!lock.online) commandMessage = "Offline: không gửi lệnh mở/khóa."
            },
            onDismiss = { showConfirm = false }
        )
    }

    BaseScreen(
        title = lock.name,
        subtitle = "${lock.homeName} • ${lock.room}",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_globe,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true,
        floatingAction = { spec -> FloatingScannerButton(spec, onClick = onOpenRemoteUnlock) }
    ) { spec ->
        commandMessage?.let {
            AplusMessageBanner(it, spec, onClick = { commandMessage = null })
            Spacer(Modifier.height(spec.gapSm))
        }

        Spacer(Modifier.height(spec.gapSm))
        Box(
            modifier = Modifier
                .size(if (spec.logoSize < 70.dp) 108.dp else 132.dp)
                .clip(CircleShape)
                .background(lockCenterBackground(lock, phase))
                .border(BorderStroke(1.dp, lockCenterStroke(lock, phase)), CircleShape)
                .clickable(enabled = phase == CommandPhase.Idle) { showConfirm = true }
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            if (phase == CommandPhase.Pending || phase == CommandPhase.Sending) {
                CircularProgressIndicator(color = AplusTheme.Red, strokeWidth = 3.dp, modifier = Modifier.size(54.dp))
            } else {
                AplusIcon(R.drawable.ic_lock, lockCenterIconColor(lock, phase), spec.icon + 18.dp)
            }
        }
        Spacer(Modifier.height(spec.gapSm))
        Text(
            lockCenterLabel(lock, phase),
            color = AplusTheme.Text,
            fontWeight = FontWeight.Black,
            fontSize = spec.body,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(spec.gapSm))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            AplusStatusChip(if (lock.online) "Online" else "Offline", if (lock.online) AplusTheme.Green else AplusTheme.Yellow, spec)
            Spacer(Modifier.width(8.dp))
            AplusStatusChip("Pin ${lock.battery}%", if (lock.battery <= 20) AplusTheme.Yellow else AplusTheme.Green, spec)
            Spacer(Modifier.width(8.dp))
            AplusStatusChip("Sóng ${lock.signal}/4", AplusTheme.Blue, spec)
        }
        Spacer(Modifier.height(spec.gapSm))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            AplusStatusChip(lock.lockStatusLabel, if (lock.locked) AplusTheme.Red else AplusTheme.Blue, spec)
            Spacer(Modifier.width(8.dp))
            AplusStatusChip(lock.doorStatusLabel, if (lock.doorOpen) AplusTheme.Yellow else AplusTheme.Green, spec)
        }
        Spacer(Modifier.height(spec.gapMd))

        AplusPillButton(
            "Mở khóa từ xa",
            R.drawable.ic_lock,
            Modifier.fillMaxWidth(),
            spec,
            enabled = phase == CommandPhase.Idle,
            onClick = onOpenRemoteUnlock
        )
        Spacer(Modifier.height(spec.gapMd))

        QuickActionPanel(spec = spec, onAction = onQuickAction)
        Spacer(Modifier.height(spec.gapMd))
        DeviceInfoCard(lock, spec)
        Spacer(Modifier.height(spec.gapMd))
        LastEventCard(lock, spec)
    }
}

private fun lockCenterBackground(lock: LockDevice, phase: CommandPhase): Color = when (phase) {
    CommandPhase.Pending, CommandPhase.Sending -> AplusTheme.CardDark
    CommandPhase.Failed, CommandPhase.Timeout -> AplusTheme.Red.copy(alpha = 0.18f)
    else -> if (!lock.online) AplusTheme.CardDark else if (lock.locked) AplusTheme.WhitePanel else AplusTheme.Red.copy(alpha = 0.22f)
}

private fun lockCenterStroke(lock: LockDevice, phase: CommandPhase): Color = when (phase) {
    CommandPhase.Pending, CommandPhase.Sending -> AplusTheme.Red
    CommandPhase.Failed, CommandPhase.Timeout -> AplusTheme.Error
    else -> if (!lock.online) AplusTheme.Yellow else if (lock.locked) Color.White.copy(alpha = 0.75f) else AplusTheme.Red
}

private fun lockCenterIconColor(lock: LockDevice, phase: CommandPhase): Color = when (phase) {
    CommandPhase.Failed, CommandPhase.Timeout -> AplusTheme.Error
    else -> if (!lock.online) AplusTheme.Yellow else if (lock.locked) AplusTheme.Red else Color.White
}

private fun lockCenterLabel(lock: LockDevice, phase: CommandPhase): String = when (phase) {
    CommandPhase.Pending -> "Đang tạo command..."
    CommandPhase.Sending -> "Đang gửi lệnh tới khóa..."
    CommandPhase.Success -> "Thành công"
    CommandPhase.Failed -> "Thao tác thất bại"
    CommandPhase.Timeout -> "Command timeout"
    CommandPhase.Idle -> if (!lock.online) "Khóa offline" else if (lock.locked) "Chạm để mở khóa" else "Chạm để khóa lại"
}

@Composable
private fun UnlockPermissionHubScreen(
    lock: LockDevice?,
    credentials: List<Credential>,
    message: String?,
    onClearMessage: () -> Unit,
    onBack: () -> Unit,
    onAction: (QuickAction) -> Unit,
    onRevoke: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    val lockCredentials = credentials.filter { credential -> lock?.id == null || credential.lockIds.contains(lock.id) }
    BaseScreen(
        title = "Thêm quyền mở khóa",
        subtitle = lock?.let { "${it.name} • ${it.room}" } ?: "UI-16",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_keypad,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true
    ) { spec ->
        if (!message.isNullOrBlank()) {
            AplusMessageBanner(message, spec, onClick = onClearMessage)
            Spacer(Modifier.height(spec.gapSm))
        }
        AplusCard(spec) {
            Text("Credential Center", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Spacer(Modifier.height(spec.gapXs))
            Text("UI-16 gom các loại chìa khóa vào chung một model Credential. Không còn dòng Key bên trong màn này.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            CredentialStatusSummary(lockCredentials, spec)
        }
        Spacer(Modifier.height(spec.gapMd))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            AplusPillButton("Quản lý mật khẩu", R.drawable.ic_lock, Modifier.weight(1f), spec, onClick = { onAction(QuickAction("Quản lý mật khẩu", "Danh sách mã", R.drawable.ic_lock, AppRoute.PasswordManager.name, "UI-03")) })
            AplusPillButton("Quản lý thẻ", R.drawable.ic_card, Modifier.weight(1f), spec, onClick = { onAction(QuickAction("Quản lý thẻ", "Danh sách thẻ", R.drawable.ic_card, AppRoute.CardManager.name, "UI-09")) })
        }
        Spacer(Modifier.height(spec.gapMd))

        SectionHeader("Thêm quyền mở khóa", "Batch 04", spec)
        Spacer(Modifier.height(spec.gapSm))
        Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            batch4CredentialActions.forEach { action ->
                UnlockHubRow(action = action, spec = spec, onClick = { onAction(action) })
            }
        }

        Spacer(Modifier.height(spec.gapMd))
        SectionHeader("Credential đang có", "${lockCredentials.size} mục", spec)
        Spacer(Modifier.height(spec.gapSm))
        if (lockCredentials.isEmpty()) {
            EmptyStateCard("Chưa có credential", "Bấm một phương thức ở trên để tạo credential cho khóa/phòng này.", R.drawable.ic_keypad, spec)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                lockCredentials.forEach { credential ->
                    CredentialRow(
                        credential = credential,
                        spec = spec,
                        onRevoke = { onRevoke(credential.id) },
                        onPause = { onPause(credential.id) },
                        onResume = { onResume(credential.id) }
                    )
                }
            }
        }
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote(
            title = "Rule an toàn Batch 04",
            body = "Không tạo credential nếu thiếu owner/lock. Thu hồi chỉ đổi trạng thái Revoked để giữ audit/log; credential hết hạn hoặc revoked không còn hợp lệ.",
            spec = spec
        )
    }
}

@Composable
private fun CredentialStatusSummary(credentials: List<Credential>, spec: UiScaleConfig) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
        CredentialMetric("Active", credentials.count { it.status == CredentialStatus.Active }, AplusTheme.Green, Modifier.weight(1f), spec)
        CredentialMetric("Pending", credentials.count { it.status == CredentialStatus.Pending }, AplusTheme.Blue, Modifier.weight(1f), spec)
        CredentialMetric("Paused", credentials.count { it.status == CredentialStatus.Paused }, AplusTheme.Yellow, Modifier.weight(1f), spec)
        CredentialMetric("Revoked", credentials.count { it.status == CredentialStatus.Revoked }, AplusTheme.Error, Modifier.weight(1f), spec)
    }
}

@Composable
private fun CredentialMetric(title: String, value: Int, color: Color, modifier: Modifier, spec: UiScaleConfig) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.75f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.35f)), RoundedCornerShape(14.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value.toString(), color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
        Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = (spec.caption.value - 1).sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun statusColor(status: CredentialStatus): Color = when (status) {
    CredentialStatus.Active -> AplusTheme.Green
    CredentialStatus.Pending -> AplusTheme.Blue
    CredentialStatus.Paused -> AplusTheme.Yellow
    CredentialStatus.Expired -> AplusTheme.Subtle
    CredentialStatus.Revoked -> AplusTheme.Error
    CredentialStatus.Used -> AplusTheme.Blue
    CredentialStatus.FailedSync -> AplusTheme.Error
}

@Composable
private fun UnlockHubRow(action: QuickAction, spec: UiScaleConfig, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.94f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(AplusTheme.Red.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            AplusIcon(action.icon, AplusTheme.Red, spec.icon)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(action.title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${action.uiCode} • ${action.subtitle}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("›", color = AplusTheme.Muted, fontSize = spec.title, fontWeight = FontWeight.Black)
    }
}


@Composable
private fun CredentialListScreen(
    title: String,
    uiCode: String,
    typeFilter: CredentialType,
    lock: LockDevice?,
    credentials: List<Credential>,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRevoke: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    val rows = credentials.filter { it.type == typeFilter && (lock == null || it.lockIds.contains(lock.id)) }
    BaseScreen(
        title = title,
        subtitle = "$uiCode • ${lock?.name ?: "Tất cả khóa"}",
        showBack = true,
        onBack = onBack,
        rightIcon = if (typeFilter == CredentialType.Card) R.drawable.ic_card else R.drawable.ic_lock,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true,
        floatingAction = { spec -> FloatingScannerButton(spec, onClick = onAdd) }
    ) { spec ->
        AplusCard(spec) {
            Text(typeFilter.label, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Danh sách dùng chung Credential model: owner, lock/phòng, thời hạn, trạng thái và audit.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            CredentialStatusSummary(rows, spec)
        }
        Spacer(Modifier.height(spec.gapMd))
        AplusButton(if (typeFilter == CredentialType.Card) "Thêm thẻ" else "Thêm mật khẩu", if (typeFilter == CredentialType.Card) R.drawable.ic_card else R.drawable.ic_lock, onAdd, spec)
        Spacer(Modifier.height(spec.gapMd))
        if (rows.isEmpty()) {
            EmptyStateCard("Chưa có ${typeFilter.label.lowercase()}", "Bấm nút thêm để tạo credential mới cho ${lock?.name ?: "khóa đang chọn"}.", if (typeFilter == CredentialType.Card) R.drawable.ic_card else R.drawable.ic_lock, spec)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                rows.forEach { credential ->
                    CredentialRow(
                        credential = credential,
                        spec = spec,
                        onRevoke = { onRevoke(credential.id) },
                        onPause = { onPause(credential.id) },
                        onResume = { onResume(credential.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordManagerScreen(
    lock: LockDevice?,
    credentials: List<Credential>,
    message: String?,
    onClearMessage: () -> Unit,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRevoke: (String) -> Unit,
    onPause: (String) -> Unit,
    onResume: (String) -> Unit,
    onExtend: (String) -> Unit,
    onMockUnlock: (String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    val rows = credentials.filter { it.type == CredentialType.Password && (lock == null || it.lockIds.contains(lock.id)) }
    val activeCount = rows.count { it.status == CredentialStatus.Active }
    val expiringCount = rows.count { it.validTo.contains("ngày mai", ignoreCase = true) || it.validTo.contains("7 ngày", ignoreCase = true) }
    val revokedCount = rows.count { it.status == CredentialStatus.Revoked }
    val unlockTodayCount = rows.sumOf { it.usedCount }

    BaseScreen(
        title = "Quản lý mật khẩu",
        subtitle = "UI-03 • ${lock?.name ?: "Tất cả khóa"}",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_lock,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true,
        floatingAction = { spec -> FloatingScannerButton(spec, onClick = onAdd) }
    ) { spec ->
        if (!message.isNullOrBlank()) {
            AplusMessageBanner(message, spec, onClick = onClearMessage)
            Spacer(Modifier.height(spec.gapSm))
        }
        AplusCard(spec) {
            Text("Tổng quan mật khẩu", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Quản lý mã thường, mã tạm thời, mã một lần và mã chu kỳ. Mã chỉ lưu token/hash mock, không hiển thị lại plain text sau khi tạo.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                CredentialMetric("Active", activeCount, AplusTheme.Green, Modifier.weight(1f), spec)
                CredentialMetric("Sắp hết", expiringCount, AplusTheme.Yellow, Modifier.weight(1f), spec)
                CredentialMetric("Revoked", revokedCount, AplusTheme.Error, Modifier.weight(1f), spec)
                CredentialMetric("Mở hôm nay", unlockTodayCount, AplusTheme.Blue, Modifier.weight(1f), spec)
            }
        }
        Spacer(Modifier.height(spec.gapMd))

        AplusButton("Thêm mật khẩu", R.drawable.ic_lock, onAdd, spec)
        Spacer(Modifier.height(spec.gapMd))

        if (rows.isEmpty()) {
            EmptyStateCard("Chưa có mật khẩu", "Bấm Thêm mật khẩu để tạo mã mới cho ${lock?.name ?: "khóa đang chọn"}.", R.drawable.ic_lock, spec)
        } else {
            SectionHeader("Danh sách mã", "${rows.size} mục", spec)
            Spacer(Modifier.height(spec.gapSm))
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                rows.forEach { credential ->
                    PasswordCredentialRow(
                        credential = credential,
                        spec = spec,
                        onMockUnlock = { onMockUnlock(credential.id) },
                        onExtend = { onExtend(credential.id) },
                        onRevoke = { onRevoke(credential.id) },
                        onPause = { onPause(credential.id) },
                        onResume = { onResume(credential.id) }
                    )
                }
            }
        }
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote(
            title = "Rule Batch 05",
            body = "Mã trùng trên cùng khóa bị chặn; mã offline chuyển PendingSync; thu hồi khi offline chuyển PendingRevoke; mã một lần chuyển Used sau mock unlock thành công.",
            spec = spec
        )
    }
}

@Composable
private fun PasswordCredentialRow(
    credential: Credential,
    spec: UiScaleConfig,
    onMockUnlock: () -> Unit,
    onExtend: () -> Unit,
    onRevoke: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit
) {
    AplusCard(spec) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(42.dp).clip(CircleShape).background(AplusTheme.Red.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) {
                AplusIcon(R.drawable.ic_lock, AplusTheme.Red, spec.icon)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(credential.name, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${credential.passwordType?.label ?: "Mật khẩu"} • ${credential.ownerName} • ${credential.lockName}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            AplusStatusChip(credential.status.label, statusColor(credential.status), spec)
        }
        Spacer(Modifier.height(spec.gapSm))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            MiniInfoCard("Hiệu lực", "${credential.validFrom} → ${credential.validTo}", R.drawable.ic_report, Modifier.weight(1f), spec)
            MiniInfoCard("Sync", credential.syncState.label, R.drawable.ic_wifi, Modifier.weight(1f), spec)
        }
        Spacer(Modifier.height(spec.gapSm))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            MiniInfoCard("Policy", credential.passwordPolicy ?: "6-10 số", R.drawable.ic_shield, Modifier.weight(1f), spec)
            MiniInfoCard("Lượt dùng", "${credential.usedCount}/${credential.maxUseCount ?: "∞"}", R.drawable.ic_keypad, Modifier.weight(1f), spec)
        }
        credential.riskNote?.let { note ->
            Spacer(Modifier.height(spec.gapSm))
            Text(note, color = AplusTheme.Yellow, fontSize = spec.caption, lineHeight = (spec.caption.value + 3).sp)
        }
        Spacer(Modifier.height(spec.gapSm))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            AplusPillButton("Mở thử", R.drawable.ic_keypad, Modifier.weight(1f), spec, enabled = credential.status == CredentialStatus.Active && credential.syncState == SyncState.Synced, onClick = onMockUnlock)
            AplusPillButton("Gia hạn", R.drawable.ic_report, Modifier.weight(1f), spec, enabled = credential.status != CredentialStatus.Revoked && credential.status != CredentialStatus.Used, onClick = onExtend)
        }
        Spacer(Modifier.height(spec.gapSm))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
            if (credential.status == CredentialStatus.Paused) {
                AplusPillButton("Bật lại", R.drawable.ic_shield, Modifier.weight(1f), spec, enabled = true, onClick = onResume)
            } else {
                AplusPillButton("Tạm dừng", R.drawable.ic_shield, Modifier.weight(1f), spec, enabled = credential.status == CredentialStatus.Active || credential.status == CredentialStatus.Pending, onClick = onPause)
            }
            AplusPillButton("Thu hồi", R.drawable.ic_lock, Modifier.weight(1f), spec, enabled = credential.status != CredentialStatus.Revoked, onClick = onRevoke)
        }
    }
}

@Composable
private fun AddPasswordScreen(
    lock: LockDevice?,
    existing: List<Credential>,
    onBack: () -> Unit,
    onSubmit: (String, String, PermissionRole, String, String, PasswordType, String, String, String?) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var owner by rememberSaveable { mutableStateOf("Khách / Thành viên") }
    var name by rememberSaveable { mutableStateOf("Mã tạm thời") }
    var code by rememberSaveable { mutableStateOf("258369") }
    var validFrom by rememberSaveable { mutableStateOf(defaultValidFromDateTime()) }
    var validTo by rememberSaveable { mutableStateOf(defaultValidToDateTime()) }
    var repeatRule by rememberSaveable { mutableStateOf("T2/T4/T6 • 09:00-12:00") }
    var selectedTypeName by rememberSaveable { mutableStateOf(PasswordType.Temporary.name) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedType = PasswordType.valueOf(selectedTypeName)
    LaunchedEffect(selectedTypeName) {
        validTo = normalizeValidToForPasswordType(validTo, selectedType)
    }
    val lockId = lock?.id.orEmpty()
    val duplicateName = existing.any { it.type == CredentialType.Password && it.lockIds.contains(lockId) && it.name.equals(name, ignoreCase = true) && it.status !in listOf(CredentialStatus.Revoked, CredentialStatus.Expired, CredentialStatus.Used) }

    BaseScreen(title = "Thêm mật khẩu", subtitle = "UI-26 • ${lock?.name ?: "Chưa chọn khóa"}", showBack = true, onBack = onBack, rightIcon = R.drawable.ic_lock, selectedTab = selectedTab, onTab = onTab, scrollable = true) { spec ->
        error?.let { AplusMessageBanner(it, spec) { error = null }; Spacer(Modifier.height(spec.gapSm)) }
        AplusCard(spec) {
            Text("Tạo mã mở cửa", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Chọn đúng loại mã, phạm vi khóa/phòng, người nhận và thời hạn. Mã chỉ hiển thị một lần sau khi tạo.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                    PasswordType.values().take(3).forEach { type ->
                        AplusPillButton(
                            text = type.label.replace("Mã ", ""),
                            icon = R.drawable.ic_keypad,
                            modifier = Modifier.weight(1f),
                            spec = spec,
                            enabled = true,
                            selected = selectedType == type,
                            onClick = {
                                selectedTypeName = type.name
                                validTo = normalizeValidToForPasswordType(validTo, type)
                            }
                        )
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                    PasswordType.values().drop(3).forEach { type ->
                        AplusPillButton(
                            text = type.label.replace("Mã ", ""),
                            icon = R.drawable.ic_keypad,
                            modifier = Modifier.weight(1f),
                            spec = spec,
                            enabled = true,
                            selected = selectedType == type,
                            onClick = {
                                selectedTypeName = type.name
                                validTo = normalizeValidToForPasswordType(validTo, type)
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(spec.gapMd))
            AplusStatusChip("Đang chọn: ${selectedType.label}", AplusTheme.Red, spec)
            Spacer(Modifier.height(spec.gapMd))
            AplusTextField(owner, { owner = it }, "Người nhận / Owner", R.drawable.ic_user, spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(name, { name = it }, "Tên mã", R.drawable.ic_lock, spec)
            Spacer(Modifier.height(spec.gapSm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) {
                    AplusTextField(code, { code = it.filter(Char::isDigit).take(10) }, "Mã số 6-10 số", R.drawable.ic_keypad, spec, KeyboardType.Number, password = true)
                }
                AplusPillButton("Tự sinh", R.drawable.ic_keypad, Modifier.width(92.dp), spec, enabled = true, onClick = { code = (((System.currentTimeMillis() % 900000) + 100000).toString()) })
            }
            Spacer(Modifier.height(spec.gapSm))
            AplusDateTimeField(
                value = validFrom,
                label = "Hiệu lực từ",
                icon = R.drawable.ic_report,
                spec = spec,
                onDateTimeSelected = { validFrom = it }
            )
            Spacer(Modifier.height(spec.gapSm))
            if (selectedType == PasswordType.Cycle) {
                AplusDateField(
                    value = validTo,
                    label = "Hiệu lực đến (ngày)",
                    icon = R.drawable.ic_report,
                    spec = spec,
                    onDateSelected = { validTo = it }
                )
                Spacer(Modifier.height(4.dp))
                Text("Mã chu kỳ dùng giờ trong lịch lặp bên dưới; hiệu lực đến chỉ chọn ngày kết thúc.", color = AplusTheme.Muted, fontSize = spec.caption, lineHeight = (spec.caption.value + 3).sp)
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(repeatRule, { repeatRule = it }, "Lịch lặp / ngày trong tuần + giờ", R.drawable.ic_report, spec)
            } else {
                AplusDateTimeField(
                    value = validTo,
                    label = "Hiệu lực đến",
                    icon = R.drawable.ic_report,
                    spec = spec,
                    onDateTimeSelected = { validTo = it }
                )
            }
            Spacer(Modifier.height(spec.gapMd))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                MiniInfoCard("Phạm vi", lock?.room ?: "Chưa chọn phòng", R.drawable.ic_home, Modifier.weight(1f), spec)
                MiniInfoCard("Khóa", lock?.name ?: "Chưa chọn", R.drawable.ic_lock, Modifier.weight(1f), spec)
            }
            Spacer(Modifier.height(spec.gapMd))
            AplusButton("Lưu mật khẩu", R.drawable.ic_lock, onClick = {
                error = when {
                    lock == null -> "Chưa chọn khóa áp dụng."
                    owner.isBlank() -> "Cần nhập người nhận."
                    name.isBlank() -> "Cần nhập tên mã."
                    code.length !in 6..10 -> "Mã phải có 6-10 số."
                    validFrom.isBlank() || validTo.isBlank() -> "Cần chọn ngày hiệu lực từ/đến."
                    validatePasswordValidityRange(validFrom, validTo, selectedType == PasswordType.Cycle) != null -> validatePasswordValidityRange(validFrom, validTo, selectedType == PasswordType.Cycle)
                    duplicateName -> "Tên mã đã tồn tại trên khóa này."
                    selectedType == PasswordType.Cycle && repeatRule.isBlank() -> "Mã chu kỳ cần lịch lặp."
                    else -> null
                }
                if (error == null && lock != null) {
                    onSubmit(lock.id, owner, PermissionRole.Guest, name, code, selectedType, validFrom, validTo, if (selectedType == PasswordType.Cycle) repeatRule else selectedType.label)
                }
            }, spec = spec)
        }
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote("Chính sách mật khẩu", "Policy hiện tại: 6-10 chữ số, chặn trùng mã trên cùng khóa còn hiệu lực, không lưu plain text lâu dài, mã một lần tự chuyển Used sau khi mock unlock.", spec)
    }
}

@Composable
private fun SimpleCredentialCreateScreen(
    title: String,
    uiCode: String,
    credentialType: CredentialType,
    defaultName: String,
    lock: LockDevice?,
    existing: List<Credential>,
    onBack: () -> Unit,
    onSubmit: (String, CredentialType, String, PermissionRole, String, String, String, String?) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var owner by rememberSaveable(title) { mutableStateOf(defaultOwnerFor(credentialType)) }
    var roleName by rememberSaveable(title) { mutableStateOf(PermissionRole.Member.name) }
    var name by rememberSaveable(title) { mutableStateOf(defaultName) }
    var validFrom by rememberSaveable(title) { mutableStateOf("Hôm nay") }
    var validTo by rememberSaveable(title) { mutableStateOf(if (credentialType == CredentialType.Combination) "30 ngày" else "Không giới hạn") }
    var scanState by rememberSaveable(title) { mutableStateOf("Chưa bắt đầu") }
    var error by rememberSaveable(title) { mutableStateOf<String?>(null) }
    val role = runCatching { PermissionRole.valueOf(roleName) }.getOrDefault(PermissionRole.Member)

    BaseScreen(title = title, subtitle = "$uiCode • ${lock?.name ?: "Chưa chọn khóa"}", showBack = true, onBack = onBack, rightIcon = iconForCredential(credentialType), selectedTab = selectedTab, onTab = onTab, scrollable = true) { spec ->
        error?.let { AplusMessageBanner(it, spec) { error = null }; Spacer(Modifier.height(spec.gapSm)) }
        AplusCard(spec) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text(descriptionForCredential(credentialType), color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                PermissionRole.values().take(4).forEach { item ->
                    AplusPillButton(
                        text = item.label,
                        icon = R.drawable.ic_user,
                        modifier = Modifier.weight(1f),
                        spec = spec,
                        enabled = true,
                        selected = roleName == item.name,
                        onClick = { roleName = item.name }
                    )
                }
            }
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(owner, { owner = it }, "Người sở hữu", R.drawable.ic_user, spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(name, { name = it }, "Tên credential", iconForCredential(credentialType), spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(validFrom, { validFrom = it }, "Hiệu lực từ", R.drawable.ic_report, spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(validTo, { validTo = it }, "Hiệu lực đến", R.drawable.ic_report, spec)
            Spacer(Modifier.height(spec.gapSm))
            TermsRow(scanState != "Chưa bắt đầu", if (credentialType == CredentialType.Card) "Mock quẹt thẻ / nhận CardId" else "Mock ghép thiết bị / token", { scanState = "Đã nhận token mock" }, spec)
            Spacer(Modifier.height(spec.gapMd))
            AplusButton("Hoàn tất", iconForCredential(credentialType), onClick = {
                val duplicated = existing.any { it.type == credentialType && lock != null && it.lockIds.contains(lock.id) && it.name.equals(name, ignoreCase = true) && it.status != CredentialStatus.Revoked }
                error = when {
                    lock == null -> "Chưa chọn khóa áp dụng."
                    owner.isBlank() -> "Cần nhập người sở hữu."
                    name.isBlank() -> "Cần nhập tên credential."
                    duplicated -> "Credential cùng tên đã tồn tại trên khóa này."
                    credentialType != CredentialType.Combination && scanState == "Chưa bắt đầu" -> "Cần chạy mock scan/pair trước khi lưu."
                    else -> null
                }
                if (error == null && lock != null) onSubmit(lock.id, credentialType, owner, role, name, validFrom, validTo, scanState)
            }, spec = spec)
        }
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote("Capability & audit", "Nếu khóa không hỗ trợ ${credentialType.label}, Repository sẽ chặn và trả lỗi; mọi tạo mới đều ghi audit/log.", spec)
    }
}

@Composable
private fun EnrollmentScreen(
    title: String,
    uiCode: String,
    credentialType: CredentialType,
    lock: LockDevice?,
    capabilitySupported: Boolean,
    onBack: () -> Unit,
    onComplete: (String, String, String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var owner by rememberSaveable(title) { mutableStateOf("Thành viên Aplus") }
    var name by rememberSaveable(title) { mutableStateOf(if (credentialType == CredentialType.Face) "Khuôn mặt chính" else "Vân tay ngón trỏ") }
    var step by rememberSaveable(title) { mutableStateOf(0) }
    var error by rememberSaveable(title) { mutableStateOf<String?>(null) }
    val steps = if (credentialType == CredentialType.Face) listOf("Nhìn thẳng", "Quay trái", "Quay phải", "Xác minh") else listOf("Đặt tay", "Scan lần 1", "Scan lần 2", "Scan lần 3")

    BaseScreen(title = title, subtitle = "$uiCode • ${lock?.name ?: "Chưa chọn khóa"}", showBack = true, onBack = onBack, rightIcon = iconForCredential(credentialType), selectedTab = selectedTab, onTab = onTab, scrollable = true) { spec ->
        error?.let { AplusMessageBanner(it, spec) { error = null }; Spacer(Modifier.height(spec.gapSm)) }
        AplusCard(spec) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Enrollment nhiều bước, không lưu dữ liệu sinh trắc học thô trong app; chỉ lưu templateId/token mock.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            AplusTextField(owner, { owner = it }, "Người sở hữu", R.drawable.ic_user, spec, enabled = step == 0)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(name, { name = it }, "Tên credential", iconForCredential(credentialType), spec, enabled = step == 0)
            Spacer(Modifier.height(spec.gapMd))
            Box(Modifier.fillMaxWidth().height(118.dp).clip(RoundedCornerShape(24.dp)).background(AplusTheme.Field).border(BorderStroke(1.dp, if (capabilitySupported) AplusTheme.Red.copy(alpha = 0.45f) else AplusTheme.Yellow), RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AplusIcon(iconForCredential(credentialType), if (capabilitySupported) AplusTheme.Red else AplusTheme.Yellow, spec.icon + 24.dp)
                    Spacer(Modifier.height(8.dp))
                    Text(if (capabilitySupported) steps[step.coerceIn(0, steps.lastIndex)] else "Khóa không hỗ trợ ${credentialType.label}", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
                    Text("Progress ${(step * 100 / steps.size).coerceAtMost(100)}%", color = AplusTheme.Muted, fontSize = spec.caption)
                }
            }
            Spacer(Modifier.height(spec.gapMd))
            AplusButton(if (step < steps.size) "Tiếp tục scan" else "Lưu credential", iconForCredential(credentialType), onClick = {
                error = when {
                    lock == null -> "Chưa chọn khóa."
                    !capabilitySupported -> "${lock?.name ?: "Khóa này"} không hỗ trợ ${credentialType.label}."
                    owner.isBlank() -> "Cần chọn owner."
                    name.isBlank() -> "Cần đặt tên credential."
                    else -> null
                }
                if (error == null) {
                    if (step < steps.size) step += 1 else lock?.let { onComplete(it.id, owner, name) }
                }
            }, spec = spec)
            if (step > 0) {
                Spacer(Modifier.height(spec.gapSm))
                AplusSecondaryButton("Quét lại bước hiện tại", R.drawable.ic_report, { step = (step - 1).coerceAtLeast(0) }, spec)
            }
        }
    }
}

@Composable
private fun CredentialRow(
    credential: Credential,
    spec: UiScaleConfig,
    onRevoke: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit
) {
    val statusColor = when (credential.status) {
        CredentialStatus.Active -> AplusTheme.Green
        CredentialStatus.Pending -> AplusTheme.Blue
        CredentialStatus.Paused -> AplusTheme.Yellow
        CredentialStatus.Expired -> AplusTheme.Subtle
        CredentialStatus.Revoked -> AplusTheme.Error
        CredentialStatus.Used -> AplusTheme.Blue
        CredentialStatus.FailedSync -> AplusTheme.Error
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.94f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(statusColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                AplusIcon(iconForCredential(credential.type), statusColor, spec.icon)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(credential.name, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${credential.type.label} • ${credential.ownerName} • ${credential.lockName}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            AplusStatusChip(credential.status.label, statusColor, spec)
        }
        Spacer(Modifier.height(8.dp))
        Text("${credential.validFrom} → ${credential.validTo}${credential.scheduleRule?.let { " • $it" } ?: ""}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 2, overflow = TextOverflow.Ellipsis)
        credential.riskNote?.let { Text(it, color = AplusTheme.Yellow, fontSize = spec.caption, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AplusPillButton(if (credential.status == CredentialStatus.Paused) "Bật lại" else "Tạm dừng", R.drawable.ic_shield, Modifier.weight(1f), spec, enabled = credential.status != CredentialStatus.Revoked, onClick = { if (credential.status == CredentialStatus.Paused) onResume() else onPause() })
            AplusPillButton("Thu hồi", R.drawable.ic_lock, Modifier.weight(1f), spec, enabled = credential.status != CredentialStatus.Revoked, onClick = onRevoke)
        }
    }
}

@Composable
private fun EmptyStateCard(title: String, body: String, icon: Int, spec: UiScaleConfig) {
    AplusCard(spec) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(AplusTheme.Red.copy(alpha = 0.15f)).align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
            AplusIcon(icon, AplusTheme.Red, spec.icon + 8.dp)
        }
        Spacer(Modifier.height(spec.gapSm))
        Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Text(body, color = AplusTheme.Muted, fontSize = spec.caption, textAlign = TextAlign.Center, lineHeight = (spec.caption.value + 4).sp, modifier = Modifier.fillMaxWidth())
    }
}

private fun iconForCredential(type: CredentialType): Int = when (type) {
    CredentialType.Password -> R.drawable.ic_lock
    CredentialType.Fingerprint -> R.drawable.ic_fingerprint
    CredentialType.Face -> R.drawable.ic_face
    CredentialType.Card -> R.drawable.ic_card
    CredentialType.Remote -> R.drawable.ic_remote
    CredentialType.PhoneNfc -> R.drawable.ic_nfc
    CredentialType.Combination -> R.drawable.ic_combo
    CredentialType.Admin -> R.drawable.ic_user
}

private fun defaultOwnerFor(type: CredentialType): String = when (type) {
    CredentialType.Card -> "Khách / Nhân viên"
    CredentialType.Remote -> "Bảo vệ / Quản lý"
    CredentialType.PhoneNfc -> "Điện thoại được cấp quyền"
    CredentialType.Combination -> "Nhóm người dùng"
    else -> "Thành viên Aplus"
}

private fun descriptionForCredential(type: CredentialType): String = when (type) {
    CredentialType.Card -> "Mock quẹt thẻ, nhận cardId, gán owner/phòng và trạng thái sync."
    CredentialType.Remote -> "Ghép remote vật lý với khóa/phòng, serial và phạm vi sử dụng."
    CredentialType.PhoneNfc -> "Đăng ký NFC hoặc thẻ điện thoại, có token và thời hạn."
    CredentialType.Combination -> "Tạo rule mở khóa kết hợp như PIN + thẻ, App + vân tay."
    else -> "Tạo credential mới dùng chung repository và audit log."
}


@Composable
private fun PairingGatewayScreen(
    homes: List<vn.aplus.smart.data.HomeInfo>,
    rooms: List<RoomInfo>,
    discoveredDevices: List<DiscoveredDevice>,
    gateways: List<GatewayInfo>,
    onBack: () -> Unit,
    onPair: (PairingMethod, String, String, String, String, String, String, String?) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var methodName by rememberSaveable { mutableStateOf(PairingMethod.Bluetooth.name) }
    val method = PairingMethod.valueOf(methodName)
    var cameraGranted by rememberSaveable { mutableStateOf(false) }
    var bluetoothGranted by rememberSaveable { mutableStateOf(false) }
    var locationGranted by rememberSaveable { mutableStateOf(false) }
    var wifiGranted by rememberSaveable { mutableStateOf(false) }
    var nearbyGranted by rememberSaveable { mutableStateOf(false) }
    var notificationGranted by rememberSaveable { mutableStateOf(false) }
    var scanned by rememberSaveable { mutableStateOf(false) }
    var selectedSerial by rememberSaveable { mutableStateOf("") }
    var selectedModel by rememberSaveable { mutableStateOf("Aplus L520 Pro") }
    var lockName by rememberSaveable { mutableStateOf("Khóa mới Aplus") }
    var selectedRoomId by rememberSaveable { mutableStateOf(rooms.firstOrNull()?.id.orEmpty()) }
    var wifiSsid by rememberSaveable { mutableStateOf("Aplus_Office_5G") }
    var wifiPassword by rememberSaveable { mutableStateOf("") }
    var selectedGatewayId by rememberSaveable { mutableStateOf(gateways.firstOrNull { it.online }?.id ?: gateways.firstOrNull()?.id.orEmpty()) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var scanLog by rememberSaveable { mutableStateOf("Chưa quét thiết bị") }

    val requiredPermissions = requiredPermissionsFor(method)
    val permissionState = mapOf(
        "Camera" to cameraGranted,
        "Bluetooth" to bluetoothGranted,
        "Location" to locationGranted,
        "Wi‑Fi" to wifiGranted,
        "Nearby devices" to nearbyGranted,
        "Notification" to notificationGranted
    )
    val missingPermissions = requiredPermissions.filter { permissionState[it] != true }
    val selectedDevice = discoveredDevices.firstOrNull { it.serial == selectedSerial }

    LaunchedEffect(methodName) {
        scanned = false
        selectedSerial = when (method) {
            PairingMethod.DeviceCode -> "APL-CODE-${(System.currentTimeMillis() % 9000).toString().padStart(4, '0')}"
            PairingMethod.ManualDemo -> "APL-MANUAL-${(System.currentTimeMillis() % 9000).toString().padStart(4, '0')}"
            else -> ""
        }
        selectedModel = when (method) {
            PairingMethod.GatewayMqtt -> "Aplus Hotel H3"
            PairingMethod.Wifi -> "Aplus Gate G1"
            else -> "Aplus L520 Pro"
        }
        lockName = when (method) {
            PairingMethod.GatewayMqtt -> "Khóa gateway mới"
            PairingMethod.Wifi -> "Khóa Wi‑Fi mới"
            PairingMethod.ManualDemo -> "Khóa demo thủ công"
            else -> "Khóa mới Aplus"
        }
        scanLog = "Đã chọn ${method.label}. Chạy preflight rồi quét/mock nhập thiết bị."
    }

    fun runMockScan() {
        if (missingPermissions.isNotEmpty()) {
            error = "Còn thiếu quyền: ${missingPermissions.joinToString()}"
            scanLog = "Preflight fail: thiếu ${missingPermissions.joinToString()}"
            return
        }
        scanned = true
        val candidate = discoveredDevices.firstOrNull { device ->
            when (method) {
                PairingMethod.QrCode -> true
                PairingMethod.Bluetooth -> device.recommendedMethod == PairingMethod.Bluetooth
                PairingMethod.Wifi -> device.recommendedMethod == PairingMethod.Wifi
                PairingMethod.GatewayMqtt -> device.recommendedMethod == PairingMethod.GatewayMqtt
                else -> false
            }
        }
        if (candidate != null) {
            selectedSerial = candidate.serial
            selectedModel = candidate.model
            lockName = candidate.type.replace("Khóa", "Khóa").ifBlank { "Khóa mới Aplus" }
            scanLog = "Đã tìm thấy ${candidate.model} • ${candidate.serial} • RSSI ${candidate.rssi} dBm"
        } else {
            scanLog = "Không có thiết bị phù hợp; có thể dùng nhập thủ công demo."
        }
    }

    fun submit() {
        error = when {
            missingPermissions.isNotEmpty() -> "Còn thiếu quyền: ${missingPermissions.joinToString()}"
            selectedSerial.isBlank() -> "Cần quét hoặc nhập serial/mã thiết bị."
            selectedModel.isBlank() -> "Cần nhập model thiết bị."
            lockName.isBlank() -> "Cần đặt tên khóa."
            selectedRoomId.isBlank() -> "Cần chọn phòng/khu vực."
            selectedDevice?.alreadyBound == true -> "Thiết bị đã được bind với owner khác. Cần reset hoặc chuyển quyền."
            method == PairingMethod.Wifi && (wifiSsid.isBlank() || wifiPassword.length < 8) -> "Wi‑Fi setup cần SSID và mật khẩu tối thiểu 8 ký tự."
            method == PairingMethod.GatewayMqtt && selectedGatewayId.isBlank() -> "Cần chọn gateway để gán khóa."
            method == PairingMethod.GatewayMqtt && gateways.firstOrNull { it.id == selectedGatewayId }?.online == false -> "Gateway đang offline, không thể pairing."
            else -> null
        }
        if (error == null) {
            onPair(method, selectedSerial, selectedModel, lockName, selectedRoomId, wifiSsid, wifiPassword, selectedGatewayId.takeIf { it.isNotBlank() })
        }
    }

    BaseScreen(
        title = "Kết nối Gateway",
        subtitle = "UI-12 • thêm khóa & networking",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_gateway,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true
    ) { spec ->
        error?.let { AplusMessageBanner(it, spec) { error = null }; Spacer(Modifier.height(spec.gapSm)) }

        AplusCard(spec) {
            Text("Pairing Wizard", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Text("Chọn phương thức, kiểm tra quyền, quét thiết bị, cấu hình mạng/gateway rồi tạo LockDevice thật trong repository.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                PairingMethod.values().toList().chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                        row.forEach { item ->
                            AplusPillButton(
                                text = item.shortLabel,
                                icon = iconForPairingMethod(item),
                                modifier = Modifier.weight(1f),
                                spec = spec,
                                selected = method == item,
                                onClick = { methodName = item.name }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(spec.gapSm))
            AplusStatusChip("Đang chọn: ${method.label}", AplusTheme.Red, spec)
        }

        Spacer(Modifier.height(spec.gapMd))
        AplusCard(spec) {
            SectionHeader("Preflight permission", if (missingPermissions.isEmpty()) "Đủ quyền" else "Thiếu ${missingPermissions.size}", spec)
            Spacer(Modifier.height(spec.gapSm))
            Text("Batch 12 không bỏ qua quyền Android mới. Bản demo dùng checkbox để mô phỏng cấp quyền trước khi scan/provision.", color = AplusTheme.Muted, fontSize = spec.caption, lineHeight = (spec.caption.value + 3).sp)
            Spacer(Modifier.height(spec.gapSm))
            PermissionToggle("Camera", requiredPermissions.contains("Camera"), cameraGranted, { cameraGranted = !cameraGranted }, spec)
            PermissionToggle("Bluetooth", requiredPermissions.contains("Bluetooth"), bluetoothGranted, { bluetoothGranted = !bluetoothGranted }, spec)
            PermissionToggle("Location", requiredPermissions.contains("Location"), locationGranted, { locationGranted = !locationGranted }, spec)
            PermissionToggle("Wi‑Fi", requiredPermissions.contains("Wi‑Fi"), wifiGranted, { wifiGranted = !wifiGranted }, spec)
            PermissionToggle("Nearby devices", requiredPermissions.contains("Nearby devices"), nearbyGranted, { nearbyGranted = !nearbyGranted }, spec)
            PermissionToggle("Notification", requiredPermissions.contains("Notification"), notificationGranted, { notificationGranted = !notificationGranted }, spec)
        }

        Spacer(Modifier.height(spec.gapMd))
        AplusCard(spec) {
            SectionHeader("Quét / nhập thiết bị", if (scanned) "Đã scan" else "Chưa scan", spec)
            Spacer(Modifier.height(spec.gapSm))
            Text(scanLog, color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapSm))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                AplusPillButton("Quét mock", iconForPairingMethod(method), Modifier.weight(1f), spec, enabled = method !in listOf(PairingMethod.DeviceCode, PairingMethod.ManualDemo), onClick = ::runMockScan)
                AplusPillButton("Log retry", R.drawable.ic_report, Modifier.weight(1f), spec, onClick = { scanLog = "Retry log: timeout/sai Wi‑Fi/duplicate/gateway offline đều được chặn trước khi tạo lock." })
            }
            Spacer(Modifier.height(spec.gapSm))
            if (scanned && method !in listOf(PairingMethod.DeviceCode, PairingMethod.ManualDemo)) {
                discoveredDevices.forEach { device ->
                    DeviceCandidateRow(device, selected = selectedSerial == device.serial, spec = spec) {
                        selectedSerial = device.serial
                        selectedModel = device.model
                        lockName = device.type.ifBlank { "Khóa mới Aplus" }
                        scanLog = if (device.alreadyBound) "${device.serial} đã bind; không được bind đè." else "Đã chọn ${device.serial}"
                    }
                    Spacer(Modifier.height(spec.gapSm))
                }
            }
            AplusTextField(selectedSerial, { selectedSerial = it.uppercase().take(32) }, "Serial / mã thiết bị", R.drawable.ic_keypad, spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(selectedModel, { selectedModel = it }, "Model thiết bị", R.drawable.ic_lock, spec)
        }

        Spacer(Modifier.height(spec.gapMd))
        if (method == PairingMethod.Wifi || method == PairingMethod.QrCode || method == PairingMethod.Bluetooth) {
            AplusCard(spec) {
                SectionHeader("Wi‑Fi setup", "Provision network", spec)
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(wifiSsid, { wifiSsid = it }, "SSID Wi‑Fi", R.drawable.ic_wifi, spec)
                Spacer(Modifier.height(spec.gapSm))
                AplusTextField(wifiPassword, { wifiPassword = it }, "Mật khẩu Wi‑Fi", R.drawable.ic_shield, spec, password = true)
                Spacer(Modifier.height(spec.gapSm))
                Text("Mật khẩu Wi‑Fi chỉ là mock trong bản demo; production không lưu plain text.", color = AplusTheme.Yellow, fontSize = spec.caption)
            }
            Spacer(Modifier.height(spec.gapMd))
        }

        if (method == PairingMethod.GatewayMqtt) {
            AplusCard(spec) {
                SectionHeader("Gateway/MQTT", "Chọn gateway", spec)
                Spacer(Modifier.height(spec.gapSm))
                gateways.forEach { gateway ->
                    GatewayRow(gateway, selected = selectedGatewayId == gateway.id, spec = spec) { selectedGatewayId = gateway.id }
                    Spacer(Modifier.height(spec.gapSm))
                }
            }
            Spacer(Modifier.height(spec.gapMd))
        }

        AplusCard(spec) {
            SectionHeader("Đặt tên & chọn phòng", "Tạo LockDevice", spec)
            Spacer(Modifier.height(spec.gapSm))
            AplusTextField(lockName, { lockName = it }, "Tên khóa", R.drawable.ic_lock, spec)
            Spacer(Modifier.height(spec.gapSm))
            Text("Chọn phòng/khu vực", color = AplusTheme.Muted, fontSize = spec.label, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(spec.gapXs))
            Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                rooms.chunked(2).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                        row.forEach { room ->
                            val homeName = homes.firstOrNull { it.id == room.homeId }?.name ?: room.homeId
                            AplusPillButton(
                                text = room.roomNo,
                                icon = R.drawable.ic_home,
                                modifier = Modifier.weight(1f),
                                spec = spec,
                                selected = selectedRoomId == room.id,
                                onClick = { selectedRoomId = room.id; lockName = if (lockName.isBlank() || lockName.startsWith("Khóa")) room.name else lockName }
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(spec.gapSm))
            rooms.firstOrNull { it.id == selectedRoomId }?.let { room ->
                val homeName = homes.firstOrNull { it.id == room.homeId }?.name ?: room.homeId
                MiniInfoCard("Phòng chọn", "$homeName • ${room.floor} • ${room.name}", R.drawable.ic_home, Modifier.fillMaxWidth(), spec)
            }
        }

        Spacer(Modifier.height(spec.gapMd))
        AplusButton("Hoàn tất pairing", R.drawable.ic_gateway, onClick = ::submit, spec = spec)
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote("Batch 12 guard", "Chặn serial trùng, thiết bị đã bind, thiếu permission, Wi‑Fi sai policy, gateway offline và không tạo lock nếu pairing thất bại.", spec)
    }
}

private fun requiredPermissionsFor(method: PairingMethod): List<String> = when (method) {
    PairingMethod.QrCode -> listOf("Camera", "Wi‑Fi", "Notification")
    PairingMethod.DeviceCode -> listOf("Notification")
    PairingMethod.Bluetooth -> listOf("Bluetooth", "Nearby devices", "Location", "Notification")
    PairingMethod.Wifi -> listOf("Wi‑Fi", "Location", "Notification")
    PairingMethod.GatewayMqtt -> listOf("Wi‑Fi", "Notification")
    PairingMethod.ManualDemo -> emptyList()
}

private fun iconForPairingMethod(method: PairingMethod): Int = when (method) {
    PairingMethod.QrCode -> R.drawable.ic_qr
    PairingMethod.DeviceCode -> R.drawable.ic_keypad
    PairingMethod.Bluetooth -> R.drawable.ic_bluetooth
    PairingMethod.Wifi -> R.drawable.ic_wifi
    PairingMethod.GatewayMqtt -> R.drawable.ic_gateway
    PairingMethod.ManualDemo -> R.drawable.ic_more
}

@Composable
private fun PermissionToggle(title: String, required: Boolean, granted: Boolean, onToggle: () -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (required && granted) AplusTheme.Red.copy(alpha = 0.22f) else AplusTheme.Field)
            .border(BorderStroke(1.dp, if (required && !granted) AplusTheme.Yellow.copy(alpha = 0.5f) else AplusTheme.Stroke), RoundedCornerShape(14.dp))
            .clickable(enabled = required, onClick = onToggle)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AplusIcon(if (title.contains("Wi")) R.drawable.ic_wifi else if (title.contains("Bluetooth")) R.drawable.ic_bluetooth else if (title.contains("Camera")) R.drawable.ic_qr else R.drawable.ic_shield, if (required && granted) AplusTheme.Red else AplusTheme.Muted, spec.icon)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Bold, fontSize = spec.body)
            Text(if (!required) "Không cần cho phương thức hiện tại" else if (granted) "Đã cấp mock" else "Bấm để cấp mock", color = if (required && !granted) AplusTheme.Yellow else AplusTheme.Muted, fontSize = spec.caption)
        }
        AplusStatusChip(if (!required) "Skip" else if (granted) "OK" else "Thiếu", if (!required) AplusTheme.Subtle else if (granted) AplusTheme.Green else AplusTheme.Yellow, spec)
    }
}

@Composable
private fun DeviceCandidateRow(device: DiscoveredDevice, selected: Boolean, spec: UiScaleConfig, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) AplusTheme.Red.copy(alpha = 0.2f) else AplusTheme.CardDark)
            .border(BorderStroke(1.dp, if (selected) AplusTheme.Red else AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(AplusTheme.Field), contentAlignment = Alignment.Center) {
            AplusIcon(iconForPairingMethod(device.recommendedMethod), if (device.alreadyBound) AplusTheme.Yellow else AplusTheme.Red, spec.icon)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(device.model, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${device.serial} • RSSI ${device.rssi}dBm • ${device.capabilities.joinToString()}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        AplusStatusChip(if (device.alreadyBound) "Bound" else "Ready", if (device.alreadyBound) AplusTheme.Yellow else AplusTheme.Green, spec)
    }
}

@Composable
private fun GatewayRow(gateway: GatewayInfo, selected: Boolean, spec: UiScaleConfig, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) AplusTheme.Red.copy(alpha = 0.2f) else AplusTheme.CardDark)
            .border(BorderStroke(1.dp, if (selected) AplusTheme.Red else AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(AplusTheme.Field), contentAlignment = Alignment.Center) {
            AplusIcon(R.drawable.ic_gateway, if (gateway.online) AplusTheme.Red else AplusTheme.Yellow, spec.icon)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(gateway.name, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("FW ${gateway.firmwareVersion} • sóng ${gateway.signal}/4 • ${gateway.lockIds.size} khóa", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        AplusStatusChip(if (gateway.online) "Online" else "Offline", if (gateway.online) AplusTheme.Green else AplusTheme.Yellow, spec)
    }
}

@Composable
private fun RemoteUnlockScreen(
    lock: LockDevice,
    onBack: () -> Unit,
    onCommandSuccess: () -> Unit,
    onCommandFailed: (String) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var pin by rememberSaveable(lock.id) { mutableStateOf("") }
    var confirmChecked by rememberSaveable(lock.id) { mutableStateOf(false) }
    var phaseName by rememberSaveable(lock.id) { mutableStateOf(CommandPhase.Idle.name) }
    var message by rememberSaveable(lock.id) { mutableStateOf<String?>(null) }
    val phase = CommandPhase.valueOf(phaseName)
    val allowRemoteUnlock = lock.id != "lock-301"

    LaunchedEffect(phaseName, lock.id) {
        when (CommandPhase.valueOf(phaseName)) {
            CommandPhase.Pending -> { delay(450); phaseName = CommandPhase.Sending.name }
            CommandPhase.Sending -> {
                delay(900)
                if (!lock.online) {
                    onCommandFailed("OFFLINE")
                    message = "Khóa offline, command không được gửi."
                    phaseName = CommandPhase.Failed.name
                } else if (!allowRemoteUnlock) {
                    onCommandFailed("REMOTE_DISABLED")
                    message = "Remote unlock đang tắt trong cài đặt khóa."
                    phaseName = CommandPhase.Failed.name
                } else {
                    onCommandSuccess()
                    message = "Remote unlock success: đã tạo AccessRecord method=REMOTE_APP."
                    phaseName = CommandPhase.Success.name
                }
            }
            CommandPhase.Success, CommandPhase.Failed -> { delay(1800); phaseName = CommandPhase.Idle.name }
            else -> Unit
        }
    }

    fun submit() {
        message = when {
            !lock.online -> "Không thể mở từ xa khi khóa offline."
            !allowRemoteUnlock -> "Cài đặt remote unlock của khóa này đang tắt."
            pin != "123456" -> "App PIN sai. PIN mock để test là 123456."
            !confirmChecked -> "Cần xác nhận lại trước khi gửi lệnh mở khóa từ xa."
            phase != CommandPhase.Idle -> "Đang có command pending, không gửi lệnh thứ hai."
            else -> null
        }
        if (message == null) phaseName = CommandPhase.Pending.name
    }

    BaseScreen(
        title = "Mở khóa từ xa",
        subtitle = "UI-30 • ${lock.name}",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_lock,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true,
        isLoading = phase == CommandPhase.Pending || phase == CommandPhase.Sending
    ) { spec ->
        message?.let {
            AplusMessageBanner(it, spec, onClick = { message = null })
            Spacer(Modifier.height(spec.gapSm))
        }
        Box(
            Modifier
                .size(if (spec.logoSize < 70.dp) 88.dp else 108.dp)
                .clip(CircleShape)
                .background(AplusTheme.Red.copy(alpha = 0.16f))
                .border(BorderStroke(1.dp, AplusTheme.Red.copy(alpha = 0.45f)), CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) { AplusIcon(R.drawable.ic_lock, AplusTheme.Red, spec.icon + 18.dp) }
        Spacer(Modifier.height(spec.gapMd))
        AplusCard(spec) {
            Text("Xác thực mở từ xa", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Spacer(Modifier.height(spec.gapXs))
            Text("Lệnh chỉ được gửi khi khóa online, user có quyền, setting cho phép remote unlock và đã xác thực lại.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 4).sp)
            Spacer(Modifier.height(spec.gapMd))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                AplusStatusChip(if (lock.online) "Online" else "Offline", if (lock.online) AplusTheme.Green else AplusTheme.Yellow, spec)
                AplusStatusChip(if (allowRemoteUnlock) "Remote ON" else "Remote OFF", if (allowRemoteUnlock) AplusTheme.Green else AplusTheme.Yellow, spec)
                AplusStatusChip("Command ${phase.name.lowercase()}", if (phase == CommandPhase.Failed) AplusTheme.Error else AplusTheme.Blue, spec)
            }
            Spacer(Modifier.height(spec.gapMd))
            AplusTextField(
                value = pin,
                onValueChange = { pin = it.filter(Char::isDigit).take(6) },
                label = "App PIN / Biometric fallback",
                icon = R.drawable.ic_keypad,
                spec = spec,
                keyboardType = KeyboardType.Number,
                password = true,
                enabled = phase == CommandPhase.Idle
            )
            Spacer(Modifier.height(spec.gapSm))
            TermsRow(checked = confirmChecked, text = "Tôi xác nhận mở khóa từ xa cho ${lock.name}", onClick = { confirmChecked = !confirmChecked }, spec = spec, enabled = phase == CommandPhase.Idle)
            Spacer(Modifier.height(spec.gapMd))
            AplusButton("Gửi lệnh mở từ xa", R.drawable.ic_lock, onClick = ::submit, spec = spec, enabled = phase == CommandPhase.Idle)
        }
        Spacer(Modifier.height(spec.gapMd))
        SecurityNote("Command lifecycle", "pending → sending → success/failed/timeout. UI không đổi trạng thái khóa trước khi command success.", spec)
    }
}


private data class MoreHubAction(
    val group: String,
    val title: String,
    val code: String,
    val icon: Int,
    val route: AppRoute? = null,
    val featureTitle: String = title
)

private val moreHubActions = listOf(
    MoreHubAction("Quản lý quyền", "Phòng", "UI-11", R.drawable.ic_home, featureTitle = "Quản lý phòng"),
    MoreHubAction("Quản lý quyền", "Nhân sự", "UI-08", R.drawable.ic_user, featureTitle = "Nhân sự & khách thuê"),
    MoreHubAction("Quản lý quyền", "Chuyển khóa", "UI-22", R.drawable.ic_remote, featureTitle = "Chuyển quyền khóa"),
    MoreHubAction("Quản lý quyền", "NFC", "UI-15", R.drawable.ic_nfc, AppRoute.NfcPhoneCard, "NFC & thẻ điện thoại"),
    MoreHubAction("Vận hành", "Lịch sử", "UI-10", R.drawable.ic_report, featureTitle = "Lịch sử mở khóa"),
    MoreHubAction("Vận hành", "Pin", "UI-21", R.drawable.ic_battery, featureTitle = "Pin & điện năng"),
    MoreHubAction("Vận hành", "Báo cáo", "UI-17", R.drawable.ic_report, featureTitle = "Báo cáo dữ liệu"),
    MoreHubAction("Vận hành", "Cảnh báo", "UI-19", R.drawable.ic_shield, featureTitle = "Trung tâm báo động"),
    MoreHubAction("Kết nối", "Gateway", "UI-12", R.drawable.ic_gateway, AppRoute.PairingGateway, "Kết nối Gateway"),
    MoreHubAction("Kết nối", "Wi‑Fi", "UI-12", R.drawable.ic_wifi, AppRoute.PairingGateway, "Wi‑Fi setup"),
    MoreHubAction("Kết nối", "Bluetooth", "UI-12", R.drawable.ic_bluetooth, AppRoute.PairingGateway, "Bluetooth nearby"),
    MoreHubAction("Kết nối", "PMS", "UI-14", R.drawable.ic_report, featureTitle = "Apartment / Hotel PMS"),
    MoreHubAction("An toàn", "Tổ hợp", "UI-28", R.drawable.ic_combo, AppRoute.CombinationUnlock, "Mở khóa kết hợp"),
    MoreHubAction("An toàn", "Normally Open", "UI-20", R.drawable.ic_lock, featureTitle = "Mở thường xuyên"),
    MoreHubAction("An toàn", "Cạy khóa", "UI-19", R.drawable.ic_shield, featureTitle = "Cảnh báo cạy khóa"),
    MoreHubAction("An toàn", "Door Alarm", "UI-19", R.drawable.ic_phone, featureTitle = "Cảnh báo cửa")
)

@Composable
private fun MoreHubScreen(
    onBack: () -> Unit,
    onOpenGateway: () -> Unit,
    onOpenFeature: (String, String) -> Unit,
    onOpenRoute: (AppRoute) -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) moreHubActions else moreHubActions.filter {
            it.title.contains(query, ignoreCase = true) ||
                it.featureTitle.contains(query, ignoreCase = true) ||
                it.group.contains(query, ignoreCase = true) ||
                it.code.contains(query, ignoreCase = true)
        }
    }
    BaseScreen(
        title = "More",
        subtitle = "Tính năng nâng cao",
        showBack = true,
        onBack = onBack,
        rightIcon = R.drawable.ic_more,
        selectedTab = selectedTab,
        onTab = onTab,
        scrollable = true
    ) { spec ->
        AplusTextField(
            value = query,
            onValueChange = { query = it },
            label = "Tìm chức năng...",
            icon = R.drawable.ic_more,
            spec = spec
        )
        Spacer(Modifier.height(spec.gapMd))

        val groups = filtered.groupBy { it.group }
        if (filtered.isEmpty()) {
            AplusCard(spec) {
                Text("Không tìm thấy chức năng", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
                Spacer(Modifier.height(spec.gapXs))
                Text("Thử nhập Gateway, Wi‑Fi, phòng, nhân sự, cảnh báo...", color = AplusTheme.Muted, fontSize = spec.caption)
            }
        } else {
            groups.forEach { (group, actions) ->
                Text(group, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
                Spacer(Modifier.height(spec.gapSm))
                actions.chunked(4).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                        row.forEach { action ->
                            MoreHubTile(
                                action = action,
                                modifier = Modifier.weight(1f),
                                spec = spec,
                                onClick = {
                                    val target = action.route
                                    when (target) {
                                        AppRoute.PairingGateway -> onOpenGateway()
                                        null -> onOpenFeature(action.featureTitle, action.code)
                                        else -> onOpenRoute(target)
                                    }
                                }
                            )
                        }
                        repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                    Spacer(Modifier.height(spec.gapSm))
                }
                Spacer(Modifier.height(spec.gapMd))
            }
        }
    }
}

@Composable
private fun MoreHubTile(action: MoreHubAction, modifier: Modifier, spec: UiScaleConfig, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.96f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AplusIcon(action.icon, AplusTheme.Red, spec.icon + 5.dp)
        Spacer(Modifier.height(8.dp))
        Text(action.title, color = AplusTheme.Text, fontWeight = FontWeight.Bold, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
    }
}

@Composable
private fun FeaturePlaceholderScreen(
    title: String,
    uiCode: String,
    onBack: () -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit
) {
    BaseScreen(title = title, subtitle = "$uiCode • đã nối route từ Batch 03", showBack = true, onBack = onBack, selectedTab = selectedTab, onTab = onTab, scrollable = false) { spec ->
        Spacer(Modifier.height(60.dp))
        AplusCard(spec) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
            Spacer(Modifier.height(spec.gapSm))
            Text("Batch 03 đã bỏ toast mù: nút đã mở đúng màn/route. Logic chi tiết của $uiCode sẽ làm ở batch tương ứng theo kế hoạch.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 5).sp)
            Spacer(Modifier.height(spec.gapMd))
            AplusButton("Quay lại Thêm quyền", R.drawable.ic_keypad, onBack, spec)
        }
    }
}

@Composable
private fun PlaceholderScreen(
    tab: MainTab,
    authState: AuthUiState,
    authViewModel: AuthViewModel,
    onBackHome: () -> Unit,
    selectedTab: MainTab,
    onTab: (MainTab) -> Unit,
    message: String? = null
) {
    BaseScreen(title = tab.label, subtitle = if (tab == MainTab.Me) "Tài khoản & phiên đăng nhập" else "Đã giữ route cho 31 màn", selectedTab = selectedTab, onTab = onTab, scrollable = false, isLoading = authState.isLoading) { spec ->
        Spacer(Modifier.height(80.dp))
        AplusCard(spec) {
            if (tab == MainTab.Me) {
                Text("Tài khoản Aplus", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
                Spacer(Modifier.height(spec.gapSm))
                InfoRow("Người dùng", authState.session?.name ?: "Aplus Interior", R.drawable.ic_user, spec)
                Spacer(Modifier.height(spec.gapSm))
                InfoRow("Vai trò", authState.session?.role ?: "owner", R.drawable.ic_shield, spec)
                Spacer(Modifier.height(spec.gapSm))
                Text("Batch 01: logout sẽ xóa session DataStore và token mock, rồi quay về Login.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 5).sp)
                Spacer(Modifier.height(spec.gapMd))
                AplusButton("Đăng xuất", R.drawable.ic_lock, authViewModel::logout, spec, enabled = !authState.isLoading)
            } else {
                Text("Màn ${tab.label}", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle)
                Spacer(Modifier.height(spec.gapSm))
                Text(message ?: "Batch 03 đã hoàn thiện Lock Detail, Thêm quyền mở khóa và Remote Unlock ở mức mock/native. Các flow sâu sẽ nối ở batch tương ứng.", color = AplusTheme.Muted, fontSize = spec.body, lineHeight = (spec.body.value + 5).sp)
                Spacer(Modifier.height(spec.gapMd))
                AplusButton("Quay về Nhà", R.drawable.ic_home, onBackHome, spec)
            }
        }
    }
}

@Composable
private fun AplusLogo(spec: UiScaleConfig) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.aplus_logo),
            contentDescription = "Aplus logo",
            modifier = Modifier
                .size(spec.logoSize)
                .clip(CircleShape)
                .shadow(10.dp, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(spec.gapXs))
        Text("Aplus Lock", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.title, letterSpacing = 0.2.sp)
        Text("Quản lý khóa thông minh cao cấp", color = AplusTheme.Muted, fontSize = spec.caption, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AplusScreenHeader(title: String, subtitle: String, showBack: Boolean, onBack: (() -> Unit)?, rightIcon: Int?, onRight: (() -> Unit)?, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.headerHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(46.dp), contentAlignment = Alignment.CenterStart) {
            if (showBack) CircleIconButton(R.drawable.ic_chevron_left, { onBack?.invoke() }, transparent = true, spec = spec)
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle.isNotBlank()) Text(subtitle, color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Box(Modifier.width(46.dp), contentAlignment = Alignment.CenterEnd) {
            rightIcon?.let { CircleIconButton(it, { onRight?.invoke() }, transparent = false, spec = spec) }
        }
    }
}

@Composable
private fun AplusCard(spec: UiScaleConfig, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(14.dp, RoundedCornerShape(spec.cardCorner), ambientColor = Color.Black.copy(alpha = 0.24f), spotColor = Color.Black.copy(alpha = 0.38f))
            .clip(RoundedCornerShape(spec.cardCorner))
            .background(AplusTheme.Card.copy(alpha = 0.97f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(spec.cardCorner))
            .padding(spec.cardPadding),
        content = content
    )
}

@Composable
private fun AplusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: Int,
    spec: UiScaleConfig,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    error: String? = null,
    enabled: Boolean = true
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(spec.inputHeight)
                .clip(RoundedCornerShape(15.dp))
                .background(if (enabled) AplusTheme.Field else AplusTheme.Field.copy(alpha = 0.55f))
                .border(BorderStroke(1.dp, if (error == null) AplusTheme.Stroke else AplusTheme.Error.copy(alpha = 0.8f)), RoundedCornerShape(15.dp))
                .padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AplusIcon(icon, if (error == null) AplusTheme.Muted else AplusTheme.Error, spec.icon)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(label, color = if (error == null) AplusTheme.Muted else AplusTheme.Error, fontSize = spec.label, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
                    textStyle = TextStyle(color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Bold),
                    cursorBrush = SolidColor(AplusTheme.Red),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        if (error != null) {
            Spacer(Modifier.height(3.dp))
            Text(error, color = AplusTheme.Error, fontSize = spec.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AplusDateTimeField(
    value: String,
    label: String,
    icon: Int,
    spec: UiScaleConfig,
    onDateTimeSelected: (String) -> Unit
) {
    var showPicker by rememberSaveable(label) { mutableStateOf(false) }
    val parsedMillis = parseDateTimeMillis(value) ?: System.currentTimeMillis()
    val parsedCalendar = remember(value) { Calendar.getInstance().apply { timeInMillis = parsedMillis } }
    var hourText by rememberSaveable(label, value) { mutableStateOf(parsedCalendar.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')) }
    var minuteText by rememberSaveable(label, value) { mutableStateOf(parsedCalendar.get(Calendar.MINUTE).toString().padStart(2, '0')) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = parsedMillis)

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(spec.inputHeight)
                .clip(RoundedCornerShape(15.dp))
                .background(AplusTheme.Field)
                .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(15.dp))
                .clickable { showPicker = true }
                .padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AplusIcon(icon, AplusTheme.Red, spec.icon)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(label, color = AplusTheme.Muted, fontSize = spec.label, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value.ifBlank { "Chọn ngày và giờ" }, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("Lịch", color = AplusTheme.Red, fontSize = spec.caption, fontWeight = FontWeight.Black)
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis ?: parsedMillis
                        val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: parsedCalendar.get(Calendar.HOUR_OF_DAY)
                        val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: parsedCalendar.get(Calendar.MINUTE)
                        onDateTimeSelected(formatDateTimeForUi(millis, hour, minute))
                        showPicker = false
                    }
                ) { Text("Chọn", color = AplusTheme.Red, fontWeight = FontWeight.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Hủy", color = AplusTheme.Muted) }
            }
        ) {
            Column(
                Modifier
                    .padding(bottom = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    if (label.contains("từ", ignoreCase = true)) "Sửa hiệu lực từ" else "Sửa hiệu lực đến",
                    color = AplusTheme.Text,
                    fontWeight = FontWeight.Black,
                    fontSize = spec.body,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
                Text(
                    "Ngày/giờ đang chọn: ${formatDateTimeForUi(pickerState.selectedDateMillis ?: parsedMillis, hourText.toIntOrNull()?.coerceIn(0, 23) ?: 0, minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0)}",
                    color = AplusTheme.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = spec.caption,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    AplusTimeInput(hourText, { hourText = it.filter(Char::isDigit).take(2) }, "Giờ", spec, Modifier.weight(1f))
                    Text(":", color = AplusTheme.Text, fontSize = spec.screenTitle, fontWeight = FontWeight.Black)
                    AplusTimeInput(minuteText, { minuteText = it.filter(Char::isDigit).take(2) }, "Phút", spec, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AplusTimeAdjustButton("-1h", spec, Modifier.weight(1f)) {
                        hourText = ((hourText.toIntOrNull() ?: 0) - 1).floorMod24().toString().padStart(2, '0')
                    }
                    AplusTimeAdjustButton("+1h", spec, Modifier.weight(1f)) {
                        hourText = ((hourText.toIntOrNull() ?: 0) + 1).floorMod24().toString().padStart(2, '0')
                    }
                    AplusTimeAdjustButton("-15p", spec, Modifier.weight(1f)) {
                        val updated = ((minuteText.toIntOrNull() ?: 0) - 15).floorMod60()
                        minuteText = updated.toString().padStart(2, '0')
                    }
                    AplusTimeAdjustButton("+15p", spec, Modifier.weight(1f)) {
                        val updated = ((minuteText.toIntOrNull() ?: 0) + 15).floorMod60()
                        minuteText = updated.toString().padStart(2, '0')
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AplusTimeAdjustButton("Giờ hiện tại", spec, Modifier.weight(1f)) {
                        val now = Calendar.getInstance()
                        hourText = now.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
                        minuteText = now.get(Calendar.MINUTE).toString().padStart(2, '0')
                    }
                    AplusTimeAdjustButton("12 giờ sau", spec, Modifier.weight(1f)) {
                        val later = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 12) }
                        hourText = later.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
                        minuteText = later.get(Calendar.MINUTE).toString().padStart(2, '0')
                    }
                }
                Text("Bấm các nút +/- hoặc nhập trực tiếp giờ/phút, rồi chọn ngày ở lịch bên dưới.", color = AplusTheme.Muted, fontSize = spec.caption, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
                DatePicker(state = pickerState)
            }
        }
    }
}

@Composable
private fun AplusTimeAdjustButton(
    text: String,
    spec: UiScaleConfig,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(AplusTheme.Red.copy(alpha = 0.16f))
            .border(BorderStroke(1.dp, AplusTheme.Red.copy(alpha = 0.42f)), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = AplusTheme.Red, fontSize = spec.caption, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
    }
}

private fun Int.floorMod24(): Int = ((this % 24) + 24) % 24
private fun Int.floorMod60(): Int = ((this % 60) + 60) % 60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AplusDateField(
    value: String,
    label: String,
    icon: Int,
    spec: UiScaleConfig,
    onDateSelected: (String) -> Unit
) {
    var showPicker by rememberSaveable(label) { mutableStateOf(false) }
    val parsedMillis = parseDateOnlyStartMillis(value) ?: parseDateTimeMillis(value) ?: System.currentTimeMillis()
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = parsedMillis)

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(spec.inputHeight)
                .clip(RoundedCornerShape(15.dp))
                .background(AplusTheme.Field)
                .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(15.dp))
                .clickable { showPicker = true }
                .padding(horizontal = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AplusIcon(icon, AplusTheme.Red, spec.icon)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(label, color = AplusTheme.Muted, fontSize = spec.label, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(value.ifBlank { "Chọn ngày" }, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text("Lịch", color = AplusTheme.Red, fontSize = spec.caption, fontWeight = FontWeight.Black)
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis ?: parsedMillis
                        onDateSelected(formatDateForUi(millis))
                        showPicker = false
                    }
                ) { Text("Chọn", color = AplusTheme.Red, fontWeight = FontWeight.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Hủy", color = AplusTheme.Muted) }
            }
        ) {
            Column(Modifier.padding(bottom = 12.dp)) {
                DatePicker(state = pickerState)
                Text("Chỉ chọn ngày kết thúc. Giờ mở của mã chu kỳ lấy theo lịch lặp.", color = AplusTheme.Muted, fontSize = spec.caption, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
private fun AplusTimeInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    spec: UiScaleConfig,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, color = AplusTheme.Muted, fontSize = spec.caption, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(AplusTheme.Field)
                .border(BorderStroke(1.dp, AplusTheme.Red.copy(alpha = 0.35f)), RoundedCornerShape(13.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Black),
                cursorBrush = SolidColor(AplusTheme.Red),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private const val APLUS_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm"
private const val APLUS_DATE_PATTERN = "dd/MM/yyyy"

private fun defaultValidFromDateTime(): String = SimpleDateFormat(APLUS_DATE_TIME_PATTERN, Locale("vi", "VN")).format(Date())

private fun defaultValidToDateTime(): String = dateTimePlusHoursForUi(12)

private fun dateTimePlusHoursForUi(hours: Int): String {
    val calendar = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, hours) }
    return SimpleDateFormat(APLUS_DATE_TIME_PATTERN, Locale("vi", "VN")).format(calendar.time)
}

private fun parseDateTimeMillis(value: String): Long? {
    return runCatching {
        SimpleDateFormat(APLUS_DATE_TIME_PATTERN, Locale("vi", "VN")).apply { isLenient = false }.parse(value)?.time
    }.getOrNull()
}

private fun parseDateOnlyStartMillis(value: String): Long? {
    return runCatching {
        SimpleDateFormat(APLUS_DATE_PATTERN, Locale("vi", "VN")).apply { isLenient = false }.parse(value)?.time
    }.getOrNull()
}

private fun parseDateOnlyEndMillis(value: String): Long? {
    val start = parseDateOnlyStartMillis(value) ?: return null
    return Calendar.getInstance().apply {
        timeInMillis = start
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}

private fun normalizeValidToForPasswordType(value: String, type: PasswordType): String {
    return if (type == PasswordType.Cycle) {
        val millis = parseDateTimeMillis(value) ?: parseDateOnlyStartMillis(value) ?: Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 12) }.timeInMillis
        formatDateForUi(millis)
    } else {
        if (parseDateTimeMillis(value) != null) {
            value
        } else {
            val millis = parseDateOnlyStartMillis(value)
            if (millis != null) formatDateTimeForUi(millis, 23, 59) else defaultValidToDateTime()
        }
    }
}

private fun validatePasswordValidityRange(validFrom: String, validTo: String, dateOnlyValidTo: Boolean): String? {
    val fromMillis = parseDateTimeMillis(validFrom) ?: return "Hiệu lực từ phải theo dạng dd/MM/yyyy HH:mm."
    val toMillis = if (dateOnlyValidTo) {
        parseDateOnlyEndMillis(validTo) ?: return "Hiệu lực đến của mã chu kỳ chỉ cần ngày, theo dạng dd/MM/yyyy."
    } else {
        parseDateTimeMillis(validTo) ?: return "Hiệu lực đến phải theo dạng dd/MM/yyyy HH:mm."
    }
    val now = System.currentTimeMillis()
    if (fromMillis < now - 60_000L) return "Hiệu lực từ không được là thời điểm trong quá khứ."
    if (toMillis <= now) return "Hiệu lực đến không được là ngày/thời điểm trong quá khứ."
    if (toMillis <= fromMillis) return "Hiệu lực đến phải sau hiệu lực từ."
    return null
}

private fun formatDateTimeForUi(dateMillis: Long, hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
        set(Calendar.MINUTE, minute.coerceIn(0, 59))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return SimpleDateFormat(APLUS_DATE_TIME_PATTERN, Locale("vi", "VN")).format(calendar.time)
}

private fun formatDateForUi(millis: Long): String {
    return SimpleDateFormat(APLUS_DATE_PATTERN, Locale("vi", "VN")).format(Date(millis))
}

@Composable
private fun AplusButton(text: String, icon: Int, onClick: () -> Unit, spec: UiScaleConfig, enabled: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.buttonHeight)
            .clip(RoundedCornerShape(15.dp))
            .background(if (enabled) AplusTheme.Red else AplusTheme.Subtle)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AplusIcon(icon, Color.White, spec.icon)
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.White, fontWeight = FontWeight.Black, fontSize = spec.button, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AplusSecondaryButton(text: String, icon: Int, onClick: () -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.buttonHeight)
            .clip(RoundedCornerShape(15.dp))
            .background(AplusTheme.Field)
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(15.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AplusIcon(icon, AplusTheme.Text, spec.icon)
        Spacer(Modifier.width(9.dp))
        Text(text, color = AplusTheme.Text, fontWeight = FontWeight.ExtraBold, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AplusPillButton(
    text: String,
    icon: Int,
    modifier: Modifier,
    spec: UiScaleConfig,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val background = when {
        !enabled -> AplusTheme.Field.copy(alpha = 0.55f)
        selected -> AplusTheme.Red
        else -> AplusTheme.Field
    }
    val foreground = if (selected && enabled) Color.White else AplusTheme.Text
    Row(
        modifier = modifier
            .height(if (spec.buttonHeight < 42.dp) 38.dp else spec.buttonHeight - 4.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .border(BorderStroke(1.dp, if (selected && enabled) AplusTheme.Red else AplusTheme.Stroke), RoundedCornerShape(15.dp))
            .clickable(enabled = enabled && onClick != null) { onClick?.invoke() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AplusIcon(icon, foreground, spec.icon)
        Spacer(Modifier.width(8.dp))
        Text(text, color = foreground, fontWeight = FontWeight.Bold, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun TermsRow(checked: Boolean, text: String, onClick: () -> Unit, spec: UiScaleConfig, error: String? = null, enabled: Boolean = true) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AplusTheme.Field)
                .border(BorderStroke(1.dp, if (error == null) AplusTheme.Stroke else AplusTheme.Error), RoundedCornerShape(14.dp))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (checked) AplusTheme.Red else Color.Transparent)
                    .border(BorderStroke(1.dp, if (checked) AplusTheme.Red else AplusTheme.Muted), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) { if (checked) Text("✓", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp) }
            Spacer(Modifier.width(10.dp))
            Text(text, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.SemiBold)
        }
        if (error != null) {
            Spacer(Modifier.height(3.dp))
            Text(error, color = AplusTheme.Error, fontSize = spec.label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
private fun MiniInfoCard(title: String, subtitle: String, icon: Int, modifier: Modifier, spec: UiScaleConfig) {
    Column(
        modifier = modifier
            .height(if (spec.logoSize < 70.dp) 66.dp else 78.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.88f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .padding(9.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(Modifier.size(25.dp).clip(CircleShape).background(AplusTheme.Red.copy(alpha = 0.17f)), contentAlignment = Alignment.Center) {
            AplusIcon(icon, AplusTheme.Red, 14.dp)
        }
        Column {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = AplusTheme.Muted, fontSize = (spec.caption.value - 1).sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SecurityNote(title: String, body: String, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AplusTheme.CardDark)
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AplusIcon(R.drawable.ic_shield, AplusTheme.Green, spec.icon + 3.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
            Text(body, color = AplusTheme.Muted, fontSize = spec.caption, lineHeight = (spec.caption.value + 3).sp)
        }
    }
}

@Composable
private fun DashboardSummaryRow(state: HomeUiState, spec: UiScaleConfig) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
        DashboardMetric("Offline", state.offlineCount.toString(), R.drawable.ic_wifi, if (state.offlineCount > 0) AplusTheme.Yellow else AplusTheme.Green, Modifier.weight(1f), spec)
        DashboardMetric("Pin yếu", state.lowBatteryCount.toString(), R.drawable.ic_shield, if (state.lowBatteryCount > 0) AplusTheme.Yellow else AplusTheme.Green, Modifier.weight(1f), spec)
        DashboardMetric("Đang mở", state.unlockedCount.toString(), R.drawable.ic_lock, if (state.unlockedCount > 0) AplusTheme.Blue else AplusTheme.Green, Modifier.weight(1f), spec)
        DashboardMetric("Cảnh báo", state.alertCount.toString(), R.drawable.ic_report, if (state.alertCount > 0) AplusTheme.Red else AplusTheme.Green, Modifier.weight(1f), spec)
    }
}

@Composable
private fun DashboardMetric(title: String, value: String, icon: Int, color: Color, modifier: Modifier, spec: UiScaleConfig) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.92f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(16.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AplusIcon(icon, color, 17.dp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1)
        Text(title, color = AplusTheme.Muted, fontSize = (spec.caption.value - 1).sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SearchBox(value: String, onValueChange: (String) -> Unit, placeholder: String, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.inputHeight)
            .clip(RoundedCornerShape(15.dp))
            .background(AplusTheme.Field)
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(15.dp))
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AplusIcon(R.drawable.ic_keypad, AplusTheme.Muted, spec.icon)
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Bold),
            cursorBrush = SolidColor(AplusTheme.Red),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                if (value.isBlank()) Text(placeholder, color = AplusTheme.Subtle, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
                inner()
            }
        )
        if (value.isNotBlank()) {
            Text(
                "Xóa",
                color = AplusTheme.Red,
                fontWeight = FontWeight.Black,
                fontSize = spec.caption,
                modifier = Modifier.clickable { onValueChange("") }.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun RiskFilterRow(selected: RiskFilter, onSelect: (RiskFilter) -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RiskFilter.values().forEach { filter ->
            val active = selected == filter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) AplusTheme.Red else AplusTheme.CardDark)
                    .border(BorderStroke(1.dp, if (active) AplusTheme.Red else AplusTheme.Stroke), RoundedCornerShape(999.dp))
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 13.dp, vertical = 8.dp)
            ) {
                Text(filter.label, color = if (active) Color.White else AplusTheme.Muted, fontSize = spec.caption, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HeroLockCard(lock: LockDevice, onOpen: () -> Unit, spec: UiScaleConfig) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spec.cardCorner))
            .background(
                Brush.horizontalGradient(
                    listOf(AplusTheme.Card.copy(alpha = 0.98f), AplusTheme.RedDark.copy(alpha = 0.62f))
                )
            )
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(spec.cardCorner))
            .clickable(onClick = onOpen)
            .padding(spec.cardPadding)
    ) {
        Column(Modifier.fillMaxWidth().padding(end = if (spec.logoSize < 70.dp) 70.dp else 92.dp)) {
            Text(lock.name, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.screenTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${lock.homeName} • ${lock.room} • ${lock.kind.label}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(spec.gapSm))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                AplusStatusChip(if (lock.online) "Online" else "Offline", if (lock.online) AplusTheme.Green else AplusTheme.Yellow, spec)
                AplusStatusChip(lock.lockStatusLabel, if (lock.locked) AplusTheme.Red else AplusTheme.Blue, spec)
                AplusStatusChip("Pin ${lock.battery}%", if (lock.battery <= 20) AplusTheme.Yellow else AplusTheme.Green, spec)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(if (spec.logoSize < 70.dp) 64.dp else 82.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AplusTheme.CardDark.copy(alpha = 0.82f)),
            contentAlignment = Alignment.Center
        ) { AplusIcon(R.drawable.ic_keypad, AplusTheme.Red, spec.icon + 14.dp) }
    }
}

@Composable
private fun FilterRow(selected: BuildingType, onSelect: (BuildingType) -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BuildingType.values().forEach { type ->
            val active = selected == type
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) AplusTheme.Red else AplusTheme.CardDark)
                    .border(BorderStroke(1.dp, if (active) AplusTheme.Red else AplusTheme.Stroke), RoundedCornerShape(999.dp))
                    .clickable { onSelect(type) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(type.label, color = if (active) Color.White else AplusTheme.Muted, fontSize = spec.caption, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, meta: String, spec: UiScaleConfig) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
        Spacer(Modifier.weight(1f))
        Text(meta, color = AplusTheme.Muted, fontWeight = FontWeight.Bold, fontSize = spec.caption)
    }
}

@Composable
private fun LockListItem(lock: LockDevice, onClick: () -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.92f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(14.dp))
                .background((if (lock.online) AplusTheme.Green else AplusTheme.Yellow).copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) { AplusIcon(R.drawable.ic_lock, if (lock.online) AplusTheme.Green else AplusTheme.Yellow, spec.icon) }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(lock.name, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (lock.alertBadges.isNotEmpty()) AplusStatusChip(lock.alertBadges.first(), AplusTheme.Yellow, spec)
            }
            Spacer(Modifier.height(3.dp))
            Text("${lock.homeName} • ${lock.room} • ${lock.kind.label}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                AplusStatusChip(if (lock.online) "Online" else "Offline", if (lock.online) AplusTheme.Green else AplusTheme.Yellow, spec)
                AplusStatusChip(lock.lockStatusLabel, if (lock.locked) AplusTheme.Green else AplusTheme.Blue, spec)
                AplusStatusChip(lock.doorStatusLabel, if (lock.doorOpen) AplusTheme.Yellow else AplusTheme.Green, spec)
                AplusStatusChip("Pin ${lock.battery}%", if (lock.battery <= 20) AplusTheme.Yellow else AplusTheme.Green, spec)
                AplusStatusChip("Sóng ${lock.signal}/4", AplusTheme.Blue, spec)
            }
        }
    }
}

private data class QuickAccessTarget(
    val title: String,
    val code: String,
    val icon: Int,
    val route: AppRoute
)

@Composable
private fun QuickAccessGrid(spec: UiScaleConfig, onOpen: (AppRoute) -> Unit) {
    val items = listOf(
        QuickAccessTarget("Mật khẩu", "UI-03", R.drawable.ic_keypad, AppRoute.PasswordManager),
        QuickAccessTarget("Thẻ", "UI-09", R.drawable.ic_card, AppRoute.CardManager),
        QuickAccessTarget("Nhân sự", "UI-08", R.drawable.ic_user, AppRoute.FeaturePlaceholder),
        QuickAccessTarget("Cảnh báo", "UI-19", R.drawable.ic_shield, AppRoute.FeaturePlaceholder),
        QuickAccessTarget("Gateway", "UI-12", R.drawable.ic_gateway, AppRoute.PairingGateway),
        QuickAccessTarget("Cài đặt", "UI-29", R.drawable.ic_globe, AppRoute.FeaturePlaceholder)
    )
    Column(verticalArrangement = Arrangement.spacedBy(spec.gapSm)) {
        items.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(spec.gapSm)) {
                row.forEach { item ->
                    QuickAccessItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        spec = spec,
                        onClick = { onOpen(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessItem(item: QuickAccessTarget, modifier: Modifier, spec: UiScaleConfig, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AplusTheme.CardDark)
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(30.dp).clip(CircleShape).background(AplusTheme.Red.copy(alpha = 0.16f)), contentAlignment = Alignment.Center) { AplusIcon(item.icon, AplusTheme.Red, 15.dp) }
        Spacer(Modifier.height(6.dp))
        Text(item.title, color = AplusTheme.Text, fontWeight = FontWeight.Bold, fontSize = spec.caption, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(item.code, color = AplusTheme.Subtle, fontSize = (spec.caption.value - 1).sp, maxLines = 1)
    }
}

@Composable
private fun QuickActionPanel(spec: UiScaleConfig, onAction: (QuickAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(AplusTheme.WhitePanel)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        lockDetailQuickActions.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { action ->
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onAction(action) }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AplusIcon(action.icon, AplusTheme.InkOnWhite.copy(alpha = 0.72f), spec.icon)
                        Spacer(Modifier.height(5.dp))
                        Text(action.title, color = AplusTheme.InkOnWhite, fontSize = (spec.caption.value - 1).sp, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun DeviceInfoCard(lock: LockDevice, spec: UiScaleConfig) {
    AplusCard(spec) {
        Text("Thông tin thiết bị", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
        Spacer(Modifier.height(spec.gapSm))
        InfoRow("Model", lock.model, R.drawable.ic_shield, spec)
        Spacer(Modifier.height(spec.gapSm))
        InfoRow("Serial", lock.serial, R.drawable.ic_report, spec)
        Spacer(Modifier.height(spec.gapSm))
        InfoRow("Firmware", lock.firmwareVersion, R.drawable.ic_wifi, spec)
        Spacer(Modifier.height(spec.gapSm))
        InfoRow("Cập nhật cuối", lock.lastSeen, R.drawable.ic_globe, spec)
    }
}

@Composable
private fun LastEventCard(lock: LockDevice, spec: UiScaleConfig) {
    AplusCard(spec) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AplusIcon(R.drawable.ic_report, AplusTheme.Red, spec.icon)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("Bản ghi gần nhất", color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
                Text("${lock.name} • ${lock.lockStatusLabel} • ${lock.lastSeen}", color = AplusTheme.Muted, fontSize = spec.caption, maxLines = 2)
            }
            AplusStatusChip("Batch 03", AplusTheme.Blue, spec)
        }
    }
}

@Composable
private fun AplusBottomTab(selected: MainTab, onTab: (MainTab) -> Unit, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(spec.bottomBarHeight)
            .clip(RoundedCornerShape(22.dp))
            .background(AplusTheme.CardDark.copy(alpha = 0.98f))
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(22.dp))
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MainTab.values().forEach { tab ->
            val active = selected == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (active) AplusTheme.Red else Color.Transparent)
                    .clickable { onTab(tab) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AplusIcon(tab.icon, if (active) Color.White else AplusTheme.Subtle, 17.dp)
                Spacer(Modifier.height(3.dp))
                Text(tab.label, color = if (active) Color.White else AplusTheme.Subtle, fontSize = spec.caption, fontWeight = if (active) FontWeight.Black else FontWeight.Medium, maxLines = 1)
            }
        }
    }
}

@Composable
private fun AplusStatusChip(text: String, color: Color, spec: UiScaleConfig) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.28f)), RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) { Text(text, color = color, fontWeight = FontWeight.Black, fontSize = (spec.caption.value - 0.5f).sp, maxLines = 1) }
}

@Composable
private fun CircleIconButton(icon: Int, onClick: () -> Unit, transparent: Boolean, spec: UiScaleConfig) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (transparent) Color.Transparent else AplusTheme.CardDark.copy(alpha = 0.86f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { AplusIcon(icon, AplusTheme.Text, spec.icon + 2.dp) }
}

@Composable
private fun FloatingScannerButton(spec: UiScaleConfig, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(if (spec.logoSize < 70.dp) 52.dp else 58.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { AplusIcon(R.drawable.ic_keypad, Color.White, spec.icon + 8.dp) }
}

@Composable
private fun AplusErrorBanner(message: String, spec: UiScaleConfig) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AplusTheme.Error.copy(alpha = 0.13f))
            .border(BorderStroke(1.dp, AplusTheme.Error.copy(alpha = 0.35f)), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) { Text(message, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.SemiBold) }
}


@Composable
private fun InfoRow(title: String, value: String, icon: Int, spec: UiScaleConfig) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AplusTheme.Field)
            .border(BorderStroke(1.dp, AplusTheme.Stroke), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(AplusTheme.Red.copy(alpha = 0.14f)), contentAlignment = Alignment.Center) {
            AplusIcon(icon, AplusTheme.Red, spec.icon + 3.dp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AplusTheme.Muted, fontSize = spec.label, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(value, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun AuthMessage(message: String?, spec: UiScaleConfig) {
    if (!message.isNullOrBlank()) {
        Spacer(Modifier.height(spec.gapSm))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (message.contains("thành công", true) || message.contains("OTP", true)) AplusTheme.Green.copy(alpha = 0.12f) else AplusTheme.Error.copy(alpha = 0.12f))
                .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 9.dp)
        ) { Text(message, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun AplusMessageBanner(message: String, spec: UiScaleConfig, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AplusTheme.Green.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, AplusTheme.Green.copy(alpha = 0.32f)), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) { Text(message, color = AplusTheme.Text, fontSize = spec.body, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun EmptyState(title: String, body: String, spec: UiScaleConfig) {
    AplusCard(spec) {
        Text(title, color = AplusTheme.Text, fontWeight = FontWeight.Black, fontSize = spec.body)
        Spacer(Modifier.height(4.dp))
        Text(body, color = AplusTheme.Muted, fontSize = spec.caption, lineHeight = (spec.caption.value + 4).sp)
    }
}

@Composable
private fun ConfirmDialog(title: String, message: String, confirmText: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Black) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText, color = AplusTheme.Red, fontWeight = FontWeight.Black) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
private fun AplusIcon(icon: Int, color: Color, size: Dp) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier.size(size),
        colorFilter = ColorFilter.tint(color)
    )
}
