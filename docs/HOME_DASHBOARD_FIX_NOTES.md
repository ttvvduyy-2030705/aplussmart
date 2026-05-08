# Home Dashboard Fix

Patch này sửa đúng nhóm lỗi ở màn Home:

- Nút Chia sẻ trên card chính đã có chức năng, đi tới màn `phone_authorization`.
- Cụm filter `Tất cả / Nhà / Khách sạn / Văn phòng` không bị vẽ lại; chỉ đặt hotspot trong suốt lên ảnh gốc.
- `Tất cả`: giữ 3 khóa mẫu.
- `Nhà`: chỉ còn `Căn hộ 520`.
- `Khách sạn`: chỉ còn `Phòng 301`.
- `Văn phòng`: chỉ còn `Cổng sau`.
- Không còn dòng `Ẩn bởi bộ lọc...`.
- Vùng bấm khóa đi theo danh sách đang lọc.
- Bấm từng khóa sẽ mở `lock_detail` với đúng tên khóa ở header: `Căn hộ 520`, `Phòng 301`, hoặc `Cổng sau`.
- Tác vụ nhanh `Mật khẩu` từ Home đã đi đúng màn `add_password`, không còn đi nhầm `add_key_menu`.

File sửa chính:

`android/app/src/main/java/com/aplus/locknative/ui/AplusLockApp.kt`
