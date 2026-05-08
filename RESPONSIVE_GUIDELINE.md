# Responsive guideline - Aplus Batch 00

## Mục tiêu

Sửa triệt để tình trạng giao diện to hơn màn hình, phải kéo xuống mới thấy nút chính.

## Quy tắc triển khai

1. Dùng `BaseScreen` cho mọi màn.
2. Tính scale theo `BoxWithConstraints`, không đoán kích thước bằng số cứng.
3. Không đặt chiều cao cố định quá lớn cho header, logo, card hoặc form.
4. Màn có input phải dùng `imePadding()` và giữ text bằng `rememberSaveable` hoặc ViewModel state.
5. Bottom tab nằm ngoài content scroll.
6. Màn nhỏ được phép scroll phần phụ, nhưng form chính và CTA không được bị che.
7. App đang khóa portrait trong Manifest để tránh xoay màn làm vỡ layout ở Batch 00.

## Checklist test

- Máy 360x640: Login không bị vỡ, input nhập được.
- Máy 390x844: Home và Lock Detail giữ đúng tỷ lệ.
- Mở/tắt keyboard 20 lần: không mất dữ liệu.
- Bấm Login -> Home; Home -> Lock Detail; Back -> Home.
- Filter Home đổi dữ liệu, không đổi kiểu card.
- Bottom tab không trôi theo scroll.
