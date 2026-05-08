# Batch 12 - UI-12 Kết nối Gateway / Thêm khóa / Networking

## Mục tiêu

Batch 12 biến nút Thêm khóa ở Home thành luồng Pairing Wizard thật ở mức mock/native Compose. App không còn mở placeholder khi bấm thêm khóa.

## Màn đã làm

- UI-12 Kết nối Gateway
- Route mới: `PairingGateway`
- Mở từ Home bằng nút `+` hoặc nút `Thêm khóa`

## Chức năng hiện có

- Chọn phương thức thêm thiết bị:
  - QR code
  - Mã thiết bị
  - Bluetooth nearby
  - Wi-Fi setup
  - Gateway/MQTT
  - Nhập thủ công demo
- Preflight permission mock:
  - Camera
  - Bluetooth
  - Location
  - Wi-Fi
  - Nearby devices
  - Notification
- Mock scan thiết bị gần đó.
- Chọn thiết bị tìm thấy, hiển thị model, serial, RSSI, capability, trạng thái alreadyBound.
- Nhập hoặc sửa serial/model thủ công.
- Cấu hình Wi-Fi gồm SSID và mật khẩu.
- Chọn Gateway; gateway offline bị chặn.
- Đặt tên khóa.
- Chọn phòng/khu vực từ repository thật.
- Hoàn tất pairing tạo `LockDevice` mới trong `MockLockRepository`.
- Sau pairing thành công tự quay về Home và danh sách Home có khóa mới.

## Guard / lỗi đã chặn

- Thiếu permission mock thì không scan/pairing.
- Serial trùng thì không tạo khóa mới.
- Thiết bị đã bind owner khác thì bị chặn.
- Wi-Fi setup yêu cầu SSID và mật khẩu tối thiểu 8 ký tự.
- Gateway offline thì không pairing.
- Không tạo lock mới nếu pairing thất bại.

## Model / data thêm mới

- `PairingMethod`
- `DiscoveredDevice`
- `GatewayInfo`
- `MockLockRepository.discoveredDevices`
- `MockLockRepository.gateways`
- `MockLockRepository.addPairedLock(...)`
- `HomeViewModel.addPairedLock(...)`

## Test nhanh

1. Login bằng `admin@aplus.vn / 123456`.
2. Ở Home, bấm nút `+` hoặc `Thêm khóa`.
3. Chọn `BLE`, bật các quyền mock bắt buộc, bấm `Quét mock`.
4. Chọn thiết bị `APL-NEW-8842`.
5. Nhập mật khẩu Wi-Fi nếu phương thức yêu cầu.
6. Chọn phòng và bấm `Hoàn tất pairing`.
7. Quay về Home, kiểm tra khóa mới xuất hiện.
8. Thử serial `APL-520-0001` để kiểm tra chặn trùng/đã bind.
9. Chọn `GW`, chọn gateway offline để kiểm tra chặn gateway offline.

## Patch: Home quick action Gateway

- Tác vụ nhanh Gateway ở Home đã được nối route trực tiếp sang UI-12 PairingGateway.
- QuickAccessItem đã có clickable thật, không còn là card hiển thị tĩnh.
- Card Gateway được viền/nhấn đỏ để biết đây là tác vụ đã có route thật.

## Fix UI-06 More Hub route
- Nút `More` trong bảng tác vụ nhanh của UI-07 giờ mở màn `UI-06 More Hub`, không mở placeholder.
- Trong `UI-06`, nhóm `Kết nối` có các ô `Gateway`, `Wi‑Fi`, `Bluetooth`; bấm các ô này mở `UI-12 Kết nối Gateway`.
- Khi mở UI-12 từ More Hub, nút back quay lại More Hub.
- Quick action `Gateway` ở Home vẫn bấm được nhưng đã bỏ trạng thái tô đỏ cố định; card chỉ giữ style bình thường như các quick action khác.
