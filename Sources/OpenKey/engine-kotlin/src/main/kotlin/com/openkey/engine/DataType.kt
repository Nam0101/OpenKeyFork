/**
 * DataType.kt
 * OpenKey Engine - Kotlin Implementation
 *
 * Ported from C++ OpenKey Engine
 * Original: DataType.h
 * Copyright © 2019 Tuyen Mai. All rights reserved.
 */

package com.openkey.engine

const val MAX_BUFF = 32

enum class VKeyEvent {
    KEYBOARD,
    MOUSE
}

enum class VKeyEventState {
    KEY_DOWN,
    KEY_UP,
    MOUSE_DOWN,
    MOUSE_UP
}

enum class VKeyInputType {
    V_TELEX,
    V_VNI,
    V_SIMPLE_TELEX_1,
    V_SIMPLE_TELEX_2
}

// Type aliases for clarity
typealias Byte = kotlin.Byte
typealias UInt8 = kotlin.UByte
typealias UInt16 = kotlin.UShort
typealias UInt32 = kotlin.UInt
typealias UInt64 = kotlin.ULong

enum class HookCodeState {
    V_DO_NOTHING,           // do not do anything
    V_WILL_PROCESS,         // will reverse
    V_BREAK_WORD,           // start new
    V_RESTORE,              // restore character to old char
    V_REPLACE_MACRO,        // replace by macro
    V_RESTORE_AND_START_NEW_SESSION  // special flag: use for restore key if invalid word with break character
}

/**
 * Data structure for main program
 */
data class VKeyHookState(
    /**
     * 0: Do nothing
     * 1: Process
     * 2: Word break
     * 3: Restore
     * 4: replace by macro
     */
    var code: UInt8 = 0u,
    var backspaceCount: UInt8 = 0u,
    var newCharCount: UInt8 = 0u,
    
    /**
     * 1: Word Break
     * 2: Delete key
     * 3: Normal key
     * 4: Should not send empty character
     */
    var extCode: UInt8 = 0u,
    
    var charData: Array<UInt32> = Array(MAX_BUFF) { 0u },
    
    // Used for macro function
    var macroKey: MutableList<UInt32> = mutableListOf(),
    var macroData: MutableList<UInt32> = mutableListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VKeyHookState

        if (code != other.code) return false
        if (backspaceCount != other.backspaceCount) return false
        if (newCharCount != other.newCharCount) return false
        if (extCode != other.extCode) return false
        if (!charData.contentEquals(other.charData)) return false
        if (macroKey != other.macroKey) return false
        if (macroData != other.macroData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code.hashCode()
        result = 31 * result + backspaceCount.hashCode()
        result = 31 * result + newCharCount.hashCode()
        result = 31 * result + extCode.hashCode()
        result = 31 * result + charData.contentHashCode()
        result = 31 * result + macroKey.hashCode()
        result = 31 * result + macroData.hashCode()
        return result
    }
}

// Internal engine data masks
const val CAPS_MASK: UInt32 = 0x10000u
const val TONE_MASK: UInt32 = 0x20000u
const val TONEW_MASK: UInt32 = 0x40000u

/**
 * MARK MASK
 * 1: Dấu Sắc - á
 * 2: Dấu Huyền - à
 * 3: Dấu Hỏi - ả
 * 4: Dấu Ngã - ã
 * 5: dấu Nặng - ạ
 */
const val MARK1_MASK: UInt32 = 0x80000u
const val MARK2_MASK: UInt32 = 0x100000u
const val MARK3_MASK: UInt32 = 0x200000u
const val MARK4_MASK: UInt32 = 0x400000u
const val MARK5_MASK: UInt32 = 0x800000u

// For checking has mark or not
const val MARK_MASK: UInt32 = 0xF80000u

// Mark and get first 16 bytes character
const val CHAR_MASK: UInt32 = 0xFFFFu

// Check whether the data is created by standalone key or not (W)
const val STANDALONE_MASK: UInt32 = 0x1000000u

// Check whether the data is keyboard code or character code
const val CHAR_CODE_MASK: UInt32 = 0x2000000u

const val PURE_CHARACTER_MASK: UInt32 = 0x80000000u

// For special feature
const val END_CONSONANT_MASK: UInt16 = 0x4000u
const val CONSONANT_ALLOW_MASK: UInt16 = 0x8000u

// Utilities functions
fun isConsonant(keyCode: UInt16): Boolean {
    return !(keyCode == KEY_A || keyCode == KEY_E || keyCode == KEY_U || 
             keyCode == KEY_Y || keyCode == KEY_I || keyCode == KEY_O)
}

fun isNumberKey(code: UInt16): Boolean {
    return code == KEY_1 || code == KEY_2 || code == KEY_3 || code == KEY_4 || 
           code == KEY_5 || code == KEY_6 || code == KEY_7 || code == KEY_8 || 
           code == KEY_9 || code == KEY_0
}
