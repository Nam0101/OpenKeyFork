# Getting Started with OpenKey Engine - Kotlin

## Giới thiệu nhanh / Quick Introduction

OpenKey Engine Kotlin là bản port hiện đại của bộ gõ tiếng Việt OpenKey từ C++ sang Kotlin.

OpenKey Engine Kotlin is a modern port of the OpenKey Vietnamese input method from C++ to Kotlin.

## Cài đặt / Installation

### Yêu cầu / Requirements

- **JDK**: Java 11 or higher
- **Kotlin**: 1.9.20 or higher
- **Gradle** (optional): For building with Gradle

### Kiểm tra / Verification

```bash
# Check Java
java -version

# Check Kotlin
kotlinc -version
```

## Sử dụng nhanh / Quick Start

### 1. Biên dịch / Compile

Sử dụng Kotlin compiler trực tiếp:

```bash
cd Sources/OpenKey/engine-kotlin/src/main/kotlin

# Compile all files
kotlinc -d output com/openkey/engine/*.kt

# Run example
kotlin -classpath output com.openkey.engine.ExampleKt
```

### 2. Sử dụng trong code / Using in Code

```kotlin
import com.openkey.engine.*

fun main() {
    // 1. Create engine instance
    val engine = VietnameseEngine()
    
    // 2. Initialize
    val hookState = engine.init()
    
    // 3. Configure
    EngineConfig.apply {
        language = 1              // Vietnamese mode
        inputType = 0             // Telex
        useModernOrthography = 1  // Modern (oà, uý)
        checkSpelling = 1         // Enable spell check
    }
    
    // 4. Handle keyboard events
    engine.handleEvent(
        event = VKeyEvent.KEYBOARD,
        state = VKeyEventState.KEY_DOWN,
        data = KEY_A,
        capsStatus = 0u,
        otherControlKey = false
    )
    
    // 5. Check result
    when (HookCodeState.values()[hookState.code.toInt()]) {
        HookCodeState.V_WILL_PROCESS -> {
            // Delete hookState.backspaceCount characters
            // Insert hookState.newCharCount new characters
            val newChars = hookState.charData.take(hookState.newCharCount.toInt())
            println("New chars: $newChars")
        }
        HookCodeState.V_DO_NOTHING -> {
            println("No processing needed")
        }
        else -> {
            // Handle other states
        }
    }
}
```

## Các ví dụ / Examples

### Ví dụ 1: Gõ "anh" (brother)

```kotlin
val engine = VietnameseEngine()
val hookState = engine.init()

EngineConfig.inputType = 0 // Telex

// Type: a-n-h
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_N, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_H, 0u, false)

// Result: "anh"
```

### Ví dụ 2: Gõ "ánh" (light) với dấu sắc

```kotlin
// Type: a-n-h-s
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_N, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_H, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_S, 0u, false)

// hookState will contain:
// - backspaceCount = 3 (delete "anh")
// - newCharCount = 3 (insert "ánh")
// - charData = [á, n, h]
```

### Ví dụ 3: Gõ "ăn" (eat) với dấu mũ

```kotlin
// Type: a-a-n
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false) // aa = ă
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_N, 0u, false)

// Result: "ăn"
```

### Ví dụ 4: Gõ "ươ" với dấu móc

```kotlin
// Type: u-o-w
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_U, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_O, 0u, false)
engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_W, 0u, false)

// Result: "ươ"
```

## Cấu hình / Configuration

### Input Types / Kiểu gõ

```kotlin
EngineConfig.inputType = 0  // Telex (default)
EngineConfig.inputType = 1  // VNI
EngineConfig.inputType = 2  // Simple Telex 1
EngineConfig.inputType = 3  // Simple Telex 2
```

### Tone Marks / Dấu thanh

#### Telex Mode:
- `s` = Dấu sắc (á)
- `f` = Dấu huyền (à)
- `r` = Dấu hỏi (ả)
- `x` = Dấu ngã (ã)
- `j` = Dấu nặng (ạ)

#### VNI Mode:
- `1` = Dấu sắc (á)
- `2` = Dấu huyền (à)
- `3` = Dấu hỏi (ả)
- `4` = Dấu ngã (ã)
- `5` = Dấu nặng (ạ)

### Circumflex & Horn / Dấu mũ & móc

#### Telex:
- `aa` = ă
- `ee` = ê
- `oo` = ô
- `w` after vowel = ư, ơ
- `dd` = đ

#### VNI:
- `6` after vowel = ^, ư, ơ
- `9` = đ

### Orthography / Chính tả

```kotlin
// Modern orthography (oà, uý)
EngineConfig.useModernOrthography = 1

// Old orthography (òa, úy)
EngineConfig.useModernOrthography = 0
```

## Xử lý kết quả / Result Handling

### Hook State Codes

