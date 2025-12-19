# So sánh phiên bản C++ và Kotlin / Comparison: C++ vs Kotlin

## Tổng quan / Overview

Đây là bản so sánh giữa phiên bản gốc (C++) và phiên bản Kotlin của OpenKey Engine.

This document compares the original C++ version and the Kotlin version of OpenKey Engine.

---

## Cấu trúc tệp / File Structure

### C++ (Original)
```
Sources/OpenKey/engine/
├── DataType.h          # Data type definitions
├── Engine.h            # Engine interface
├── Engine.cpp          # Engine implementation (1559 lines)
├── Vietnamese.h        # Vietnamese data headers
├── Vietnamese.cpp      # Vietnamese data (575 lines)
├── Macro.h/cpp         # Macro feature
├── SmartSwitchKey.h/cpp # Smart switching
├── ConvertTool.h/cpp   # Code conversion
└── platforms/
    ├── mac.h           # macOS key codes
    ├── win32.h         # Windows key codes
    └── linux.h         # Linux key codes
```

### Kotlin (New)
```
engine-kotlin/src/main/kotlin/com/openkey/engine/
├── DataType.kt         # Data types & enums
├── KeyCodes.kt         # Key code definitions
├── Vietnamese.kt       # Vietnamese language data
├── Engine.kt           # Main engine logic
└── Example.kt          # Demo program
```

---

## Sự khác biệt chính / Key Differences

### 1. Ngôn ngữ / Language

| Aspect | C++ | Kotlin |
|--------|-----|--------|
| Type System | Static, manual type management | Static with type inference |
| Memory Management | Manual (pointers, new/delete) | Automatic (garbage collection) |
| Null Safety | No built-in null safety | Built-in null safety |
| Collections | STL (vector, map, list) | Kotlin stdlib (List, Map, MutableList) |
| String Handling | std::string, std::wstring | String (UTF-16 by default) |

### 2. Kiểu dữ liệu / Data Types

#### C++ Original:
```cpp
typedef unsigned char Byte;
typedef unsigned short Uint16;
typedef unsigned int Uint32;
typedef unsigned long int Uint64;

struct vKeyHookState {
    Byte code;
    Byte backspaceCount;
    Byte newCharCount;
    Byte extCode;
    Uint32 charData[MAX_BUFF];
    vector<Uint32> macroKey;
    vector<Uint32> macroData;
};
```

#### Kotlin Port:
```kotlin
typealias Byte = kotlin.Byte
typealias UInt16 = kotlin.UShort
typealias UInt32 = kotlin.UInt
typealias UInt64 = kotlin.ULong

data class VKeyHookState(
    var code: UInt8 = 0u,
    var backspaceCount: UInt8 = 0u,
    var newCharCount: UInt8 = 0u,
    var extCode: UInt8 = 0u,
    var charData: Array<UInt32> = Array(MAX_BUFF) { 0u },
    var macroKey: MutableList<UInt32> = mutableListOf(),
    var macroData: MutableList<UInt32> = mutableListOf()
)
```

### 3. Cấu trúc dữ liệu / Data Structures

#### C++ (STL):
```cpp
static vector<Uint8> _charKeyCode = { ... };
static vector<Uint8> _breakCode = { ... };
static Uint32 TypingWord[MAX_BUFF];

map<Uint16, vector<vector<Uint16>>> _vowel = { ... };
```

#### Kotlin:
```kotlin
private val charKeyCode = listOf( ... )
private val breakCode = listOf( ... )
private val typingWord = Array<UInt32>(MAX_BUFF) { 0u }

val vowel: Map<UInt16, List<List<UInt16>>> = mapOf( ... )
```

### 4. Hàm và phương thức / Functions and Methods

#### C++ (Global functions + static state):
```cpp
void* vKeyInit();
void vKeyHandleEvent(const vKeyEvent& event, ...);
void startNewSession();
Uint32 getCharacterCode(const Uint32& data);
```

#### Kotlin (Object-oriented):
```kotlin
class VietnameseEngine {
    fun init(): VKeyHookState
    fun handleEvent(event: VKeyEvent, ...)
    fun startNewSession()
    fun getCharacterCode(data: UInt32): UInt32
}

object EngineConfig {
    var language: Int = 1
    var inputType: Int = 0
    // ...
}
```

### 5. Macro và Constants / Macros and Constants

#### C++ Macros:
```cpp
#define CAPS_MASK                0x10000
#define TONE_MASK                0x20000
#define MARK_MASK                0xF80000
#define CHR(index) (Uint16)TypingWord[index]
#define IS_CONSONANT(keyCode) !(keyCode == KEY_A || ...)
```

#### Kotlin Constants & Functions:
```kotlin
const val CAPS_MASK: UInt32 = 0x10000u
const val TONE_MASK: UInt32 = 0x20000u
const val MARK_MASK: UInt32 = 0xF80000u

fun isConsonant(keyCode: UInt16): Boolean {
    return !(keyCode == KEY_A || keyCode == KEY_E || ...)
}
```

---

## Ưu điểm / Advantages

### C++ Version

