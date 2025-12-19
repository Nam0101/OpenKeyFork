# OpenKey Engine - Kotlin Implementation

## Giới thiệu (Introduction)

Đây là phiên bản Kotlin của OpenKey Engine - bộ xử lý core logic cho phương thức gõ tiếng Việt.

This is the Kotlin version of OpenKey Engine - the core keyboard logic for Vietnamese input method.

## Cấu trúc (Structure)

```
engine-kotlin/
└── src/
    └── main/
        └── kotlin/
            └── com/
                └── openkey/
                    └── engine/
                        ├── DataType.kt       # Các kiểu dữ liệu cơ bản
                        ├── KeyCodes.kt       # Định nghĩa mã phím
                        ├── Vietnamese.kt     # Dữ liệu ngôn ngữ tiếng Việt
                        └── Engine.kt         # Logic chính của bộ gõ
```

## Các tính năng chính (Main Features)

### 1. Hỗ trợ nhiều kiểu gõ (Multiple Input Types)
- **Telex**: Kiểu gõ phổ biến nhất (a + s = á, a + a = â)
- **VNI**: Kiểu gõ số (a + 1 = á, a + 6 = â)
- **Simple Telex**: Biến thể của Telex

### 2. Xử lý thanh điệu (Tone Mark Processing)
- Dấu sắc (á): S key hoặc 1
- Dấu huyền (à): F key hoặc 2
- Dấu hỏi (ả): R key hoặc 3
- Dấu ngã (ã): X key hoặc 4
- Dấu nặng (ạ): J key hoặc 5

### 3. Xử lý dấu mũ và dấu móc (Circumflex and Horn)
- Dấu mũ (â, ê, ô): Gõ phím hai lần (a + a = â)
- Dấu móc (ư, ơ): W key

### 4. Tự động đặt dấu thanh (Automatic Tone Placement)
- **Chính tả hiện đại** (Modern Orthography): oà, uý
- **Chính tả cổ** (Old Orthography): òa, úy

### 5. Kiểm tra chính tả (Spelling Check)
- Tự động kiểm tra từ hợp lệ
- Khôi phục phím nếu từ sai

## Cách sử dụng (Usage)

### Khởi tạo Engine (Initialize Engine)

```kotlin
import com.openkey.engine.*

// Tạo instance của engine
val engine = VietnameseEngine()

// Khởi tạo và nhận hook state
val hookState = engine.init()

// Cấu hình engine
EngineConfig.apply {
    language = 1              // Vietnamese mode
    inputType = 0             // Telex
    useModernOrthography = 1  // Modern orthography
    checkSpelling = 1         // Enable spell check
}
```

### Xử lý sự kiện phím (Handle Key Events)

```kotlin
// Xử lý phím được nhấn
engine.handleEvent(
    event = VKeyEvent.KEYBOARD,
    state = VKeyEventState.KEY_DOWN,
    data = KEY_A,            // Mã phím
    capsStatus = 0u,         // 0: normal, 1: shift, 2: caps lock
    otherControlKey = false  // Có phím Ctrl/Alt/Cmd không
)

// Kiểm tra kết quả
when (HookCodeState.values()[hookState.code.toInt()]) {
    HookCodeState.V_DO_NOTHING -> {
        // Không làm gì
    }
    HookCodeState.V_WILL_PROCESS -> {
        // Xử lý: xóa backspaceCount ký tự, thêm newCharCount ký tự mới
        val backspaces = hookState.backspaceCount
        val newChars = hookState.charData.take(hookState.newCharCount.toInt())
    }
    HookCodeState.V_RESTORE -> {
        // Khôi phục ký tự cũ
    }
    // ... các trường hợp khác
}
```

### Ví dụ cụ thể (Concrete Example)

