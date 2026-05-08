# UI_COMPONENT_RULES.md - Aplus Batch 00

## 1. Nguyên tắc chung

- Ảnh giao diện chỉ dùng làm reference, không dùng ảnh nền full-screen để che component.
- Dữ liệu động phải là Compose native: input, button, card, tab, list, chip, trạng thái khóa.
- Không dùng chiều cao cố định kiểu 900dp cho màn điện thoại.
- Bottom tab không nằm trong vùng scroll.
- Mọi màn dùng chung `BaseScreen` để xử lý safe area, navigation bar, keyboard và loading/error.

## 2. Component bắt buộc

- `BaseScreen`: header, content, bottom tab, floating action, loading, error banner.
- `AplusScreenHeader`: tiêu đề, subtitle, back button, right icon.
- `AplusTextField`: input native, giữ dữ liệu bằng state.
- `AplusButton`: CTA đỏ chính.
- `AplusSecondaryButton`: nút phụ nền đen.
- `AplusCard`: card graphite bo góc lớn.
- `AplusStatusChip`: trạng thái online/offline/pin/risk.
- `AplusBottomTab`: 4 mục Nhà / Chìa khóa / Báo cáo / Tôi.
- `ConfirmDialog`: xác nhận thao tác nhạy cảm, chuẩn bị cho Batch 03.

## 3. Màu sắc

- Background: `#05060A`
- Card graphite: `#101722`
- Card dark: `#090B0F`
- Input field: `#0B0D10`
- Main red: `#F1153D`
- Deep burgundy glow: `#650018`
- Text chính: `#F7F8FB`
- Text phụ: `#AAB0BE`
- Online/Success: `#23D66F`
- Warning: `#FFC24B`
- Info blue: `#4DA3FF`
- Error: `#FF5A6F`

## 4. Typography

`UiScaleConfig` chia theo màn:

- tiny: màn nhỏ hơn khoảng 650dp cao hoặc 365dp rộng.
- compact: màn dưới khoảng 760dp cao hoặc 390dp rộng.
- normal: màn còn lại.

Nhóm font bắt buộc:

- title
- screenTitle
- body
- caption
- label
- button

Không tự tăng font từng màn riêng lẻ nếu chưa cập nhật `UiScaleConfig`.

## 5. Spacing

Spacing đi qua `UiScaleConfig`:

- `gapXs`
- `gapSm`
- `gapMd`
- `horizontal`
- `cardPadding`
- `inputHeight`
- `buttonHeight`
- `bottomBarHeight`

Không hard-code margin lớn khiến màn Login/Register/Forgot bị vượt chiều cao.

## 6. Keyboard và safe area

`BaseScreen` luôn dùng:

- `statusBarsPadding()`
- `navigationBarsPadding()`
- `imePadding()`

Các màn auth cho phép scroll nội dung khi màn quá nhỏ, nhưng CTA chính vẫn phải dễ nhìn thấy.

## 7. Navigation route chuẩn

Batch 00 đã đặt route cơ bản:

- `Login`
- `Register`
- `Forgot`
- `Home`
- `LockDetail`
- `Placeholder`

Từ Batch 01 trở đi có thể thay bằng Navigation Compose, nhưng không quay lại hotspot/ảnh tĩnh.
