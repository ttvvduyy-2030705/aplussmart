# Aplus Lock - Batch 01 Auth Guide

Batch 01 bám file `Ke_hoach_batch_Aplus_Lock_co_anh_giao_dien.pdf`:

- UI-00: Đăng nhập
- UI-01: Tạo tài khoản
- UI-02: Khôi phục mật khẩu

## Tài khoản test

```text
Email: admin@aplus.vn
Mật khẩu: 123456
OTP mock: 123456
```

## Đã làm

- Login bằng email/số điện thoại + mật khẩu.
- Validate realtime: thiếu tài khoản, email sai định dạng, số điện thoại sai, mật khẩu dưới 6 ký tự.
- Nút Login/Register/Forgot có loading state và chặn bấm liên tục.
- Register: tên, số điện thoại, email, mật khẩu, nhập lại mật khẩu, đồng ý điều khoản.
- Forgot Password: nhập email/số điện thoại, gửi OTP mock, nhập OTP, đặt mật khẩu mới.
- Session lưu bằng Android DataStore.
- Mở lại app nếu còn session sẽ vào Home Dashboard.
- Tab Tôi có nút Đăng xuất, xóa session và quay lại Login.
- Auth tách khỏi UI qua `AuthViewModel`, `AuthRepository`, `MockAuthRepository`, `SessionStore`.

## Luồng test nhanh

1. Mở app.
2. Đăng nhập `admin@aplus.vn / 123456`.
3. App vào Home Dashboard Batch 00.
4. Bấm tab `Tôi` > `Đăng xuất`.
5. Quay lại Login.
6. Vào `Quên mật khẩu`, gửi OTP, dùng OTP `123456`, đặt mật khẩu mới.
7. Login lại bằng mật khẩu mới.

## Icon / Flaticon

Các icon đang được tách riêng trong `app/src/main/res/drawable/` để dễ thay bằng SVG từ Flaticon.
Khi tải SVG từ https://www.flaticon.com/, đặt cùng tên file tương ứng:

- `ic_phone.xml` - phone / smartphone
- `ic_lock.xml` - lock / padlock
- `ic_user.xml` - user / account
- `ic_keypad.xml` - keypad / password
- `ic_globe.xml` - language / globe
- `ic_face.xml` - face id / scan face
- `ic_fingerprint.xml` - fingerprint
- `ic_report.xml` - report / chart
- `ic_wifi.xml` - wifi / connection
- `ic_shield.xml` - security / shield
- `ic_home.xml` - home