✅ **Performance**: Faster execution, lower memory overhead
✅ **Platform Control**: Direct memory management, closer to hardware
✅ **Mature**: Tested in production for years
✅ **Complete**: All features implemented (macro, smart switch, convert tool)

### Kotlin Version

✅ **Safety**: Null safety, no buffer overflows
✅ **Readability**: Cleaner syntax, less boilerplate
✅ **Maintainability**: Easier to understand and modify
✅ **Modern**: Better tooling, IDE support
✅ **Portability**: JVM-based, runs on multiple platforms
✅ **Interoperability**: Can call Java libraries easily

---

## Nhược điểm / Disadvantages

### C++ Version

❌ **Memory Management**: Manual memory management prone to errors
❌ **Complexity**: Harder to read with macros and pointer arithmetic
❌ **Safety**: No null safety, buffer overflow risks
❌ **Platform-specific**: Different code for macOS/Windows/Linux

### Kotlin Version

❌ **Performance**: Slightly slower due to JVM overhead
❌ **Incomplete**: Some advanced features not yet ported
❌ **Dependencies**: Requires JVM runtime
❌ **Size**: Larger binary size

---

## Tính năng được triển khai / Implemented Features

| Feature | C++ | Kotlin |
|---------|-----|--------|
| Basic key handling | ✅ | ✅ |
| Tone marks (Sắc, Huyền, Hỏi, Ngã, Nặng) | ✅ | ✅ |
| Circumflex (â, ê, ô) | ✅ | ✅ |
| Horn (ư, ơ, ă) | ✅ | ✅ |
| Modern orthography | ✅ | ✅ |
| Old orthography | ✅ | ✅ |
| Telex input | ✅ | ✅ |
| VNI input | ✅ | ⚠️ (Partial) |
| Simple Telex | ✅ | ⚠️ (Partial) |
| Spelling check | ✅ | ⚠️ (Framework only) |
| Grammar check | ✅ | ❌ |
| Code tables (TCVN3, VNI-Windows) | ✅ | ❌ |
| Macro feature | ✅ | ❌ |
| Smart switch | ✅ | ❌ |
| Convert tool | ✅ | ❌ |
| Quick Telex (cc=ch, etc.) | ✅ | ⚠️ (Data only) |
| Quick consonants | ✅ | ⚠️ (Data only) |

✅ = Fully implemented  
⚠️ = Partially implemented  
❌ = Not implemented  

---

## Hiệu suất / Performance

### Benchmark Results (Estimated)

| Operation | C++ | Kotlin | Ratio |
|-----------|-----|--------|-------|
| Key event processing | ~0.1 ms | ~0.3 ms | 3x |
| Character conversion | ~0.05 ms | ~0.1 ms | 2x |
| Memory usage | ~50 KB | ~500 KB (+ JVM) | 10x |
| Startup time | < 1 ms | ~50 ms (JVM warmup) | 50x |

**Note**: These are estimates. Actual performance depends on implementation details and JIT optimization.

### Khi nào dùng phiên bản nào? / When to use which version?

#### Use C++ Version when:
- You need maximum performance
- You're integrating with existing C++ code
- Binary size is critical
- You're on macOS/Windows with existing platform integration

#### Use Kotlin Version when:
- You need rapid development
- Code maintainability is priority
- You're building Android/JVM applications
- You want modern language features and safety
- You're prototyping or learning the algorithm

---

## Kế hoạch phát triển / Development Roadmap

### Kotlin Version TODO:

1. ✅ Core keyboard logic
2. ✅ Basic Vietnamese input
3. ⚠️ Complete spelling check
4. ❌ Grammar check implementation
5. ❌ Code table support (TCVN3, VNI-Windows, etc.)
6. ❌ Macro feature
7. ❌ Smart switching
8. ❌ Convert tool
9. ❌ Quick Telex full implementation
10. ❌ Unit tests
11. ❌ Performance optimization
12. ❌ Android/Desktop UI integration

---

## Kết luận / Conclusion

Phiên bản Kotlin là một bản port hiện đại của OpenKey Engine, phù hợp cho:
- Học tập và nghiên cứu thuật toán
- Phát triển ứng dụng JVM/Android
- Prototype và thử nghiệm tính năng mới

Phiên bản C++ vẫn là lựa chọn tốt nhất cho:
- Ứng dụng desktop chính thức (macOS, Windows, Linux)
- Yêu cầu hiệu suất cao
- Tích hợp với hệ thống

---

The Kotlin version is a modern port of OpenKey Engine, suitable for:
- Learning and studying the algorithm
- JVM/Android application development
- Prototyping and testing new features

The C++ version remains the best choice for:
- Official desktop applications (macOS, Windows, Linux)
- High-performance requirements
- System integration

---

## Tài liệu tham khảo / References

- [Original OpenKey (C++)](https://github.com/tuyenvm/OpenKey)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Vietnamese Input Method Algorithm](https://vi.wikipedia.org/wiki/B%E1%BB%99_g%C3%B5_ti%E1%BA%BFng_Vi%E1%BB%87t)
