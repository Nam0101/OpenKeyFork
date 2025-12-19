/**
 * Vietnamese.kt
 * OpenKey Engine - Kotlin Implementation
 *
 * Ported from C++ OpenKey Engine
 * Original: Vietnamese.cpp, Vietnamese.h
 * Copyright © 2019 Tuyen Mai. All rights reserved.
 *
 * Contains Vietnamese language data structures for:
 * - Vowel combinations
 * - Consonant patterns
 * - Tone and mark mappings
 * - Character code tables
 */

package com.openkey.engine

/**
 * Double key mappings for tone marks
 * Example: a -> â, e -> ê
 */
val douKey: Array<Pair<UInt16, UInt16>> = arrayOf(
    Pair(KEY_A, 0xE2u),  // a -> â
    Pair(KEY_E, 0xEAu)   // e -> ê
)

/**
 * Vowel combination patterns for different contexts
 * Maps vowel keys to valid combinations with consonants
 */
val vowel: Map<UInt16, List<List<UInt16>>> = mapOf(
    KEY_A to listOf(
        listOf(KEY_A, KEY_N, KEY_G), listOf(KEY_A, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_A, KEY_N),
        listOf(KEY_A, KEY_M),
        listOf(KEY_A, KEY_U),
        listOf(KEY_A, KEY_Y),
        listOf(KEY_A, KEY_T),
        listOf(KEY_A, KEY_P),
        listOf(KEY_A),
        listOf(KEY_A, KEY_C)
    ),
    KEY_O to listOf(
        listOf(KEY_O, KEY_N, KEY_G), listOf(KEY_O, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_O, KEY_N),
        listOf(KEY_O, KEY_M),
        listOf(KEY_O, KEY_I),
        listOf(KEY_O, KEY_C),
        listOf(KEY_O, KEY_T),
        listOf(KEY_O, KEY_P),
        listOf(KEY_O)
    ),
    KEY_E to listOf(
        listOf(KEY_E, KEY_N, KEY_H), listOf(KEY_E, KEY_H or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_N, KEY_G), listOf(KEY_E, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_C, KEY_H), listOf(KEY_E, KEY_K or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_C),
        listOf(KEY_E, KEY_T),
        listOf(KEY_E, KEY_Y),
        listOf(KEY_E, KEY_U),
        listOf(KEY_E, KEY_P),
        listOf(KEY_E, KEY_N),
        listOf(KEY_E, KEY_M),
        listOf(KEY_E)
    ),
    KEY_W to listOf(
        listOf(KEY_O, KEY_N),
        listOf(KEY_U, KEY_O, KEY_N, KEY_G), listOf(KEY_U, KEY_O, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_U, KEY_O, KEY_N),
        listOf(KEY_U, KEY_O, KEY_I),
        listOf(KEY_U, KEY_O, KEY_C),
        listOf(KEY_O, KEY_I),
        listOf(KEY_O, KEY_P),
        listOf(KEY_O, KEY_M),
        listOf(KEY_O, KEY_A),
        listOf(KEY_O, KEY_T),
        listOf(KEY_U, KEY_N, KEY_G), listOf(KEY_U, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_A, KEY_N, KEY_G), listOf(KEY_A, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_U, KEY_N),
        listOf(KEY_U, KEY_M),
        listOf(KEY_U, KEY_C),
        listOf(KEY_U, KEY_A),
        listOf(KEY_U, KEY_I),
        listOf(KEY_U, KEY_T),
        listOf(KEY_U),
        listOf(KEY_A, KEY_P),
        listOf(KEY_A, KEY_T),
        listOf(KEY_A, KEY_M),
        listOf(KEY_A, KEY_N),
        listOf(KEY_A),
        listOf(KEY_A, KEY_C),
        listOf(KEY_A, KEY_C, KEY_H), listOf(KEY_A, KEY_K or END_CONSONANT_MASK),
        listOf(KEY_O),
        listOf(KEY_U, KEY_U)
    )
)