```kotlin
// Gõ "anh"
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_N, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_H, 0u, false)

// Thêm dấu sắc -> "ánh"
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_S, 0u, false)
// hookState sẽ chứa: backspaceCount = 3, newCharCount = 3
// charData = ['á', 'n', 'h']

// Gõ aa -> "â"
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
// hookState: backspaceCount = 1, newCharCount = 1, charData = ['â']
```

## Sự khác biệt với phiên bản C++ (Differences from C++ Version)

1. **Ngôn ngữ**: Kotlin thay vì C++
2. **Quản lý bộ nhớ**: Kotlin tự động quản lý, không cần pointer
3. **Kiểu dữ liệu**: Sử dụng UInt, UByte thay vì unsigned types trong C++
4. **Collections**: Sử dụng List, Map của Kotlin thay vì vector, map của C++
5. **Null safety**: Kotlin có kiểm tra null tự động
6. **Immutability**: Khuyến khích dùng val (immutable) thay vì var

## Kiến trúc (Architecture)

### DataType.kt
Định nghĩa các kiểu dữ liệu cơ bản:
- Enums: VKeyEvent, VKeyEventState, VKeyInputType, HookCodeState
- Data class: VKeyHookState
- Constants: Masks cho tone, mark, caps, v.v.

### KeyCodes.kt
Định nghĩa mã phím cho macOS (có thể mở rộng cho Windows/Linux):
- Phím chữ: KEY_A đến KEY_Z
- Phím số: KEY_0 đến KEY_9
- Phím đặc biệt: KEY_SPACE, KEY_DELETE, v.v.

### Vietnamese.kt
Dữ liệu ngôn ngữ tiếng Việt:
- Bảng nguyên âm (vowel): Các tổ hợp nguyên âm hợp lệ
- Bảng phụ âm (consonant): Các tổ hợp phụ âm hợp lệ
- Bảng mã Unicode: Chuyển đổi sang các bảng mã khác nhau
- Quick shortcuts: cc=ch, gg=gi, v.v.

### Engine.kt
Logic chính của bộ gõ:
- `VietnameseEngine`: Class chính xử lý sự kiện
- `EngineConfig`: Cấu hình toàn cục
- Các phương thức chính:
  - `init()`: Khởi tạo engine
  - `handleEvent()`: Xử lý sự kiện phím
  - `startNewSession()`: Bắt đầu từ mới
  - `getCharacterCode()`: Chuyển đổi mã nội bộ sang ký tự

## Tính năng chưa hoàn thiện (Incomplete Features)

Phiên bản Kotlin này là bản port cơ bản. Một số tính năng cần bổ sung:

1. **Code Table**: Chuyển đổi sang TCVN3, VNI-Windows, Unicode Compound
2. **Macro**: Tính năng gõ tắt
3. **Smart Switch**: Tự động chuyển ngôn ngữ theo ứng dụng
4. **Grammar Check**: Kiểm tra ngữ pháp chi tiết
5. **Full Spelling Check**: Kiểm tra chính tả đầy đủ
6. **Convert Tool**: Công cụ chuyển mã

## Đóng góp (Contributing)

Để đóng góp vào dự án:

1. Fork repository
2. Tạo branch mới: `git checkout -b feature/ten-tinh-nang`
3. Commit changes: `git commit -am 'Thêm tính năng X'`
4. Push to branch: `git push origin feature/ten-tinh-nang`
5. Tạo Pull Request

## Giấy phép (License)

GPL - Giống với OpenKey gốc

Copyright © 2019 Tuyen Mai. All rights reserved.

## Liên hệ (Contact)

- Original Author: Mai Vũ Tuyên (maivutuyen.91@gmail.com)
- Kotlin Port: OpenKey Community

## Tài liệu tham khảo (References)

- [OpenKey GitHub](https://github.com/tuyenvm/OpenKey)
- [Vietnamese Typing Rules](https://vi.wikipedia.org/wiki/Ch%C3%AD_qu%E1%BB%91c_ng%E1%BB%AF)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
