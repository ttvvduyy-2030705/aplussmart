# Batch 05 - Password Management

Batch này triển khai đúng UI-03 Quản lý mật khẩu và UI-26 Thêm mật khẩu theo file kế hoạch mới.

## Luồng đã có

- Vào Lock Detail > Key > Quản lý mật khẩu để mở UI-03.
- Bấm Thêm mật khẩu để mở UI-26.
- Tạo được các loại mã:
  - Mã thường
  - Mã tạm thời
  - Mã một lần
  - Mã chu kỳ
  - Mã nhân viên/khách
- Nhập mã thủ công hoặc bấm Tự sinh.
- Validate mã 6-10 số.
- Chặn tên mã trùng trên cùng khóa.
- Repository chặn mã trùng trên cùng khóa còn hiệu lực.
- Nếu khóa online: mã tạo ra Active/Synced.
- Nếu khóa offline: mã tạo ra Pending/PendingSync.
- Thu hồi khi khóa offline sẽ chuyển syncState sang PendingRevoke.
- Mô phỏng mở bằng mã tại UI-03 bằng nút Mở thử.
- Mã một lần sau khi mở thử thành công sẽ chuyển Used.
- Có Gia hạn, Tạm dừng, Bật lại, Thu hồi.

## Dữ liệu mới

Các model đã thêm trong `LockModels.kt`:

- `PasswordType`
- `SyncState`
- `PasswordPolicy`
- Các field password trong `Credential`:
  - `passwordType`
  - `passwordToken`
  - `passwordPolicy`
  - `maxUseCount`
  - `usedCount`
  - `syncState`

## Repository mới

Trong `MockLockRepository`:

- `createPasswordCredential(...)`
- `simulatePasswordUnlock(...)`
- `extendCredential(...)`
- `revokeCredential(...)` đã xử lý `PendingRevoke` khi khóa offline.

## Checklist test nhanh

1. Tạo mã thường cho Căn hộ 520 -> Active/Synced.
2. Tạo mã một lần -> vào UI-03 bấm Mở thử -> mã chuyển Used.
3. Tạo mã trùng code trên cùng khóa -> bị chặn.
4. Tạo mã cho Phòng 301 đang offline -> Pending/PendingSync.
5. Thu hồi mã ở khóa offline -> trạng thái Revoked/PendingRevoke.
6. Tạm dừng mã Active -> nút Mở thử bị chặn.
7. Gia hạn mã -> validTo đổi thành Gia hạn thêm 7 ngày.

## Icon / Flaticon

Các icon password dùng drawable build-safe hiện có để app không fail build. Khi chốt bộ icon thương mại, tải SVG từ Flaticon và thay vào đúng file drawable tương ứng trong `app/src/main/res/drawable/`.

Gợi ý map:

- `ic_lock.xml` -> password / smart lock icon
- `ic_keypad.xml` -> keypad / password / PIN icon
- `ic_report.xml` -> calendar / schedule / expiry icon
- `ic_wifi.xml` -> sync / cloud / online icon
- `ic_shield.xml` -> policy / security icon

Không đổi tên drawable để tránh lỗi resource ID trong Kotlin.
