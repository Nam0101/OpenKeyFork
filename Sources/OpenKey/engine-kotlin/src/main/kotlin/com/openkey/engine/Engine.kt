/**
 * Engine.kt
 * OpenKey Engine - Kotlin Implementation
 *
 * Ported from C++ OpenKey Engine
 * Original: Engine.cpp, Engine.h
 * Copyright © 2019 Tuyen Mai. All rights reserved.
 *
 * Main keyboard engine for Vietnamese input method
 * Handles key events and converts them to Vietnamese characters with proper tone marks
 */

package com.openkey.engine

/**
 * Configuration variables for the engine
 * These control the behavior of the Vietnamese input method
 */
object EngineConfig {
    /**
     * 0: English
     * 1: Vietnamese
     */
    var language: Int = 1
    
    /**
     * 0: Telex
     * 1: VNI
     * 2: Simple Telex 1
     * 3: Simple Telex 2
     */
    var inputType: Int = 0
    
    /**
     * 0: No
     * 1: Yes
     * Allow free mark placement
     */
    var freeMark: Int = 0
    
    /**
     * 0: Unicode
     * 1: TCVN3 (ABC)
     * 2: VNI-Windows
     * 3: Unicode Compound
     * 4: Vietnamese Locale CP 1258
     */
    var codeTable: Int = 0
    
    /**
     * Switch key status
     * first 8 bit: keycode
     * bit 8: Control on/off
     * bit 9: Option on/off
     * bit 10: Command on/off
     */
    var switchKeyStatus: Int = 0
    
    /**
     * 0: No
     * 1: Yes
     * Enable spelling check
     */
    var checkSpelling: Int = 1
    
    /**
     * 0: òa, úy (old orthography)
     * 1: oà, uý (modern orthography)
     */
    var useModernOrthography: Int = 1
    
    /**
     * 0: No
     * 1: Yes
     * Quick Telex (cc=ch, gg=gi, kk=kh, nn=ng, qq=qu, pp=ph, tt=th)
     */
    var quickTelex: Int = 0
    
    /**
     * 0: No
     * 1: Yes
     * Restore key if wrong spelling
     */
    var restoreIfWrongSpelling: Int = 0
    
    /**
     * 0: No
     * 1: Yes
     * Fix autocorrect in browsers
     */
    var fixRecommendBrowser: Int = 1
    
    /**
     * Enable macro feature
     */
    var useMacro: Int = 0
    
    /**
     * Use macro in English mode
     */
    var useMacroInEnglishMode: Int = 0
    
    /**
     * Auto capitalize macro expansions
     */
    var autoCapsMacro: Int = 0
    
    /**
     * Auto switch language when switching apps
     */
    var useSmartSwitchKey: Int = 0
    
    /**
     * Auto uppercase first character
     */
    var upperCaseFirstChar: Int = 0
    
    /**
     * Temporarily turn off spell checking with Ctrl key
     */
    var tempOffSpelling: Int = 0
    
    /**
     * Allow consonants Z, F, W, J
     */
    var allowConsonantZFWJ: Int = 0
    
    /**
     * Quick start consonant (f->ph, j->gi, w->qu)
     */
    var quickStartConsonant: Int = 0
    
    /**
     * Quick end consonant (g->ng, h->nh, k->ch)
     */
    var quickEndConsonant: Int = 0
    
    /**
     * Auto remember code table for each application
     */
    var rememberCode: Int = 0
    
    /**
     * Turn off Vietnamese when typing in another language
     */
    var otherLanguage: Int = 0
    
    /**
     * Temporarily turn off OpenKey with hot key
     */
    var tempOffOpenKey: Int = 0
}

/**
 * Main Vietnamese keyboard engine
 */
class VietnameseEngine {
    // Internal state
    private val typingWord = Array<UInt32>(MAX_BUFF) { 0u }
    private var index: UInt8 = 0u
    private val keyStates = Array<UInt32>(MAX_BUFF) { 0u }
    private var stateIndex: UInt8 = 0u
    private val hookState = VKeyHookState()
    
    private var tempDisableKey = false
    private var vowelCount: UInt8 = 0u
    private var vowelStartIndex: UInt8 = 0u
    private var vowelEndIndex: UInt8 = 0u
    private var vowelWillSetMark: UInt8 = 0u
    
    private val longWordHelper = mutableListOf<UInt32>()
    private val typingStates = mutableListOf<List<UInt32>>()
    private val specialChar = mutableListOf<UInt32>()
    
