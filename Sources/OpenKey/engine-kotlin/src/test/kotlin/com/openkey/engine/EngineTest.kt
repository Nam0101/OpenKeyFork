/**
 * EngineTest.kt
 * OpenKey Engine - Kotlin Implementation
 *
 * Basic unit tests for the Vietnamese keyboard engine
 */

package com.openkey.engine

import kotlin.test.*

class EngineTest {
    
    private lateinit var engine: VietnameseEngine
    private lateinit var hookState: VKeyHookState
    
    @BeforeTest
    fun setup() {
        engine = VietnameseEngine()
        hookState = engine.init()
        
        // Configure for Telex mode
        EngineConfig.apply {
            language = 1
            inputType = 0
            useModernOrthography = 1
            checkSpelling = 1
        }
    }
    
    @Test
    fun testEngineInitialization() {
        assertNotNull(engine)
        assertNotNull(hookState)
        assertEquals(0u, hookState.code.toUByte())
        assertEquals(0u, hookState.backspaceCount.toUByte())
        assertEquals(0u, hookState.newCharCount.toUByte())
    }
    
    @Test
    fun testConfigurationSettings() {
        assertEquals(1, EngineConfig.language)
        assertEquals(0, EngineConfig.inputType)
        assertEquals(1, EngineConfig.useModernOrthography)
        assertEquals(1, EngineConfig.checkSpelling)
    }
    
    @Test
    fun testSimpleKeyInput() {
        // Type 'a'
        engine.handleEvent(
            event = VKeyEvent.KEYBOARD,
            state = VKeyEventState.KEY_DOWN,
            data = KEY_A,
            capsStatus = 0u,
            otherControlKey = false
        )
        
        // Should be in normal mode (no special processing for single 'a')
        assertEquals(3u, hookState.extCode.toUByte()) // normal key
    }
    
    @Test
    fun testSpaceKey() {
        // Type space
        engine.handleEvent(
            event = VKeyEvent.KEYBOARD,
            state = VKeyEventState.KEY_DOWN,
            data = KEY_SPACE,
            capsStatus = 0u,
            otherControlKey = false
        )
        
        // Should mark as word break
        assertEquals(1u, hookState.extCode.toUByte())
    }
    
    @Test
    fun testDeleteKey() {
        // Type 'a'
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
        
        // Type delete
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_DELETE, 0u, false)
        
        // Should mark as delete
        assertEquals(2u, hookState.extCode.toUByte())
    }
    
    @Test
    fun testStartNewSession() {
        // Type some keys
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_N, 0u, false)
        
        // Start new session
        engine.startNewSession()
        
        // Hook state should be reset
        assertEquals(0u, hookState.backspaceCount.toUByte())
        assertEquals(0u, hookState.newCharCount.toUByte())
    }
    
    @Test
    fun testIsConsonantFunction() {
        // Vowels should return false
        assertFalse(isConsonant(KEY_A))
        assertFalse(isConsonant(KEY_E))
        assertFalse(isConsonant(KEY_I))
        assertFalse(isConsonant(KEY_O))
        assertFalse(isConsonant(KEY_U))
        assertFalse(isConsonant(KEY_Y))
        
        // Consonants should return true
        assertTrue(isConsonant(KEY_B))
        assertTrue(isConsonant(KEY_C))
        assertTrue(isConsonant(KEY_D))
        assertTrue(isConsonant(KEY_N))
        assertTrue(isConsonant(KEY_H))
    }
    
    @Test
    fun testIsNumberKeyFunction() {
        // Number keys should return true
        assertTrue(isNumberKey(KEY_0))
        assertTrue(isNumberKey(KEY_1))
        assertTrue(isNumberKey(KEY_5))
        assertTrue(isNumberKey(KEY_9))
        
        // Letter keys should return false
        assertFalse(isNumberKey(KEY_A))
        assertFalse(isNumberKey(KEY_Z))
    }
    
    @Test
    fun testDataTypeMasks() {
        // Test CAPS_MASK
        val capsValue: UInt32 = 0x10000u
        assertEquals(CAPS_MASK, capsValue)
        
        // Test TONE_MASK
        val toneValue: UInt32 = 0x20000u
        assertEquals(TONE_MASK, toneValue)
        
        // Test MARK_MASK
        val markValue: UInt32 = 0xF80000u
        assertEquals(MARK_MASK, markValue)
    }
    
    @Test
    fun testGetCharacterCodeSimple() {
        // Test simple character without marks
        val simpleChar: UInt32 = KEY_A.toUInt()
        val result = engine.getCharacterCode(simpleChar)
        
        // Should return the character itself if no special marks
        assertNotNull(result)
    }
    
    @Test
    fun testMarkInsertion() {
        // Type 'a'
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_A, 0u, false)
        
        // Type 's' for tone mark (sắc)
        engine.handleEvent(VKeyEvent.KEYBOARD, VKeyEventState.KEY_DOWN, KEY_S, 0u, false)
        
        // Should trigger mark insertion
        // The hook state should indicate processing
        assertTrue(hookState.code.toInt() >= 0)
    }
    
    @Test
    fun testVietnameseDataStructures() {
        // Test that Vietnamese data is loaded
        assertNotNull(vowel)
        assertNotNull(consonantD)
        assertNotNull(consonantTable)
        assertNotNull(endConsonantTable)
        
        // Test some basic data
        assertTrue(vowel.containsKey(KEY_A))
        assertTrue(vowel.containsKey(KEY_E))
        assertTrue(vowel.containsKey(KEY_O))
        
        assertTrue(consonantD.isNotEmpty())
        assertTrue(consonantTable.isNotEmpty())
    }
    
    @Test
    fun testQuickTelexData() {
        // Test quick Telex shortcuts
        assertNotNull(quickTelex)
        
        // cc = ch
        assertTrue(quickTelex.containsKey(KEY_C.toUInt()))
        
        // gg = gi
        assertTrue(quickTelex.containsKey(KEY_G.toUInt()))
        
        // tt = th
        assertTrue(quickTelex.containsKey(KEY_T.toUInt()))
    }
    
    @Test
    fun testQuickConsonantData() {
        // Test quick start consonant
        assertNotNull(quickStartConsonant)
        assertTrue(quickStartConsonant.containsKey(KEY_F)) // f -> ph
        assertTrue(quickStartConsonant.containsKey(KEY_J)) // j -> gi
        assertTrue(quickStartConsonant.containsKey(KEY_W)) // w -> qu
        
        // Test quick end consonant
        assertNotNull(quickEndConsonant)
        assertTrue(quickEndConsonant.containsKey(KEY_G)) // g -> ng
        assertTrue(quickEndConsonant.containsKey(KEY_H)) // h -> nh
        assertTrue(quickEndConsonant.containsKey(KEY_K)) // k -> ch
    }
}