/**
 * Vowel combinations with tone marks
 * First element: can have end consonant (1) or not (0)
 */
val vowelCombine: Map<UInt16, List<List<UInt32>>> = mapOf(
    KEY_A to listOf(
        listOf(0u, KEY_A.toUInt(), KEY_I.toUInt()),
        listOf(0u, KEY_A.toUInt(), KEY_O.toUInt()),
        listOf(0u, KEY_A.toUInt(), KEY_U.toUInt()),
        listOf(0u, (KEY_A or TONE_MASK.toUShort()).toUInt(), KEY_U.toUInt()),
        listOf(0u, KEY_A.toUInt(), KEY_Y.toUInt()),
        listOf(0u, (KEY_A or TONE_MASK.toUShort()).toUInt(), KEY_Y.toUInt())
    ),
    KEY_E to listOf(
        listOf(0u, KEY_E.toUInt(), KEY_O.toUInt()),
        listOf(0u, (KEY_E or TONE_MASK.toUShort()).toUInt(), KEY_U.toUInt())
    ),
    KEY_I to listOf(
        listOf(1u, KEY_I.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt(), KEY_U.toUInt()),
        listOf(0u, KEY_I.toUInt(), KEY_A.toUInt()),
        listOf(1u, KEY_I.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt()),
        listOf(0u, KEY_I.toUInt(), KEY_U.toUInt())
    ),
    KEY_O to listOf(
        listOf(0u, KEY_O.toUInt(), KEY_A.toUInt(), KEY_I.toUInt()),
        listOf(0u, KEY_O.toUInt(), KEY_A.toUInt(), KEY_O.toUInt()),
        listOf(0u, KEY_O.toUInt(), KEY_A.toUInt(), KEY_Y.toUInt()),
        listOf(0u, KEY_O.toUInt(), KEY_E.toUInt(), KEY_O.toUInt()),
        listOf(1u, KEY_O.toUInt(), KEY_A.toUInt()),
        listOf(1u, KEY_O.toUInt(), (KEY_A or TONEW_MASK.toUShort()).toUInt()),
        listOf(1u, KEY_O.toUInt(), KEY_E.toUInt()),
        listOf(0u, KEY_O.toUInt(), KEY_I.toUInt()),
        listOf(0u, (KEY_O or TONE_MASK.toUShort()).toUInt(), KEY_I.toUInt()),
        listOf(0u, (KEY_O or TONEW_MASK.toUShort()).toUInt(), KEY_I.toUInt()),
        listOf(1u, KEY_O.toUInt(), KEY_O.toUInt()),
        listOf(1u, (KEY_O or TONE_MASK.toUShort()).toUInt(), (KEY_O or TONE_MASK.toUShort()).toUInt())
    ),
    KEY_U to listOf(
        listOf(0u, KEY_U.toUInt(), KEY_Y.toUInt(), KEY_U.toUInt()),
        listOf(1u, KEY_U.toUInt(), KEY_Y.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt()),
        listOf(0u, KEY_U.toUInt(), KEY_Y.toUInt(), KEY_A.toUInt()),
        listOf(0u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), (KEY_O or TONEW_MASK.toUShort()).toUInt(), KEY_U.toUInt()),
        listOf(0u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), (KEY_O or TONEW_MASK.toUShort()).toUInt(), KEY_I.toUInt()),
        listOf(0u, KEY_U.toUInt(), (KEY_O or TONE_MASK.toUShort()).toUInt(), KEY_I.toUInt()),
        listOf(0u, KEY_U.toUInt(), (KEY_A or TONE_MASK.toUShort()).toUInt(), KEY_Y.toUInt()),
        listOf(1u, KEY_U.toUInt(), KEY_A.toUInt(), KEY_O.toUInt()),
        listOf(1u, KEY_U.toUInt(), KEY_A.toUInt()),
        listOf(1u, KEY_U.toUInt(), (KEY_A or TONEW_MASK.toUShort()).toUInt()),
        listOf(1u, KEY_U.toUInt(), (KEY_A or TONE_MASK.toUShort()).toUInt()),
        listOf(0u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), KEY_A.toUInt()),
        listOf(1u, KEY_U.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt()),
        listOf(0u, KEY_U.toUInt(), KEY_I.toUInt()),
        listOf(0u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), KEY_I.toUInt()),
        listOf(1u, KEY_U.toUInt(), KEY_O.toUInt()),
        listOf(1u, KEY_U.toUInt(), (KEY_O or TONE_MASK.toUShort()).toUInt()),
        listOf(0u, KEY_U.toUInt(), (KEY_O or TONEW_MASK.toUShort()).toUInt()),
        listOf(1u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), (KEY_O or TONEW_MASK.toUShort()).toUInt()),
        listOf(0u, (KEY_U or TONEW_MASK.toUShort()).toUInt(), KEY_U.toUInt()),
        listOf(1u, KEY_U.toUInt(), KEY_Y.toUInt())
    ),
    KEY_Y to listOf(
        listOf(0u, KEY_Y.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt(), KEY_U.toUInt()),
        listOf(1u, KEY_Y.toUInt(), (KEY_E or TONE_MASK.toUShort()).toUInt())
    )
)