    private var spaceCount = 0
    private var hasHandledMacro = false
    private var upperCaseStatus: UInt8 = 0u
    private var useSpellCheckingBefore = EngineConfig.checkSpelling
    private var willTempOffEngine = false
    
    // Break codes - keys that end a word
    private val breakCode = listOf(
        KEY_ESC, KEY_TAB, KEY_ENTER, KEY_RETURN, 
        KEY_LEFT, KEY_RIGHT, KEY_DOWN, KEY_UP,
        KEY_COMMA, KEY_DOT, KEY_SLASH, KEY_SEMICOLON,
        KEY_QUOTE, KEY_BACK_SLASH, KEY_MINUS, KEY_EQUALS, KEY_BACKQUOTE
    )
    
    private val macroBreakCode = listOf(
        KEY_RETURN, KEY_COMMA, KEY_DOT, KEY_SLASH,
        KEY_SEMICOLON, KEY_QUOTE, KEY_BACK_SLASH, KEY_MINUS, KEY_EQUALS
    )
    
    private val charKeyCode = listOf(
        KEY_BACKQUOTE, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_0,
        KEY_MINUS, KEY_EQUALS, KEY_LEFT_BRACKET, KEY_RIGHT_BRACKET, KEY_BACK_SLASH,
        KEY_SEMICOLON, KEY_QUOTE, KEY_COMMA, KEY_DOT, KEY_SLASH
    )
    
    /**
     * Initialize the engine
     * Returns the hook state object for reading results
     */
    fun init(): VKeyHookState {
        index = 0u
        stateIndex = 0u
        useSpellCheckingBefore = EngineConfig.checkSpelling
        typingStates.clear()
        longWordHelper.clear()
        return hookState
    }
    
    /**
     * Start a new typing session
     */
    fun startNewSession() {
        index = 0u
        hookState.backspaceCount = 0u
        hookState.newCharCount = 0u
        tempDisableKey = false
        stateIndex = 0u
        hasHandledMacro = false
        longWordHelper.clear()
    }
    
    /**
     * Check if a key is a word break
     */
    private fun isWordBreak(event: VKeyEvent, state: VKeyEventState, data: UInt16): Boolean {
        if (event == VKeyEvent.MOUSE) return true
        return data in breakCode
    }
    
    /**
     * Check if a key is a macro break code
     */
    private fun isMacroBreakCode(data: UInt16): Boolean {
        return data in macroBreakCode
    }
    
    /**
     * Get character code from internal representation
     */
    fun getCharacterCode(data: UInt32): UInt32 {
        val capsElem = if ((data and CAPS_MASK) != 0u) 0 else 1
        val key = (data and CHAR_MASK).toUShort()
        
        // Check if has mark
        if ((data and MARK_MASK) != 0u) {
            val markElem = when {
                (data and MARK1_MASK) != 0u -> 0
                (data and MARK2_MASK) != 0u -> 2
                (data and MARK3_MASK) != 0u -> 4
                (data and MARK4_MASK) != 0u -> 6
                (data and MARK5_MASK) != 0u -> 8
                else -> -2
            }
            
            // Simplified character code generation
            // Full implementation would use code table lookups
            return data or CHAR_CODE_MASK
        } else {
            // No mark
            if ((data and TONE_MASK) != 0u || (data and TONEW_MASK) != 0u) {
                return data or CHAR_CODE_MASK
            }
        }
        
        return data
    }
    
    /**
     * Find and calculate vowel positions in current word
     */
    private fun findAndCalculateVowel(forGrammar: Boolean = false) {
        vowelCount = 0u
        vowelStartIndex = 0u
        vowelEndIndex = 0u
        
        for (i in (index.toInt() - 1) downTo 0) {
            val chr = (typingWord[i] and CHAR_MASK).toUShort()
            
            if (isConsonant(chr)) {
                if (vowelCount > 0u) break
            } else {
                // Is vowel
                if (vowelCount == 0u.toUByte()) {
                    vowelEndIndex = i.toUByte()
                }
                if (!forGrammar) {
                    // Check for special cases like 'gi', 'qu'
                    if (i - 1 >= 0) {
                        val prevChr = (typingWord[i - 1] and CHAR_MASK).toUShort()
                        if ((chr == KEY_I && prevChr == KEY_G) ||
                            (chr == KEY_U && prevChr == KEY_Q)) {
                            break
                        }
                    }
                }
                vowelStartIndex = i.toUByte()
                vowelCount = (vowelCount.toInt() + 1).toUByte()
            }
        }
        
        // Don't count 'u' in 'qu' as a vowel
        if (vowelStartIndex.toInt() - 1 >= 0) {
            val vsChr = (typingWord[vowelStartIndex.toInt()] and CHAR_MASK).toUShort()
            val prevChr = (typingWord[vowelStartIndex.toInt() - 1] and CHAR_MASK).toUShort()
            if (vsChr == KEY_U && prevChr == KEY_Q) {
                vowelStartIndex = (vowelStartIndex.toInt() + 1).toUByte()
                vowelCount = (vowelCount.toInt() - 1).toUByte()
            }
        }
    }
    
