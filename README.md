AplusSmart_HomeFilterSameUIPatch_v2

Mục tiêu:
- Không vẽ lại UI danh sách khóa khi bấm lọc.
- Tất cả / Nhà / Khách sạn / Văn phòng dùng cùng một kiểu card như ảnh gốc.
- Filter chỉ đổi dữ liệu/row hiển thị và hotspot bấm.

Cách làm trong code:
- Giữ nguyên ảnh nền screen_03_home_dashboard.
- Khi filter khác Tất cả: che vùng danh sách gốc, sau đó cắt chính row card từ ảnh home gốc rồi đặt lại vào danh sách.
- Không tự dựng card mới bằng Compose nên UI sau lọc không bị khác kiểu so với tab Tất cả.

File sửa:
android/app/src/main/java/com/aplus/locknative/ui/AplusLockApp.kt
