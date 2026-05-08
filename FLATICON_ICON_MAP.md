# Flaticon icon map - Aplus Lock

Giữ nguyên tên file drawable dưới đây. Khi tải SVG từ https://www.flaticon.com/ thì thay đúng file cùng tên trong `app/src/main/res/drawable/`.

| File drawable | Ý nghĩa UI | Gợi ý từ khóa trên Flaticon |
|---|---|---|
| `ic_home.xml` | Tab Nhà / Home | home, smart home |
| `ic_keypad.xml` | Key / mật khẩu / keypad | keypad, password, access code |
| `ic_lock.xml` | Khóa / remote unlock / password | smart lock, lock |
| `ic_fingerprint.xml` | Vân tay | fingerprint, biometric |
| `ic_face.xml` | Khuôn mặt | face scan, face id |
| `ic_card.xml` | Thẻ từ / hotel card | access card, key card, credit card |
| `ic_nfc.xml` | NFC / thẻ điện thoại | nfc, contactless, mobile key |
| `ic_remote.xml` | Remote control | remote control, key fob |
| `ic_combo.xml` | Mở khóa kết hợp | combination, grid, multi factor |
| `ic_phone.xml` | Điện thoại / phone auth | smartphone, mobile access |
| `ic_shield.xml` | Bảo mật / quyền | shield, security |
| `ic_report.xml` | Báo cáo / lịch sử | report, analytics |
| `ic_globe.xml` | Ngôn ngữ / cài đặt | globe, language |
| `ic_user.xml` | Tài khoản / owner | user, profile |
| `ic_wifi.xml` | Gateway / kết nối | wifi, gateway |
| `ic_more.xml` | More Hub | more, menu, dots |

Bản hiện tại dùng vector build-safe để tránh lỗi build khi chưa có bộ SVG Flaticon chính thức. Chỉ cần thay nội dung vector/SVG, không đổi tên file.

## Batch 05 - Password icons

Nguồn icon yêu cầu: https://www.flaticon.com/

Các drawable đang dùng trong Password Management:

| Drawable | Mục đích | Từ khóa Flaticon nên tìm |
|---|---|---|
| `ic_lock.xml` | mật khẩu / khóa | `smart lock`, `password lock`, `security lock` |
| `ic_keypad.xml` | nhập PIN / tạo mã | `keypad`, `pin code`, `password keypad` |
| `ic_report.xml` | thời hạn / lịch | `calendar`, `schedule`, `time access` |
| `ic_wifi.xml` | đồng bộ / online | `sync`, `cloud sync`, `wifi lock` |
| `ic_shield.xml` | chính sách bảo mật | `security shield`, `policy`, `access control` |

Khi tải SVG từ Flaticon, nên import bằng Android Studio: `New > Vector Asset > Local file (SVG)` rồi lưu đè đúng tên drawable ở trên.