/**
 * Consonant patterns for 'd' key
 * Used for checking valid 'd' placements in Vietnamese words
 */
val consonantD: List<List<UInt16>> = listOf(
    listOf(KEY_D, KEY_E, KEY_N, KEY_H), listOf(KEY_D, KEY_E, KEY_H or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_E, KEY_N, KEY_G), listOf(KEY_D, KEY_E, KEY_G or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_E, KEY_C, KEY_H), listOf(KEY_D, KEY_E, KEY_K or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_E, KEY_N),
    listOf(KEY_D, KEY_E, KEY_C),
    listOf(KEY_D, KEY_E, KEY_M),
    listOf(KEY_D, KEY_E),
    listOf(KEY_D, KEY_E, KEY_T),
    listOf(KEY_D, KEY_U, KEY_N, KEY_G), listOf(KEY_D, KEY_U, KEY_G or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_U, KEY_N),
    listOf(KEY_D, KEY_U, KEY_M),
    listOf(KEY_D, KEY_U, KEY_C),
    listOf(KEY_D, KEY_U),
    listOf(KEY_D, KEY_I, KEY_C, KEY_H), listOf(KEY_D, KEY_I, KEY_K or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_I, KEY_C),
    listOf(KEY_D, KEY_I, KEY_N, KEY_H), listOf(KEY_D, KEY_I, KEY_H or END_CONSONANT_MASK),
    listOf(KEY_D, KEY_I, KEY_N),
    listOf(KEY_D, KEY_I),
    listOf(KEY_D, KEY_O),
    listOf(KEY_D, KEY_O, KEY_A),
    listOf(KEY_D, KEY_A),
    listOf(KEY_D)
)

/**
 * Valid consonant patterns at the start of words
 */
val consonantTable: List<List<UInt16>> = listOf(
    listOf(KEY_B, KEY_L),
    listOf(KEY_B, KEY_R),
    listOf(KEY_C, KEY_H),
    listOf(KEY_G, KEY_H),
    listOf(KEY_G, KEY_I),
    listOf(KEY_K, KEY_H),
    listOf(KEY_N, KEY_G),
    listOf(KEY_N, KEY_H),
    listOf(KEY_P, KEY_H),
    listOf(KEY_Q, KEY_U),
    listOf(KEY_T, KEY_H),
    listOf(KEY_T, KEY_R)
)

/**
 * Valid consonant patterns at the end of words
 */
val endConsonantTable: List<List<UInt16>> = listOf(
    listOf(KEY_C, KEY_H), listOf(KEY_K or END_CONSONANT_MASK),
    listOf(KEY_N, KEY_G), listOf(KEY_G or END_CONSONANT_MASK),
    listOf(KEY_N, KEY_H), listOf(KEY_H or END_CONSONANT_MASK),
    listOf(KEY_C),
    listOf(KEY_N),
    listOf(KEY_M),
    listOf(KEY_P),
    listOf(KEY_T)
)

