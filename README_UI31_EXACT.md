# Aplus Lock UI31 Exact Build

Bản này chuyển Android app sang chế độ `UI31 exact mode`:

- 31 màn trong `Hihi.zip` được đưa thẳng vào `res/drawable-nodpi` làm visual source of truth.
- Jetpack Compose vẽ ảnh full-screen bằng `ContentScale.FillBounds` để giống mockup nhất có thể.
- Transparent native hotspots được đặt trên ảnh để app vẫn bấm/chuyển màn/chạy mock action.
- Tabbar chính: `Nhà`, `Chìa khóa`, `Báo cáo`, `Tôi`.
- Màn mở khóa `04_lock_detail.png` là màn chính của tab `Chìa khóa`, chạm vòng khóa lớn để mock lock/unlock.
- Chức năng bám catalog smart-lock: fingerprint, card, password, app unlock, face unlock, remote unlock, secondary administrator, lock transfer, record reporting, electricity reporting, combination unlock, normally-open schedule, NFC, room management, PMS/hotel, gateway/realtime monitoring.

## Cách áp vào project hiện tại

Copy thư mục `android` trong bản này đè vào project hiện tại, hoặc dùng patch zip `AplusSmart_UI31_ExactPatch.zip`.

Sau đó chạy:

```powershell
cd D:\project\aplussmart\android
.\gradlew.bat --stop
.\gradlew.bat clean :app:assembleDebug
```

Nếu chưa có `gradlew.bat`, mở trực tiếp thư mục `android` bằng Android Studio và bấm Run.

