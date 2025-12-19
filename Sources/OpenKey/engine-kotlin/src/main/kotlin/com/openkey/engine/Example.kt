/**
 * Example.kt
 * OpenKey Engine - Kotlin Implementation
 *
 * Example usage of the Vietnamese keyboard engine
 */

package com.openkey.engine

/**
 * Demo program showing how to use the Vietnamese Engine
 */
fun main() {
    println("=== OpenKey Engine - Kotlin Implementation ===")
    println("Demo: Vietnamese Input Method\n")
    
    // Initialize engine
    val engine = VietnameseEngine()
    val hookState = engine.init()
    
    // Configure engine for Telex mode
    EngineConfig.apply {
        language = 1              // Vietnamese
        inputType = 0             // Telex
        useModernOrthography = 1  // Modern orthography (oà, uý)
        checkSpelling = 1         // Enable spell check
        quickTelex = 1           // Enable quick Telex (cc=ch, etc.)
    }
    
    println("Configuration:")
    println("  Input Type: Telex")
    println("  Modern Orthography: Enabled")
    println("  Spell Check: Enabled\n")
    
    // Example 1: Type "anh" (brother)
    println("Example 1: Typing 'anh'")
    simulateTyping(engine, hookState, listOf(KEY_A, KEY_N, KEY_H))
    
    // Example 2: Type "vietnam" -> "việt nam"
    println("\nExample 2: Typing 'viet nam' with tones")
    engine.startNewSession()
    
    // Type "viet" -> "việt"
    println("  Typing: v-i-e-e-t-s")
    simulateTyping(engine, hookState, listOf(
        KEY_V, KEY_I, KEY_E, KEY_E,  // vie + e = việ
        KEY_T, KEY_S                  // t + s = t + sắc = việt
    ))
    
    // Space
    engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_SPACE, 0u, false)
    
    // Type "nam"
    println("\n  Typing: n-a-m")
    simulateTyping(engine, hookState, listOf(KEY_N, KEY_A, KEY_M))
    
    // Example 3: Type with circumflex
    println("\n\nExample 3: Typing 'an' -> 'ăn' (eat)")
    engine.startNewSession()
    println("  Typing: a-a-n")
    simulateTyping(engine, hookState, listOf(
        KEY_A, KEY_A,  // aa = ă
        KEY_N
    ))
    
    // Example 4: Type with horn
    println("\n\nExample 4: Typing 'uo' -> 'ươ'")
    engine.startNewSession()
    println("  Typing: u-o-w")
    simulateTyping(engine, hookState, listOf(
        KEY_U, KEY_O, KEY_W  // uow = ươ
    ))
    
    println("\n\n=== Demo completed ===")
    println("For more information, see README.md")
}

/**
 * Simulate typing a sequence of keys and display the results
 */
fun simulateTyping(engine: VietnameseEngine, hookState: VKeyHookState, keys: List<UInt16>) {
    for (key in keys) {
        engine.handleEvent(
            event = VKeyEvent.KEYBOARD,
            state = VKeyEventState.KEY_DOWN,
            data = key,
            capsStatus = 0u,
            otherControlKey = false
        )
        
        // Display result
        when (HookCodeState.values()[hookState.code.toInt()]) {
            HookCodeState.V_DO_NOTHING -> {
                println("    Key ${keyToString(key)}: No change")
            }
            HookCodeState.V_WILL_PROCESS -> {
                val backspaces = hookState.backspaceCount.toInt()
                val newChars = hookState.newCharCount.toInt()
                println("    Key ${keyToString(key)}: Delete $backspaces, Add $newChars chars")
                
                // Display character data (simplified)
                if (newChars > 0) {
                    val chars = (0 until newChars).map { i ->
                        val charCode = hookState.charData[newChars - 1 - i]
                        charCodeToDisplay(charCode)
                    }.joinToString("")
                    println("      Result: $chars")
                }
            }
            HookCodeState.V_RESTORE -> {
                println("    Key ${keyToString(key)}: Restore (removed tone/mark)")
            }
            else -> {
                println("    Key ${keyToString(key)}: ${HookCodeState.values()[hookState.code.toInt()]}")
            }
        }
    }
}

/**
 * Convert key code to readable string
 */
fun keyToString(key: UInt16): String {
    return when (key) {
        KEY_A -> "A"
        KEY_B -> "B"
        KEY_C -> "C"
        KEY_D -> "D"
        KEY_E -> "E"
        KEY_F -> "F"
        KEY_G -> "G"
        KEY_H -> "H"
        KEY_I -> "I"
        KEY_J -> "J"
        KEY_K -> "K"
        KEY_L -> "L"
        KEY_M -> "M"
        KEY_N -> "N"
        KEY_O -> "O"
        KEY_P -> "P"
        KEY_Q -> "Q"
        KEY_R -> "R"
        KEY_S -> "S"
        KEY_T -> "T"
        KEY_U -> "U"
        KEY_V -> "V"
        KEY_W -> "W"
        KEY_X -> "X"
        KEY_Y -> "Y"
        KEY_Z -> "Z"
        KEY_SPACE -> "SPACE"
        else -> key.toString()
    }
}

/**
 * Convert character code to display string (simplified)
 */
fun charCodeToDisplay(charCode: UInt32): String {
    val chr = (charCode and CHAR_MASK).toUShort()
    val hasCaps = (charCode and CAPS_MASK) != 0u
    val hasTone = (charCode and TONE_MASK) != 0u
    val hasToneW = (charCode and TONEW_MASK) != 0u
    val mark = when {
        (charCode and MARK1_MASK) != 0u -> "́" // Sắc
        (charCode and MARK2_MASK) != 0u -> "̀" // Huyền
        (charCode and MARK3_MASK) != 0u -> "̉" // Hỏi
        (charCode and MARK4_MASK) != 0u -> "̃" // Ngã
        (charCode and MARK5_MASK) != 0u -> "̣" // Nặng
        else -> ""
    }
    
    var base = keyToString(chr).lowercase()
    if (hasCaps) base = base.uppercase()
    
    // Simplified tone display
    if (hasTone) base = when(chr) {
        KEY_A -> if (hasCaps) "Â" else "â"
        KEY_E -> if (hasCaps) "Ê" else "ê"
        KEY_O -> if (hasCaps) "Ô" else "ô"
        KEY_D -> if (hasCaps) "Đ" else "đ"
        else -> base
    }
    if (hasToneW) base = when(chr) {
        KEY_A -> if (hasCaps) "Ă" else "ă"
        KEY_O -> if (hasCaps) "Ơ" else "ơ"
        KEY_U -> if (hasCaps) "Ư" else "ư"
        else -> base
    }
    
    return base + mark
}
