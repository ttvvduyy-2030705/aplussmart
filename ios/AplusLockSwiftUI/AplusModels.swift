import Foundation

enum CredentialType: String, CaseIterable, Identifiable {
    case password, fingerprint, face, card, remote, phone, nfc, admin
    var id: String { rawValue }
}

struct LockDevice: Identifiable {
    let id: String
    var name: String
    var room: String
    var isLocked: Bool
    var isOnline: Bool
    var battery: Int
    var signal: Int
}

struct ScreenSpec: Identifiable {
    let id: Int
    let route: String
    let title: String
    let subtitle: String
    let referenceImage: String
}

let aplusScreens: [ScreenSpec] = [
    .init(id: 0, route: "login", title: "Aplus Lock", subtitle: "Đăng nhập", referenceImage: "00_login.png"),
    .init(id: 1, route: "register", title: "Tạo tài khoản", subtitle: "Đăng ký", referenceImage: "01_register.png"),
    .init(id: 2, route: "forgot_password", title: "Khôi phục mật khẩu", subtitle: "OTP", referenceImage: "02_forgot_password.png"),
    .init(id: 3, route: "home_dashboard", title: "Nhà của tôi", subtitle: "Dashboard", referenceImage: "03_home_dashboard.png"),
    .init(id: 4, route: "lock_detail", title: "Chi tiết khóa", subtitle: "Lock/unlock", referenceImage: "04_lock_detail.png"),
    .init(id: 5, route: "add_key_menu", title: "Thêm phương thức mở", subtitle: "Key menu", referenceImage: "05_add_key_menu.png"),
    .init(id: 6, route: "add_password", title: "Tạo mật khẩu", subtitle: "PIN/temporary", referenceImage: "06_add_password.png"),
    .init(id: 7, route: "password_management", title: "Quản lý mật khẩu", subtitle: "Credential list", referenceImage: "07_password_management.png"),
    .init(id: 8, route: "add_fingerprint", title: "Thêm vân tay", subtitle: "Fingerprint", referenceImage: "08_add_fingerprint.png"),
    .init(id: 9, route: "add_face", title: "Thêm khuôn mặt", subtitle: "Face unlock", referenceImage: "09_add_face.png"),
    .init(id: 10, route: "add_card", title: "Thêm thẻ", subtitle: "Card", referenceImage: "10_add_card.png"),
    .init(id: 11, route: "add_remote_control", title: "Thêm remote", subtitle: "Remote control", referenceImage: "11_add_remote_control.png"),
    .init(id: 12, route: "phone_authorization", title: "Cấp quyền điện thoại", subtitle: "Phone authorization", referenceImage: "12_phone_authorization.png"),
    .init(id: 13, route: "sub_admin", title: "Admin phụ", subtitle: "Secondary administrator", referenceImage: "13_sub_admin.png"),
    .init(id: 14, route: "remote_unlock", title: "Mở khóa từ xa", subtitle: "Remote unlock", referenceImage: "14_remote_unlock.png"),
    .init(id: 15, route: "settings", title: "Cài đặt", subtitle: "Settings", referenceImage: "15_settings.png"),
    .init(id: 16, route: "more_hub", title: "Trung tâm chức năng", subtitle: "More", referenceImage: "16_more_hub.png"),
    .init(id: 17, route: "unlock_records", title: "Lịch sử mở khóa", subtitle: "Records", referenceImage: "17_unlock_records.png"),
    .init(id: 18, route: "electricity_reporting", title: "Báo pin", subtitle: "Electricity reporting", referenceImage: "18_electricity_reporting.png"),
    .init(id: 19, route: "lock_transfer", title: "Chuyển quyền khóa", subtitle: "Transfer", referenceImage: "19_lock_transfer.png"),
    .init(id: 20, route: "combination_unlock", title: "Mở khóa kết hợp", subtitle: "Combination", referenceImage: "20_combination_unlock.png"),
    .init(id: 21, route: "cycle_normally_open", title: "Lịch luôn mở", subtitle: "Normally open", referenceImage: "21_cycle_normally_open.png"),
    .init(id: 22, route: "nfc_unlocking", title: "NFC unlock", subtitle: "NFC", referenceImage: "22_nfc_unlocking.png"),
    .init(id: 23, route: "room_management", title: "Quản lý phòng", subtitle: "Rooms", referenceImage: "23_room_management.png"),
    .init(id: 24, route: "staff_tenant_management", title: "Nhân viên & khách thuê", subtitle: "Members", referenceImage: "24_staff_tenant_management.png"),
    .init(id: 25, route: "alarm_center", title: "Cảnh báo", subtitle: "Alarm", referenceImage: "25_alarm_center.png"),
    .init(id: 26, route: "networking_gateway", title: "Gateway", subtitle: "BLE/Wi-Fi/MQTT", referenceImage: "26_networking_gateway.png"),
    .init(id: 27, route: "device_management", title: "Quản lý thiết bị", subtitle: "Device", referenceImage: "27_device_management.png"),
    .init(id: 28, route: "report_analytics", title: "Báo cáo", subtitle: "Analytics", referenceImage: "28_report_analytics.png"),
    .init(id: 29, route: "apartment_hotel_pms", title: "Hotel PMS", subtitle: "PMS bridge", referenceImage: "29_apartment_hotel_pms.png"),
    .init(id: 30, route: "profile_language", title: "Tài khoản", subtitle: "Language/Profile", referenceImage: "30_profile_language.png"),
]