    /**
     * Insert a mark (tone) on the appropriate vowel
     */
    private fun insertMark(markMask: UInt32, canModifyFlag: Boolean = true) {
        vowelCount = 0u
        
        if (canModifyFlag) {
            hookState.code = HookCodeState.V_WILL_PROCESS.ordinal.toUByte()
        }
        hookState.backspaceCount = 0u
        hookState.newCharCount = 0u
        
        findAndCalculateVowel()
        vowelWillSetMark = 0u
        
        // Detect mark position based on vowel count
        if (vowelCount == 1u.toUByte()) {
            vowelWillSetMark = vowelEndIndex
            hookState.backspaceCount = (index.toInt() - vowelEndIndex.toInt()).toUByte()
        } else if (vowelCount >= 2u) {
            // Use modern or old orthography rules
            if (EngineConfig.useModernOrthography == 0) {
                handleOldMark()
            } else {
                handleModernMark()
            }
        }
        
        // Check if duplicate mark -> restore
        if ((typingWord[vowelWillSetMark.toInt()] and markMask) != 0u) {
            // Remove mark
            typingWord[vowelWillSetMark.toInt()] = typingWord[vowelWillSetMark.toInt()] and MARK_MASK.inv()
            if (canModifyFlag) {
                hookState.code = HookCodeState.V_RESTORE.ordinal.toUByte()
            }
            
            // Send data back
            var kk = index.toInt() - 1 - vowelStartIndex.toInt()
            for (i in vowelStartIndex.toInt() until index.toInt()) {
                typingWord[i] = typingWord[i] and MARK_MASK.inv()
                hookState.charData[kk--] = getCharacterCode(typingWord[i])
            }
            tempDisableKey = true
        } else {
            // Add mark
            typingWord[vowelWillSetMark.toInt()] = typingWord[vowelWillSetMark.toInt()] and MARK_MASK.inv()
            typingWord[vowelWillSetMark.toInt()] = typingWord[vowelWillSetMark.toInt()] or markMask
            
            var kk = index.toInt() - 1 - vowelStartIndex.toInt()
            for (i in vowelStartIndex.toInt() until index.toInt()) {
                if (i != vowelWillSetMark.toInt()) {
                    typingWord[i] = typingWord[i] and MARK_MASK.inv()
                }
                hookState.charData[kk--] = getCharacterCode(typingWord[i])
            }
            
            hookState.backspaceCount = (index.toInt() - vowelStartIndex.toInt()).toUByte()
        }
        hookState.newCharCount = hookState.backspaceCount
    }
    
    /**
     * Handle mark placement using modern orthography rules
     */
    private fun handleModernMark() {
        // Default
        vowelWillSetMark = vowelEndIndex
        hookState.backspaceCount = (index.toInt() - vowelEndIndex.toInt()).toUByte()
        
        val vsi = vowelStartIndex.toInt()
        val vei = vowelEndIndex.toInt()
        val chr = { i: Int -> (typingWord[i] and CHAR_MASK).toUShort() }
        
        // Rule 2: three vowels
        if (vowelCount == 3u.toUByte()) {
            if ((chr(vsi) == KEY_O && chr(vsi + 1) == KEY_A && chr(vsi + 2) == KEY_I) ||
                (chr(vsi) == KEY_U && chr(vsi + 1) == KEY_Y && chr(vsi + 2) == KEY_U) ||
                (chr(vsi) == KEY_O && chr(vsi + 1) == KEY_E && chr(vsi + 2) == KEY_O) ||
                (chr(vsi) == KEY_U && chr(vsi + 1) == KEY_Y && chr(vsi + 2) == KEY_A)) {
                vowelWillSetMark = (vsi + 1).toUByte()
                hookState.backspaceCount = (index.toInt() - vowelWillSetMark.toInt()).toUByte()
            }
        }
        // More rules would be implemented here...
        
        hookState.newCharCount = hookState.backspaceCount
    }
    
