package com.stopgalere.domain.validation

import java.text.Normalizer

object SafeText {

    // 1) Allowed character sets (ASCII).
    private val LOWER = ('a'..'z').toSet()
    private val UPPER = ('A'..'Z').toSet()
    private val DIGIT = ('0'..'9').toSet()

    // Choose the exact specials you want to allow (space included)
    private val SPECIALS = setOf(
        ' ', '.', ',', ':', ';', '!', '?', '\'', '"', '@', '_','°',
        '-', '–', '—',         // hyphen, en dash, em dash
        '/', '\\',             // slash + backslash
        '(', ')',
        '&', '%', '#', '+', '*', '=', '~',
        '$', '€', '£', '₦',    // common currencies
        '·', '•',
        '\t', '\n', '\r'       // tabs/newlines
    )

    // Public: the actual allow-list (array form + set for fast lookup)
    val ALLOWED_CHAR_LIST: List<Char> = (LOWER + UPPER + DIGIT + SPECIALS).toList().sorted()
    private val ALLOWED: Set<Char> = ALLOWED_CHAR_LIST.toSet()

    // Optional: normalize Unicode to NFC and collapse Unicode spaces to ASCII space
    private val UNICODE_SPACES = Regex("\\p{Zs}+")
    private fun normalize(input: String): String =
        Normalizer.normalize(input, Normalizer.Form.NFC)
            .replace(UNICODE_SPACES, " ")
            .trim()

    /** True if every character is in the allowed array. */
    fun isSafeText(s: String?): Boolean {
        if (s == null) return true
        val t = normalize(s)
        for (ch in t) {
            if (ch !in ALLOWED) {
                print("isSafeText Illegal character: $ch\n")
                return false
            }
        }
        return true
    }

    /** Helpful for debugging why something failed. */
    fun firstIllegalChar(s: String?): Pair<Int, Char>? {
        if (s == null) return null
        val t = normalize(s)
        t.forEachIndexed { i, ch ->
            if (ch !in ALLOWED) return i to ch
        }
        return null
    }


}