```kotlin
enum class HookCodeState {
    V_DO_NOTHING,              // No processing needed
    V_WILL_PROCESS,            // Delete old chars, insert new chars
    V_BREAK_WORD,              // Word boundary
    V_RESTORE,                 // Restore to previous state
    V_REPLACE_MACRO,           // Macro expansion (not yet implemented)
    V_RESTORE_AND_START_NEW_SESSION  // Restore and reset
}
```

### Processing Results

```kotlin
when (HookCodeState.values()[hookState.code.toInt()]) {
    HookCodeState.V_DO_NOTHING -> {
        // Just insert the key normally
    }
    
    HookCodeState.V_WILL_PROCESS -> {
        // 1. Delete backspaceCount characters from cursor position
        val deleteCount = hookState.backspaceCount.toInt()
        
        // 2. Insert newCharCount new characters
        val insertCount = hookState.newCharCount.toInt()
        val newChars = (0 until insertCount).map { i ->
            hookState.charData[insertCount - 1 - i]
        }
        
        // 3. Apply changes to your text buffer
        applyChanges(deleteCount, newChars)
    }
    
    HookCodeState.V_RESTORE -> {
        // Similar to V_WILL_PROCESS but restoring previous state
    }
    
    // ... handle other states
}
```

## Tích hợp / Integration

### Android Application

```kotlin
class VietnameseInputService : InputMethodService() {
    private val engine = VietnameseEngine()
    private val hookState = engine.init()
    
    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        engine.handleEvent(
            VKeyEvent.KEYBOARD,
            VKeyEventState.KEY_DOWN,
            primaryCode.toUShort(),
            0u,
            false
        )
        
        when (HookCodeState.values()[hookState.code.toInt()]) {
            HookCodeState.V_WILL_PROCESS -> {
                // Delete old characters
                repeat(hookState.backspaceCount.toInt()) {
                    currentInputConnection.deleteSurroundingText(1, 0)
                }
                
                // Insert new characters
                val text = convertCharDataToString(hookState)
                currentInputConnection.commitText(text, 1)
            }
            // ... handle other cases
        }
    }
}
```

### Desktop Application (Swing)

```kotlin
class VietnameseTextField : JTextField() {
    private val engine = VietnameseEngine()
    private val hookState = engine.init()
    
    init {
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val keyCode = mapToEngineKeyCode(e.keyCode)
                
                engine.handleEvent(
                    VKeyEvent.KEYBOARD,
                    VKeyEventState.KEY_DOWN,
                    keyCode,
                    0u,
                    e.isControlDown || e.isAltDown
                )
                
                when (HookCodeState.values()[hookState.code.toInt()]) {
                    HookCodeState.V_WILL_PROCESS -> {
                        e.consume() // Prevent default key handling
                        
                        // Delete characters
                        val text = text
                        val newText = text.dropLast(hookState.backspaceCount.toInt())
                        
                        // Insert new characters
                        val additions = convertCharDataToString(hookState)
                        setText(newText + additions)
                    }
                }
            }
        })
    }
}
```

## Debugging / Gỡ lỗi

### Enable Debug Output

```kotlin
// Add debug prints in your code
engine.handleEvent(...)

println("Code: ${HookCodeState.values()[hookState.code.toInt()]}")
println("Backspace: ${hookState.backspaceCount}")
println("New chars: ${hookState.newCharCount}")
println("Ext code: ${hookState.extCode}")
```

### Common Issues / Vấn đề thường gặp

1. **No processing happens**
   - Check if `EngineConfig.language = 1` (Vietnamese mode)
   - Verify the key codes match your platform

2. **Wrong characters produced**
   - Check `EngineConfig.inputType` (0 for Telex, 1 for VNI)
   - Verify orthography setting

3. **Marks not applied**
   - Ensure you have typed valid Vietnamese syllables
   - Check spelling rules are enabled

## Performance / Hiệu năng

### Tips for Better Performance

1. **Reuse engine instance** - Don't create new engines for each key
2. **Batch updates** - Process multiple keys before UI update if possible
3. **Optimize character conversion** - Cache converted characters
4. **Profile your code** - Use Kotlin profiler to find bottlenecks

## Testing / Kiểm thử

Run the example program:

```bash
cd Sources/OpenKey/engine-kotlin
kotlinc -d output src/main/kotlin/com/openkey/engine/*.kt
kotlin -classpath output com.openkey.engine.ExampleKt
```

Expected output:
```
=== OpenKey Engine - Kotlin Implementation ===
Demo: Vietnamese Input Method
...
```

## Tài nguyên / Resources

- **README.md** - Full documentation
- **COMPARISON.md** - C++ vs Kotlin comparison
- **Example.kt** - Working examples
- **EngineTest.kt** - Unit tests

## Hỗ trợ / Support

- GitHub Issues: [OpenKeyFork](https://github.com/Nam0101/OpenKeyFork)
- Original OpenKey: [tuyenvm/OpenKey](https://github.com/tuyenvm/OpenKey)

## Giấy phép / License

GPL - Same as original OpenKey

Copyright © 2019 Tuyen Mai. All rights reserved.
