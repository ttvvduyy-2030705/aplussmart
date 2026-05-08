# Aplus Smart Lock — Batch 0

Batch 0 tập trung dọn nền source để làm tiếp từng chức năng thật theo catalog và 31 màn UI.

## Đầu ra chính

- Route/navigation được gom về `AppRoutes.kt`.
- 4 tab chính được chuẩn hóa: `Nhà / Chìa khóa / Báo cáo / Tôi`.
- Tài khoản test được gom về `AuthDefaults.kt`.
- Cỡ chữ input auth được gom về `AppTextSizes.kt` để tự chỉnh nhanh.
- `AplusLockApp.kt` đã dùng nền route mới thay cho một phần string rời.
- `AplusLockViewModel.kt` đã bắt đầu dùng route chuẩn.
- Thêm `.gitignore` để không đóng gói `.gradle`, `build`, `.idea`, `node_modules`, `.env`.
- Thêm script `scripts/clean-project.ps1` để dọn source trước khi gửi.
- Tài liệu chi tiết: `docs/BATCH0_FOUNDATION.md`.

## Chạy Android

```powershell
cd android
.\gradlew.bat --stop
.\gradlew.bat clean :app:assembleDebug
```

## Dọn project trước khi zip/gửi

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\clean-project.ps1
```
