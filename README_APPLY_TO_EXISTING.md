# Cách ghép vào project hiện tại `C:\project\apluslock`

Bản này là source scaffold đầy đủ, không phải patch diff vì bạn chưa gửi zip source code hiện tại ở lượt này. Nếu muốn ghép vào app đang có:

1. Copy các package Kotlin dưới đây vào app hiện tại:

```text
android/app/src/main/java/com/aplus/locknative/domain
android/app/src/main/java/com/aplus/locknative/sdk
android/app/src/main/java/com/aplus/locknative/ui
```

2. Đổi package `com.aplus.locknative` thành package thật của app hiện tại nếu khác.
3. Copy các icon placeholder:

```text
android/app/src/main/res/drawable/ic_flaticon_*.xml
```

4. Trong `MainActivity`, gọi:

```kotlin
setContent {
    AplusLockTheme { AplusLockApp() }
}
```

5. Backend copy nguyên thư mục `backend/` hoặc merge các controller/service vào backend hiện tại.

Điểm quan trọng: UI chỉ gọi `AplusLockSdk`, không gọi mock trực tiếp. Khi có khóa thật, thay `MockSmartLockAdapter` bằng adapter phần cứng/cloud là được.
