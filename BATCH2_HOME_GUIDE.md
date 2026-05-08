# Batch 02 - Home Dashboard: Nhà, phòng, loại khóa và bộ lọc

Batch này bám UI-05 trong file kế hoạch mới và tập trung biến Home thành màn native có dữ liệu thật từ repository mock.

## Tài khoản test

- Email: `admin@aplus.vn`
- Mật khẩu: `123456`

## Đã làm

- Tạo `LockModels.kt` trong `vn.aplus.smart.data`.
- Tạo `HomeViewModel.kt` trong `vn.aplus.smart.home`.
- Home Dashboard đọc dữ liệu từ `MockLockRepository`, không còn tự hard-code trực tiếp trong UI.
- Có quan hệ dữ liệu cơ bản: `HomeInfo` -> `RoomInfo` -> `LockDevice`.
- Bộ lọc công trình hoạt động: `Tất cả / Nhà / Khách sạn / Văn phòng`.
- Bộ lọc trạng thái hoạt động: `Offline / Pin yếu / Đang mở / Có cảnh báo`.
- Tìm kiếm theo tên khóa, phòng, số phòng, khách thuê, serial, model.
- Card khóa hiển thị: tên khóa, nhà/phòng, loại khóa, online/offline, khóa/mở, cửa mở/đóng, pin, sóng, badge cảnh báo.
- Bấm từng khóa truyền đúng `lockId` sang Lock Detail.
- Lock Detail đọc cùng state với Home; thao tác khóa/mở mock cập nhật về Home.
- Nút `Thêm khóa` đã có route placeholder để nối Batch 12 Pairing/Gateway.

## Dữ liệu demo hiện có

1. `Căn hộ 520` - Nhà - online - pin 92%.
2. `Phòng 301` - Khách sạn - offline - pin 46%.
3. `Cổng sau` - Văn phòng - đang mở - pin 78%.
4. `Showroom chính` - Văn phòng - pin yếu 18%.

## Checklist test

- Login bằng tài khoản test.
- Bấm `Tất cả`: thấy toàn bộ khóa.
- Bấm `Nhà`: chỉ còn `Căn hộ 520`.
- Bấm `Khách sạn`: chỉ còn `Phòng 301`.
- Bấm `Văn phòng`: thấy `Cổng sau` và `Showroom chính`.
- Bấm filter `Offline`: chỉ hiện khóa offline trong phạm vi filter hiện tại.
- Bấm filter `Pin yếu`: chỉ hiện khóa có pin <= 20%.
- Tìm `301`, `showroom`, `APL-OFFICE` để kiểm tra search.
- Bấm từng khóa, kiểm tra màn detail đúng tên khóa.
- Vào `Căn hộ 520`, bấm vòng khóa để đổi trạng thái, quay về Home kiểm tra card đổi theo.

## Ghi chú icon Flaticon

Các icon trong `res/drawable` đang được giữ dưới dạng vector drawable nhẹ để build ổn định trong Android Studio. Khi chốt bộ icon thương mại từ Flaticon, thay SVG cùng tên vào các file drawable tương ứng hoặc dùng map trong `FLATICON_ICON_MAP.md`.
