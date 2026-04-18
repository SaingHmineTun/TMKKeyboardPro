package it.saimao.tmkkeyboardpro.logic

import android.text.TextUtils
import android.view.inputmethod.InputConnection
import it.saimao.shan_language_tools.converters.ShanZawgyiConverter
import it.saimao.shan_language_tools.dectector.ShanLanguageDetector

class ShanLanguageEngine(private val ic: InputConnection) {

    companion object {
        const val MY_E = 0x1031      // ေ
        const val SH_E = 0x1084      // ႄ (Shan AE)
        const val ASAT = 0x103A      // ်
        const val ZWSP = '\u200B'    // Zero Width Space
    }

    object ShanScript {
        // ဝၼ်ၵျုၵ်ႉ (Tone Marks)
        val TONES = setOf('\u1087', '\u1088', '\u1089', '\u108A', '\u1037', '\u1038')

        // တၢင်ႇလၢႆ/တိုတ်း (Upper Vowels)
        val UPPER_VOWELS = setOf('\u102D', '\u102E', '\u102F', '\u1030')

        // ဢႃ (AA Vowels)
        val AA_VOWELS = setOf('\u1062', '\u1083')

        // တူဝ်မႄႈလိၵ်ႈ (Consonants)
        val CONSONANTS = ('\u1000'..'\u1021').toSet() + ('\uAA60'..'\uAA7A').toSet()

        // ႁွပ်ႇ/လဵပ်ႈ (Medials)
        val MEDIALS = setOf('\u103B', '\u103C')
    }

    /**
     * Main handle function for all Shan input
     */
    fun handleInput(primaryCode: Int): String? {
        val currentChar = primaryCode.toChar()

        // 1. Get Context (Ignore ZWSP to look at real characters)
        val context = getRealContext(2)
        val charBefore = if (context.isNotEmpty()) context.last() else null
        val secondBefore = if (context.length > 1) context[context.length - 2] else null

        // 2. Rule: Prevent Invalid Sequences (Double tones, etc.)
        if (charBefore != null && isInvalidSequence(charBefore, currentChar)) {
            return null
        }

        // 3. Rule: Special Reordering (ို, ိူ, ႆၢ, ေႂ)
        checkReorderingRules(charBefore, currentChar)?.let {
            ic.deleteSurroundingText(1, 0)
            return it
        }

        // 4. Rule: Visual Ordering (Handwriting Mode for ေ and ႄ)
        return handleVisualOrdering(charBefore, secondBefore, currentChar)
    }

    private fun handleVisualOrdering(
        charBefore: Char?,
        secondBefore: Char?,
        current: Char
    ): String {
        // A. If user types ေ or ႄ -> Always add ZWSP first for proper rendering
        if (current.code == MY_E || current.code == SH_E) {
            return "$ZWSP$current"
        }

        // B. If the character before cursor is ေ or ႄ
        if (charBefore?.code == MY_E || charBefore?.code == SH_E) {
            when {
                // [ေ] + [ၵ] -> [ၵေ] (Consonant Swap)
                isShanConsonant(current) -> {
                    // Check if there's a ZWSP before ေ
                    val rawBefore = ic.getTextBeforeCursor(2, 0) ?: ""
                    if (rawBefore.length == 2 && rawBefore[0] == ZWSP) {
                        ic.deleteSurroundingText(2, 0)
                    } else {
                        ic.deleteSurroundingText(1, 0)
                    }
                    return "$current$charBefore"
                }

                // [ၵေ] + [ြ] -> [ၵြေ] (Medial Swap)
                isShanMedial(current) && isShanConsonant(secondBefore ?: ' ') -> {
                    ic.deleteSurroundingText(1, 0) // Delete only ေ
                    return "$current$charBefore" // Medial + ေ
                }
            }
        }
        return current.toString()
    }

    private fun getRealContext(len: Int): String {
        val raw = ic.getTextBeforeCursor(len + 2, 0)?.toString() ?: ""
        return raw.replace(ZWSP.toString(), "")
    }

    private fun checkReorderingRules(prev: Char?, current: Char): String? {
        if (prev == null) return null
        return when {
            prev.code == ASAT && current.code == 0x1082 -> "${0x1082.toChar()}$ASAT"
            prev.code == 0x1086 && current.code == 0x1062 -> "${0x1062.toChar()}${0x1086.toChar()}"
            prev.code == 0x1030 && current.code == 0x102D -> "${0x102D.toChar()}${0x1030.toChar()}"
            prev.code == 0x102F && current.code == 0x102D -> "${0x102D.toChar()}${0x102F.toChar()}"
            else -> null
        }
    }

    /**
     * Smart Delete handling for ZWSP and Reordered characters
     */
    fun handleShanDelete() {
        val rawBefore = ic.getTextBeforeCursor(2, 0) ?: ""

        if (rawBefore.length == 2) {
            val charBefore = rawBefore[1]
            val secondBefore = rawBefore[0]

            when {
                // Case 1: [ZWSP + ေ] -> Delete both
                charBefore.code == MY_E && secondBefore == ZWSP -> {
                    ic.deleteSurroundingText(2, 0)
                }
                // Case 2: [ၵ + ေ] -> Delete both and re-insert [ZWSP + ေ]
                // This allows user to easily change the consonant
                charBefore.code == MY_E && isShanConsonant(secondBefore) -> {
                    ic.deleteSurroundingText(2, 0)
                    ic.commitText("$ZWSP${charBefore}", 1)
                }

                else -> ic.deleteSurroundingText(1, 0)
            }
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun isShanConsonant(c: Char) = ShanScript.CONSONANTS.contains(c)
    private fun isShanMedial(c: Char) = ShanScript.MEDIALS.contains(c)

    private fun isInvalidSequence(prev: Char, current: Char): Boolean {
        return when {
            prev == current && ShanScript.TONES.contains(current) -> true
            ShanScript.TONES.contains(prev) && ShanScript.TONES.contains(current) -> true
            ShanScript.UPPER_VOWELS.contains(prev) && ShanScript.UPPER_VOWELS.contains(current) -> true
            else -> false
        }
    }

    /**
     * Converter Logic
     */
    fun convertZawgyi() {
        ic.performContextMenuAction(android.R.id.selectAll)
        val charSequence = ic.getSelectedText(0)
        if (!TextUtils.isEmpty(charSequence)) {
            val selectedText = charSequence.toString()
            val convertedText = if (ShanLanguageDetector.isShanZawgyi(selectedText)) {
                ShanZawgyiConverter.zg2uni(selectedText)
            } else {
                ShanZawgyiConverter.uni2zg(selectedText)
            }
            ic.commitText(convertedText, 1)
        }
    }
}