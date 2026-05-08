# Batch 0 — Source Foundation & Architecture Cleanup

## Mục tiêu

Batch 0 không làm thêm chức năng mới. Mục tiêu là dọn nền source để các batch sau có thể làm từng chức năng thật mà không phải sửa chắp vá trong một file lớn.

## Đầu ra đã làm

### 1. Chuẩn hóa route/navigation

Đã thêm file:

```text
android/app/src/main/java/com/aplus/locknative/ui/foundation/AppRoutes.kt
```

File này là nơi duy nhất định nghĩa route chính của app:

- `Login`
- `Register`
- `ForgotPassword`
- `Home`
- `LockDetail`
- `AddKeyMenu`
- `AddPassword`
- `PasswordManagement`
- `AddFingerprint`
- `AddFace`
- `AddCard`
- `RemoteUnlock`
- `RoomManagement`
- `ReportAnalytics`
- `ProfileLanguage`
- toàn bộ các route còn lại trong bộ 31 màn.

Đã thêm `MainTab` cho 4 tab chính:

```text
Nhà / Chìa khóa / Báo cáo / Tôi
```

Đã thêm helper:

- `AplusRoutes.isAuthRoute(route)`
- `AplusRoutes.isKeyRoute(route)`
- `AplusRoutes.routeForTab(tab, lastKeyRoute)`
- `AplusRoutes.tabForRoute(route)`
- `AplusRoutes.backRouteOf(route)`

Từ batch sau, khi thêm flow mới thì thêm route vào file này trước, không viết string rải rác trong UI.

### 2. Tách cấu hình tài khoản test

Đã thêm file:

```text
android/app/src/main/java/com/aplus/locknative/ui/foundation/AuthDefaults.kt
```

Tài khoản test nằm tập trung ở đây:

```text
admin@aplus.vn
123456
```

OTP test:

```text
888888
```

Khi cần đổi tài khoản demo, chỉ sửa file này.

### 3. Tách cấu hình cỡ chữ input

Đã thêm file:

```text
android/app/src/main/java/com/aplus/locknative/ui/foundation/AppTextSizes.kt
```

Các cỡ chữ ô nhập auth được gom vào một chỗ:

```kotlin
AuthInputLabelFontSize
AuthInputLabelLineHeight
AuthInputValueFontSize
AuthInputValueLineHeight
```

Muốn chỉnh chữ login/register/forgot nhỏ hoặc to hơn thì sửa file này, không cần tìm trong toàn bộ UI.

### 4. Cập nhật `AplusLockApp.kt`

Đã sửa file:

```text
android/app/src/main/java/com/aplus/locknative/ui/AplusLockApp.kt
```

Thay các phần quan trọng từ string rời sang nền route mới:

- route mặc định dùng `AppRoute.Login`
- last key route dùng `AppRoute.LockDetail`
- tabbar dùng `AplusRoutes.routeForTab(...)`
- back button dùng `AplusRoutes.backRouteOf(...)`
- auth route dùng `AplusRoutes.isAuthRoute(...)`
- tài khoản test dùng `AuthDefaults`
- cỡ chữ input dùng `AppTextSizes`

Giao diện hiện tại vẫn giữ đúng UI31 bằng ảnh nền + hotspot như trước, nhưng nền code đã sạch hơn để tách dần từng màn thành Compose thật.

### 5. Cập nhật ViewModel dùng route chuẩn

Đã sửa file:

```text
android/app/src/main/java/com/aplus/locknative/ui/AplusLockViewModel.kt
```

ViewModel giờ dùng `AppRoute` thay vì route string rời ở các chỗ chính.

### 6. Thêm `.gitignore`

Đã thêm file:

```text
.gitignore
```

Loại khỏi source các thư mục/file không nên đưa vào git/zip:

- `.gradle/`
- `build/`
- `local.properties`
- `.idea/`
- `node_modules/`
- `.env`
- log file

### 7. Thêm script dọn source

Đã thêm file:

```text
scripts/clean-project.ps1
```

Chạy script này khi muốn dọn project trước khi gửi source:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\clean-project.ps1
```

Script sẽ xóa:

- `android/.gradle`
- `android/.idea`
- `android/build`
- `android/app/build`
- `backend/node_modules`
- `backend/dist`

## Những gì Batch 0 chưa làm

Batch 0 chưa biến toàn bộ 31 màn thành native Compose thật. Hiện vẫn dùng ảnh nền UI31 + hotspot để giữ giống thiết kế. Các batch sau sẽ thay từng vùng chức năng bằng component thật.

Batch 0 cũng chưa làm auth thật, chưa làm database thật, chưa làm command realtime thật. Những phần đó bắt đầu từ Batch 1 trở đi.

## Cách test nhanh sau khi áp dụng

```powershell
cd D:\project\aplussmart\android
.\gradlew.bat --stop
.\gradlew.bat clean :app:assembleDebug
```

Mở app và kiểm tra:

1. Vào màn login.
2. Tài khoản test vẫn hiển thị trong ô nhập.
3. Bấm Đăng nhập vào Home.
4. Bấm 4 tab: Nhà / Chìa khóa / Báo cáo / Tôi.
5. Bấm Back từ các màn con vẫn quay đúng route.

## Batch tiếp theo nên làm

Batch 1 — Login/Register/Forgot Password production flow:

- gom state đăng nhập vào ViewModel thật
- lưu session bằng DataStore
- logout xóa session
- đăng ký tạo user mock
- quên mật khẩu có flow OTP + đổi mật khẩu
- biometric login dùng cùng session logic
