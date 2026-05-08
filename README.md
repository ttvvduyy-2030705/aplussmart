# Aplus Android Studio - Batch 03

Native Android app bằng Kotlin + Jetpack Compose.

## Chạy project

1. Giải nén vào `C:\project\aplussmart`
2. Mở Android Studio
3. `File > Open > C:\project\aplussmart`
4. Chờ Gradle Sync
5. Run module `app`

## Batch hiện tại

- Batch 00: BaseScreen, responsive UI, Login/Register/Forgot/Home/Lock Detail nền.
- Batch 01: Auth mock repository: login, register, forgot password, OTP mock, session DataStore, logout.
- Batch 02: Home Dashboard: dữ liệu Home/Room/Lock, filter, search, bấm đúng khóa, state chung.
- Batch 03: Lock Detail, bảng tác vụ nhanh, UI-16 Thêm quyền mở khóa, UI-30 Mở khóa từ xa, command lifecycle và log mock.

## Tài khoản test

```text
Login: admin@aplus.vn / 123456
OTP mock: 123456
App PIN Remote Unlock: 123456
```

## File quan trọng

```text
app/src/main/java/vn/aplus/smart/MainActivity.kt
app/src/main/java/vn/aplus/smart/data/LockModels.kt
app/src/main/java/vn/aplus/smart/home/HomeViewModel.kt
app/src/main/java/vn/aplus/smart/auth/AuthModels.kt
app/src/main/java/vn/aplus/smart/auth/AuthRepository.kt
app/src/main/java/vn/aplus/smart/auth/AuthViewModel.kt
app/src/main/java/vn/aplus/smart/data/SessionStore.kt
BATCH1_AUTH_GUIDE.md
BATCH2_HOME_GUIDE.md
BATCH3_LOCK_DETAIL_GUIDE.md
FLATICON_ICON_MAP.md
UI_COMPONENT_RULES.md
RESPONSIVE_GUIDELINE.md
```

## Ghi chú icon Flaticon

Các icon trong `res/drawable` đang là vector drawable nội bộ để app build ổn. Khi tải bộ icon chính thức từ Flaticon, thay đúng tên file theo `FLATICON_ICON_MAP.md`.


## Batch 04

Bản này đã thêm Credential Center theo file batch mới: UI-16, UI-03, UI-09, UI-15, UI-23, UI-24, UI-25, UI-26, UI-27, UI-28.

Điểm kiểm tra quan trọng: nút **Key** ở Lock Detail mở UI-16; trong UI-16 không còn dòng Key để tránh thừa/nested sai luồng.

Xem thêm: `BATCH4_CREDENTIAL_GUIDE.md` và `FLATICON_ICON_MAP.md`.

## Batch 05

Bản này đã thêm Password Management theo UI-03 và UI-26. Xem `BATCH5_PASSWORD_GUIDE.md` để test mã thường, mã tạm thời, mã một lần, mã chu kỳ, PendingSync/PendingRevoke và mock password unlock.


## Batch 12

Đã thêm UI-12 Kết nối Gateway / Pairing Wizard. Xem `BATCH12_PAIRING_GATEWAY_GUIDE.md`.
