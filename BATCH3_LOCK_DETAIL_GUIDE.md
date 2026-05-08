# Batch 03 - Chi tiết khóa và bảng tác vụ nhanh

## UI đã làm

- UI-07: Chi tiết khóa - Căn hộ 520 / Phòng 301 / Cổng sau theo `lockId` thật.
- UI-16: Thêm quyền mở khóa, dạng hub điều hướng các phương thức mở khóa.
- UI-30: Mở khóa từ xa, có xác thực App PIN mock và command lifecycle.

## Chức năng chính

- Lock Detail hiển thị đúng dữ liệu khóa được chọn: tên khóa, nhà/phòng, online/offline, pin, sóng, trạng thái cửa, trạng thái khóa.
- Nút tròn trung tâm có các trạng thái: idle, pending, sending, success, failed/offline.
- Bấm mở/khóa không đổi state ngay; app chạy lifecycle `pending -> sending -> success/failed` rồi mới cập nhật `MockLockRepository`.
- Quick action panel đã click được, không còn toast mù:
  - Key -> UI-16 Thêm quyền mở khóa
  - Add pwd -> UI-26
  - Add fingerprint -> UI-27
  - Add face -> UI-23
  - Adding card -> UI-25
  - Add remote -> UI-24
  - Phone auth -> UI-04
  - Sub admin -> UI-13
  - Settings -> UI-29
  - More -> UI-06
- Remote Unlock UI-30 kiểm tra:
  - khóa online
  - setting remote unlock mock
  - App PIN mock `123456`
  - xác nhận lại trước khi gửi lệnh
  - không gửi command thứ hai khi command đang pending
- Mọi lệnh success/failed đều ghi `AccessRecord` và `CommandLog` trong `MockLockRepository`.

## Tài khoản và PIN test

- Login: `admin@aplus.vn / 123456`
- OTP mock: `123456`
- App PIN mock cho Remote Unlock: `123456`

## Kiểm thử nhanh

1. Login vào app.
2. Home -> bấm Căn hộ 520 -> đúng chi tiết Căn hộ 520.
3. Bấm nút tròn mở khóa -> xác nhận -> chờ command success -> Home cập nhật trạng thái.
4. Bấm Thêm quyền -> vào UI-16.
5. Bấm từng dòng trong UI-16 -> mở đúng route placeholder theo UI code.
6. Vào Mở từ xa -> nhập PIN `123456` -> tick xác nhận -> gửi lệnh.
7. Vào Phòng 301 đang offline -> remote unlock bị chặn, không crash.

## Icon / Flaticon

Các drawable hiện tại là vector nội bộ để project build ổn định. Khi tải icon chính thức từ Flaticon, thay file cùng tên trong `app/src/main/res/drawable` theo map ở `FLATICON_ICON_MAP.md`.

## Bản chỉnh sau phản hồi

- Nút `Key` trong bảng tác vụ nhanh chính là lối vào `UI-16 - Thêm quyền mở khóa`.
- Đã bỏ nút `Thêm quyền` riêng ở phía trên để không bị trùng chức năng.
- Trên màn chi tiết khóa chỉ giữ CTA riêng cho `Mở khóa từ xa`; các quyền mở khóa đi qua nút `Key`.

## Fix sau review UI-16

- UI-07 vẫn giữ nút `Key` trong bảng tác vụ nhanh để mở UI-16 `Thêm quyền mở khóa`.
- UI-16 đã bỏ dòng `Key` bên trong danh sách, vì chính màn này đã là trung tâm thêm quyền mở khóa.
- Các dòng còn lại trong UI-16 chỉ còn các flow con: mật khẩu, vân tay, khuôn mặt, thẻ, remote, ủy quyền điện thoại, quản trị phụ, cài đặt, more và remote unlock.
