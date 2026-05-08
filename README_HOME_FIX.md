# Aplus Lock - Home Function Fix

Patch này chỉ sửa logic Home/navigation/state/onPress, giữ nguyên nền UI 31 ảnh.

File chính:
- android/app/src/main/java/com/aplus/locknative/ui/AplusLockApp.kt
- android/app/src/main/java/com/aplus/locknative/ui/AplusLockViewModel.kt
- android/app/src/main/java/com/aplus/locknative/sdk/AplusLockSdk.kt

Đã sửa:
1. Login xong vẫn vào Home.
2. Nút Chia sẻ trên Home điều hướng tới phone_authorization.
3. Filter Home: Tất cả / Nhà / Khách sạn / Văn phòng.
4. Filter render lại từ mảng gốc 3 khóa: Căn hộ 520 / Phòng 301 / Cổng sau.
5. Item sau filter được dồn lên trên.
6. Không hiện text phụ kiểu Ẩn bởi bộ lọc.
7. Bấm từng khóa truyền đúng lockId vào Lock Detail.
8. Quick action Mật khẩu điều hướng tới add_password.