/**
 * Vowel patterns for mark placement
 */
val vowelForMark: Map<UInt16, List<List<UInt16>>> = mapOf(
    KEY_A to listOf(
        listOf(KEY_A, KEY_N, KEY_G), listOf(KEY_A, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_A, KEY_N),
        listOf(KEY_A, KEY_N, KEY_H), listOf(KEY_A, KEY_H or END_CONSONANT_MASK),
        listOf(KEY_A, KEY_M),
        listOf(KEY_A, KEY_U),
        listOf(KEY_A, KEY_Y),
        listOf(KEY_A, KEY_T),
        listOf(KEY_A, KEY_P),
        listOf(KEY_A, KEY_C),
        listOf(KEY_A)
    ),
    KEY_O to listOf(
        listOf(KEY_O, KEY_N, KEY_G), listOf(KEY_O, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_O, KEY_N),
        listOf(KEY_O, KEY_M),
        listOf(KEY_O, KEY_I),
        listOf(KEY_O, KEY_C),
        listOf(KEY_O, KEY_T),
        listOf(KEY_O, KEY_P),
        listOf(KEY_O)
    ),
    KEY_E to listOf(
        listOf(KEY_E, KEY_N, KEY_H), listOf(KEY_E, KEY_H or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_N, KEY_G), listOf(KEY_E, KEY_G or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_C, KEY_H), listOf(KEY_E, KEY_K or END_CONSONANT_MASK),
        listOf(KEY_E, KEY_C),
        listOf(KEY_E, KEY_T),
        listOf(KEY_E, KEY_Y),
        listOf(KEY_E, KEY_U),
        listOf(KEY_E, KEY_P),
        listOf(KEY_E, KEY_N),
        listOf(KEY_E, KEY_M),
        listOf(KEY_E)
    )
)

/**
 * Characters that cannot be followed by 'w' for standalone ư
 */
val standaloneWBad: List<UInt16> = listOf(
    KEY_Q, KEY_G, KEY_K, KEY_C, KEY_N
)

/**
 * Double 'w' allowed patterns
 */
val doubleWAllowed: List<List<UInt16>> = listOf(
    listOf(KEY_T, KEY_H)
)

/**
 * Quick Telex shortcuts (cc=ch, gg=gi, etc.)
 */
val quickTelex: Map<UInt32, List<UInt16>> = mapOf(
    KEY_C.toUInt() to listOf(KEY_C, KEY_H),
    KEY_G.toUInt() to listOf(KEY_G, KEY_I),
    KEY_K.toUInt() to listOf(KEY_K, KEY_H),
    KEY_N.toUInt() to listOf(KEY_N, KEY_G),
    KEY_Q.toUInt() to listOf(KEY_Q, KEY_U),
    KEY_P.toUInt() to listOf(KEY_P, KEY_H),
    KEY_T.toUInt() to listOf(KEY_T, KEY_H)
)

/**
 * Quick start consonant shortcuts (f->ph, j->gi, w->qu)
 */
val quickStartConsonant: Map<UInt16, List<UInt16>> = mapOf(
    KEY_F to listOf(KEY_P, KEY_H),
    KEY_J to listOf(KEY_G, KEY_I),
    KEY_W to listOf(KEY_Q, KEY_U)
)

/**
 * Quick end consonant shortcuts (g->ng, h->nh, k->ch)
 */
val quickEndConsonant: Map<UInt16, List<UInt16>> = mapOf(
    KEY_G to listOf(KEY_N, KEY_G),
    KEY_H to listOf(KEY_N, KEY_H),
    KEY_K to listOf(KEY_C, KEY_H)
)

/**
 * Convert key code to character
 */
fun keyCodeToCharacter(keyCode: UInt32): UInt16 {
    // Simplified implementation - should be expanded based on full character mapping
    return (keyCode and CHAR_MASK).toUShort()
}
