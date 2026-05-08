# Batch 04 - Trung tâm chìa khóa và thêm quyền mở khóa

## Phạm vi UI đã làm

Batch 04 bám đúng file batch mới:

- UI-16: Thêm quyền mở khóa / Credential Center
- UI-03: Quản lý mật khẩu
- UI-09: Quản lý thẻ
- UI-15: NFC & thẻ điện thoại
- UI-23: Thêm khuôn mặt
- UI-24: Thêm remote
- UI-25: Thêm thẻ
- UI-26: Thêm mật khẩu
- UI-27: Thêm vân tay
- UI-28: Mở khóa kết hợp

## Điểm đã sửa để không bị thừa

- UI-07 vẫn có nút **Key** ở bảng tác vụ nhanh.
- Bấm **Key** sẽ mở UI-16.
- Trong UI-16 đã **không còn dòng Key** nữa.
- UI-16 chỉ còn các flow con: mật khẩu, vân tay, khuôn mặt, thẻ, remote, NFC/phone, mở kết hợp.
- UI-30 Remote Unlock không bị nhét lại vào UI-16 vì UI-30 thuộc Batch 03.

## Kiến trúc dữ liệu

Đã thêm model dùng chung:

- `Credential`
- `CredentialType`
- `CredentialStatus`
- `PermissionRole`
- `LockCapability`
- `AuditLog`

Các màn không tự mock riêng. Tất cả đọc/ghi qua `MockLockRepository` và `HomeViewModel`.

## Luồng test nhanh

1. Login bằng `admin@aplus.vn / 123456`.
2. Vào Home, chọn một khóa.
3. Vào Lock Detail.
4. Bấm **Key**.
5. Kiểm tra UI-16 không có dòng Key bên trong.
6. Bấm từng mục:
   - Thêm mật khẩu -> tạo xong quay lại danh sách credential.
   - Thêm vân tay -> scan mock nhiều bước -> lưu credential.
   - Thêm khuôn mặt -> nếu khóa không hỗ trợ face thì bị chặn.
   - Thêm thẻ -> mock quẹt thẻ -> lưu credential.
   - Thêm remote -> mock ghép remote -> lưu credential.
   - NFC/Phone -> mock token -> lưu credential.
   - Mở kết hợp -> tạo rule PIN + thẻ.
7. Quay lại UI-16, credential mới phải xuất hiện.
8. Bấm Tạm dừng / Bật lại / Thu hồi trên credential.

## Rule đang có

- Không tạo credential nếu thiếu owner hoặc lock.
- Không tạo credential nếu khóa không hỗ trợ capability đó.
- Credential bị thu hồi chỉ chuyển `Revoked`, không xóa khỏi list để giữ audit/log.
- Khóa offline tạo credential ở trạng thái `Pending` để mô phỏng PendingSync.
- Tạo/thu hồi/tạm dừng/bật lại credential có ghi `AuditLog` và record mock.

## Icon Flaticon

Các icon đã tách thành drawable riêng để thay bằng SVG từ Flaticon cùng tên mà không phải sửa Kotlin:

- `ic_card.xml`
- `ic_nfc.xml`
- `ic_remote.xml`
- `ic_combo.xml`
- `ic_more.xml`
- Các icon cũ: `ic_lock`, `ic_fingerprint`, `ic_face`, `ic_keypad`, `ic_phone`, `ic_user`, `ic_shield`.

Khi tải SVG từ Flaticon, giữ nguyên tên file drawable trên để app tự nhận.