    /**
     * Handle mark placement using old orthography rules
     */
    private fun handleOldMark() {
        // Default
        vowelWillSetMark = if (vowelCount == 0u.toUByte()) vowelEndIndex else vowelStartIndex
        hookState.backspaceCount = (index.toInt() - vowelWillSetMark.toInt()).toUByte()
        
        // Rule 2: three vowels or has end consonant
        if (vowelCount == 3u.toUByte()) {
            vowelWillSetMark = (vowelStartIndex.toInt() + 1).toUByte()
            hookState.backspaceCount = (index.toInt() - vowelWillSetMark.toInt()).toUByte()
        }
        
        hookState.newCharCount = hookState.backspaceCount
    }
    
    /**
     * Main entry point for handling keyboard events
     */
    fun handleEvent(
        event: VKeyEvent,
        state: VKeyEventState,
        data: UInt16,
        capsStatus: UInt8 = 0u,
        otherControlKey: Boolean = false
    ) {
        val isCaps = (capsStatus == 1u.toUByte() || capsStatus == 2u.toUByte())
        
        // Reset hook state
        hookState.code = HookCodeState.V_DO_NOTHING.ordinal.toUByte()
        hookState.backspaceCount = 0u
        hookState.newCharCount = 0u
        hookState.extCode = 0u
        
        // Check for word break
        if (otherControlKey || isWordBreak(event, state, data) || 
            (index == 0u.toUByte() && isNumberKey(data))) {
            hookState.extCode = 1u // word break
            startNewSession()
            return
        }
        
        // Handle space key
        if (data == KEY_SPACE) {
            hookState.extCode = 1u
            spaceCount++
            return
        }
        
        // Handle delete key
        if (data == KEY_DELETE) {
            hookState.extCode = 2u // delete
            if (index > 0u) {
                index = (index.toInt() - 1).toUByte()
            }
            if (stateIndex > 0u) {
                stateIndex = (stateIndex.toInt() - 1).toUByte()
            }
            return
        }
        
        // Handle regular character input
        if (!willTempOffEngine) {
            hookState.extCode = 3u // normal key
            
            // Insert key into typing word
            if (index.toInt() < MAX_BUFF) {
                typingWord[index.toInt()] = data.toUInt() or (if (isCaps) CAPS_MASK else 0u)
                index = (index.toInt() + 1).toUByte()
            }
            
            // For Vietnamese mode, process special keys
            if (EngineConfig.language == 1) {
                processVietnameseKey(data, isCaps)
            }
        }
    }
    
    /**
     * Process Vietnamese-specific key combinations
     */
    private fun processVietnameseKey(data: UInt16, isCaps: Boolean) {
        // Check for mark keys (s, f, r, x, j for Telex)
        val isMarkKey = when (EngineConfig.inputType) {
            0 -> data in listOf(KEY_S, KEY_F, KEY_R, KEY_X, KEY_J) // Telex
            1 -> data in listOf(KEY_1, KEY_2, KEY_3, KEY_4, KEY_5) // VNI
            else -> false
        }
        
        if (isMarkKey) {
            // Insert appropriate mark
            val markMask = when {
                data == KEY_S || data == KEY_1 -> MARK1_MASK // Sắc
                data == KEY_F || data == KEY_2 -> MARK2_MASK // Huyền
                data == KEY_R || data == KEY_3 -> MARK3_MASK // Hỏi
                data == KEY_X || data == KEY_4 -> MARK4_MASK // Ngã
                data == KEY_J || data == KEY_5 -> MARK5_MASK // Nặng
                else -> 0u
            }
            
            if (markMask != 0u) {
                insertMark(markMask)
            }
        }
        
        // Check for tone keys (a, e, o for â, ê, ô in Telex)
        // Check for W key for ư, ơ
        // Implementation would continue here...
    }
    
    /**
     * Temporarily turn off spell checking
     */
    fun tempOffSpellChecking() {
        if (useSpellCheckingBefore != 0) {
            EngineConfig.checkSpelling = if (EngineConfig.checkSpelling != 0) 0 else 1
        }
    }
    
    /**
     * Reset spell checking value
     */
    fun setCheckSpelling() {
        useSpellCheckingBefore = EngineConfig.checkSpelling
    }
    
    /**
     * Temporarily turn off engine
     */
    fun tempOffEngine(off: Boolean = true) {
        willTempOffEngine = off
    }
}
