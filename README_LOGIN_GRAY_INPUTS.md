# Login gray native inputs v4

Đã sửa cụm Login / Register / Forgot Password:

- Ô nhập được dựng bằng Compose native, không còn để nguyên input trắng của ảnh mock.
- Nền ô nhập: xám đen `#2A2F3A`.
- Chữ nhập: trắng.
- Label: xám sáng, nằm trên dòng nội dung nhập.
- Có viền xám `#525A68`, bo góc 18dp.
- Giữ tài khoản test `admin@aplus.vn / 123456` trong state để bấm Đăng nhập test nhanh.

File chính đã sửa:

`android/app/src/main/java/com/aplus/locknative/ui/AplusLockApp.kt`
