# Aplus Lock Native — code scaffold v1

Bộ source này được dựng theo yêu cầu:

- Android: Kotlin + Jetpack Compose.
- iOS: Swift + SwiftUI scaffold.
- Backend bridge/API mock: Node.js + NestJS + PostgreSQL/Prisma.
- Realtime: WebSocket Gateway ở backend, có stub MQTT adapter để thay sau.
- Push: để sẵn service stub cho FCM/APNs.
- Device integration: Adapter pattern, hiện chạy `MockSmartLockAdapter`, sau thay bằng `BleSmartLockAdapter`, `MqttSmartLockAdapter`, hoặc adapter cloud/phần cứng riêng.
- UI: map đủ 31 màn theo ảnh trong `docs/ui-reference-31`.
- Chức năng: lấy từ catalog khóa thông minh: app unlock, password, card, fingerprint, face, remote, admin phụ, transfer, records, battery reporting, combination unlock, normally-open schedule, NFC, room/hotel/PMS, gateway, alarm, analytics.

## Cách chạy Android trước

Mở Android Studio → Open thư mục:

```text
AplusLockNative_Code_v1/android
```

Sau đó Sync Gradle và Run `app`.

Nếu máy chưa có Gradle wrapper, Android Studio vẫn có thể dùng Gradle local để sync. Khi muốn tạo wrapper:

```bash
cd android
gradle wrapper
./gradlew :app:assembleDebug
```

## Cách chạy backend mock

```bash
cd backend
npm install
cp .env.example .env
npx prisma generate
npm run start:dev
```

API mock chạy mặc định ở `http://localhost:3000`.

## Flaticon icons

Mình không nhúng trực tiếp file icon Flaticon tải sẵn vì cần đúng license/attribution hoặc tài khoản premium. Code Android đã để sẵn các file placeholder trong `android/app/src/main/res/drawable/ic_flaticon_*.xml`. Khi bạn tải SVG/PNG từ Flaticon, chỉ cần thay đúng tên file tương ứng theo `docs/FLATICON_ICON_MAP.md`, UI không cần sửa code.

## Cấu trúc chính

```text
AplusLockNative_Code_v1/
├── android/      # Kotlin + Jetpack Compose app
├── ios/          # SwiftUI scaffold cùng model/screen mapping
├── backend/      # NestJS + Prisma mock bridge
├── simulator/    # Device simulator seed/state
└── docs/         # 31 ảnh UI reference + map chức năng/icon/API
```
