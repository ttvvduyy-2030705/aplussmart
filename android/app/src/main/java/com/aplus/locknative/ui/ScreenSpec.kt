package com.aplus.locknative.ui

import com.aplus.locknative.R
import com.aplus.locknative.domain.CredentialType

data class ScreenSpec(
    val index: Int,
    val route: String,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val referenceImage: String,
    val primaryAction: String,
    val bullets: List<String>,
    val credentialType: CredentialType? = null,
)

val screenSpecs = listOf(
    ScreenSpec(0, "login", "Aplus Lock", "Đăng nhập hệ thống khóa thông minh", R.drawable.ic_flaticon_lock, "00_login.png", "Đăng nhập", listOf("Email / số điện thoại", "Mật khẩu", "Ghi nhớ thiết bị")),
    ScreenSpec(1, "register", "Tạo tài khoản", "Đăng ký Owner đầu tiên", R.drawable.ic_flaticon_admin, "01_register.png", "Tạo tài khoản", listOf("Thông tin chủ nhà", "Xác minh số điện thoại", "Tạo mật khẩu an toàn")),
    ScreenSpec(2, "forgot_password", "Khôi phục mật khẩu", "Lấy lại quyền truy cập app", R.drawable.ic_flaticon_password, "02_forgot_password.png", "Gửi mã OTP", listOf("Nhập số điện thoại/email", "Nhận mã OTP", "Đặt mật khẩu mới")),
    ScreenSpec(3, "home_dashboard", "Nhà của tôi", "Tổng quan khóa, phòng và cảnh báo", R.drawable.ic_flaticon_home, "03_home_dashboard.png", "+ Thêm khóa", listOf("Trạng thái realtime", "Pin và tín hiệu", "Truy cập nhanh 31 chức năng")),
    ScreenSpec(4, "lock_detail", "Chi tiết khóa", "Điều khiển khóa và xem trạng thái", R.drawable.ic_flaticon_lock, "04_lock_detail.png", "Giữ để mở khóa", listOf("Lock/unlock an toàn", "Trạng thái cửa", "Pin, sóng, firmware")),
    ScreenSpec(5, "add_key_menu", "Thêm phương thức mở", "Chọn loại chìa khóa điện tử", R.drawable.ic_flaticon_unlock, "05_add_key_menu.png", "Chọn phương thức", listOf("Mật khẩu", "Vân tay", "Thẻ", "Khuôn mặt", "Remote", "NFC")),
    ScreenSpec(6, "add_password", "Tạo mật khẩu", "Mã PIN, mã tạm, mã chu kỳ", R.drawable.ic_flaticon_password, "06_add_password.png", "Tạo mã", listOf("Mã 6-8 số", "Theo ngày/giờ", "Tự hết hạn"), CredentialType.Password),
    ScreenSpec(7, "password_management", "Quản lý mật khẩu", "Xem, tạm dừng và thu hồi mã", R.drawable.ic_flaticon_password, "07_password_management.png", "Đồng bộ mã", listOf("Mã đang hoạt động", "Mã hết hạn", "Thu hồi từ xa"), CredentialType.Password),
    ScreenSpec(8, "add_fingerprint", "Thêm vân tay", "Ghi danh vân tay cho người dùng", R.drawable.ic_flaticon_fingerprint, "08_add_fingerprint.png", "Bắt đầu ghi danh", listOf("Quét nhiều lần", "Gán người dùng", "Đồng bộ xuống khóa"), CredentialType.Fingerprint),
    ScreenSpec(9, "add_face", "Thêm khuôn mặt", "Face unlock cho khóa hỗ trợ camera", R.drawable.ic_flaticon_face, "09_add_face.png", "Quét khuôn mặt", listOf("Chụp nhiều góc", "Xác thực chủ nhà", "Áp dụng cho khóa hỗ trợ face"), CredentialType.Face),
    ScreenSpec(10, "add_card", "Thêm thẻ", "Thẻ từ/campus/hotel card", R.drawable.ic_flaticon_card, "10_add_card.png", "Quét thẻ", listOf("Quẹt thẻ gần khóa", "Gán phòng", "Thiết lập thời hạn"), CredentialType.Card),
    ScreenSpec(11, "add_remote_control", "Thêm remote", "Remote control unlocking", R.drawable.ic_flaticon_remote, "11_add_remote_control.png", "Ghép remote", listOf("Bấm nút remote", "Xác nhận tín hiệu", "Gán khóa/phòng"), CredentialType.Remote),
    ScreenSpec(12, "phone_authorization", "Cấp quyền điện thoại", "App unlock cho người thân/nhân viên", R.drawable.ic_flaticon_phone, "12_phone_authorization.png", "Gửi lời mời", listOf("Owner/Admin/Member/Guest", "Một người nhiều phòng", "Một phòng nhiều người"), CredentialType.Phone),
    ScreenSpec(13, "sub_admin", "Admin phụ", "Ủy quyền quản trị theo nhà/phòng", R.drawable.ic_flaticon_admin, "13_sub_admin.png", "Thêm admin", listOf("Phân quyền theo vai trò", "Giới hạn phòng", "Nhật ký thao tác"), CredentialType.Admin),
    ScreenSpec(14, "remote_unlock", "Mở khóa từ xa", "Remote unlock qua backend realtime", R.drawable.ic_flaticon_unlock, "14_remote_unlock.png", "Mở khóa từ xa", listOf("Xác thực owner", "Gửi lệnh backend", "Nhận event xác nhận")),
    ScreenSpec(15, "settings", "Cài đặt khóa", "Cấu hình thiết bị và an toàn", R.drawable.ic_flaticon_settings, "15_settings.png", "Lưu cài đặt", listOf("Auto-lock", "Âm báo", "Firmware", "Quyền remote")),
    ScreenSpec(16, "more_hub", "Trung tâm chức năng", "Các tính năng khách sạn/căn hộ nâng cao", R.drawable.ic_flaticon_device, "16_more_hub.png", "Mở chức năng", listOf("Records", "Battery", "Transfer", "PMS", "Gateway", "Analytics")),
    ScreenSpec(17, "unlock_records", "Lịch sử mở khóa", "Record reporting theo catalog", R.drawable.ic_flaticon_records, "17_unlock_records.png", "Tải lịch sử", listOf("Theo khóa", "Theo người", "Theo phương thức", "Xuất báo cáo")),
    ScreenSpec(18, "electricity_reporting", "Báo pin", "Electricity reporting và pin yếu", R.drawable.ic_flaticon_battery, "18_electricity_reporting.png", "Kiểm tra pin", listOf("Pin theo khóa", "Cảnh báo pin yếu", "Dự đoán thay pin")),
    ScreenSpec(19, "lock_transfer", "Chuyển quyền khóa", "Lock transfer cho Owner/Admin", R.drawable.ic_flaticon_transfer, "19_lock_transfer.png", "Chuyển quyền", listOf("Chọn người nhận", "Xác minh OTP", "Ghi log chuyển quyền")),
    ScreenSpec(20, "combination_unlock", "Mở khóa kết hợp", "Yêu cầu nhiều điều kiện mở khóa", R.drawable.ic_flaticon_combination, "20_combination_unlock.png", "Tạo rule", listOf("PIN + thẻ", "App + vân tay", "Theo khung giờ")),
    ScreenSpec(21, "cycle_normally_open", "Lịch luôn mở", "Cycle normally-open cho văn phòng/lớp học", R.drawable.ic_flaticon_schedule, "21_cycle_normally_open.png", "Tạo lịch", listOf("Mở theo ca", "Tự khóa ngoài giờ", "Áp dụng cho phòng")),
    ScreenSpec(22, "nfc_unlocking", "Mở bằng NFC", "NFC unlocking cho điện thoại/thẻ", R.drawable.ic_flaticon_nfc, "22_nfc_unlocking.png", "Kích hoạt NFC", listOf("Đăng ký NFC", "Giới hạn thiết bị", "Thu hồi khi mất máy"), CredentialType.Nfc),
    ScreenSpec(23, "room_management", "Quản lý phòng", "Room management căn hộ/khách sạn", R.drawable.ic_flaticon_room, "23_room_management.png", "Thêm phòng", listOf("Tòa nhà/tầng/phòng", "Gán khóa", "Check-in/check-out")),
    ScreenSpec(24, "staff_tenant_management", "Nhân viên & khách thuê", "Quản lý người dùng theo vai trò", R.drawable.ic_flaticon_admin, "24_staff_tenant_management.png", "Thêm người", listOf("Tenant", "Staff", "Helper", "Guest")),
    ScreenSpec(25, "alarm_center", "Trung tâm cảnh báo", "Door open, tamper, offline, pin yếu", R.drawable.ic_flaticon_alarm, "25_alarm_center.png", "Xử lý cảnh báo", listOf("Critical realtime", "Chống spam", "Push FCM/APNs")),
    ScreenSpec(26, "networking_gateway", "Gateway & kết nối", "BLE/Wi-Fi/MQTT adapter", R.drawable.ic_flaticon_gateway, "26_networking_gateway.png", "Thêm gateway", listOf("Bluetooth pairing", "Wi-Fi setup", "MQTT topic", "Cloud bridge")),
    ScreenSpec(27, "device_management", "Quản lý thiết bị", "Firmware, OTA và trạng thái khóa", R.drawable.ic_flaticon_device, "27_device_management.png", "Quét thiết bị", listOf("Online/offline", "Firmware OTA", "Tín hiệu", "Bảo trì")),
    ScreenSpec(28, "report_analytics", "Báo cáo & phân tích", "Data report analysis", R.drawable.ic_flaticon_analytics, "28_report_analytics.png", "Xem báo cáo", listOf("Mở khóa theo ngày", "Người dùng hoạt động", "Khóa rủi ro")),
    ScreenSpec(29, "apartment_hotel_pms", "Apartment / Hotel PMS", "Tích hợp PMS và self-check-in", R.drawable.ic_flaticon_hotel, "29_apartment_hotel_pms.png", "Kết nối PMS", listOf("Import Excel", "PMS/self check-in", "Local + cloud framework")),
    ScreenSpec(30, "profile_language", "Tài khoản & ngôn ngữ", "Profile, bảo mật và đổi ngôn ngữ", R.drawable.ic_flaticon_language, "30_profile_language.png", "Lưu hồ sơ", listOf("Tiếng Việt/English", "App PIN", "Thiết bị tin cậy")),
)

fun specOf(route: String): ScreenSpec = screenSpecs.firstOrNull { it.route == route } ?: screenSpecs.first()
