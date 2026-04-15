package it.saimao.tmkkeyboardpro.logic

import android.text.TextUtils
import android.view.inputmethod.InputConnection
import it.saimao.shan_language_tools.converters.ShanZawgyiConverter
import it.saimao.shan_language_tools.dectector.ShanLanguageDetector


class ShanLanguageEngine(private val ic: InputConnection) {

    companion object {
        const val MY_E = 0x1031      // ေ (Myanmar E)
        const val SH_E = 0x1084      // ႄ (Shan AE - note: double check your hex)
        const val ASAT = 0x103A      // ် (Asat)
        const val ZWSP = '\u200B'    // Zero Width Space


    }

    object ShanScript {

        // ဝၼ်ၵျုၵ်ႉၽၢႆႇလင် (Tone Marks: ႇ း ႉ ၾ ြ)
        val TONES = setOf('\u1087', '\u1088', '\u1089', '\u108A', '\u1037', '\u1038')

        // တၢင်ႇလၢႆ/တိုတ်း (Upper Vowels: ိ ီ ု ူ ေ ႄ)
        val UPPER_VOWELS = setOf('\u102D', '\u102E', '\u102F', '\u1030')

        // ဢႃ (AA: ႃ ၢ)
        val AA_VOWELS = setOf('\u1062', '\u1083')
    }

    // Flags တႃႇတွတ်းၸႂ်ဝႃႈ တိုၵ်ႉမီးလွင်ႈလႅၵ်ႈတီႈ (Swap) ဝႆႉယူႇႁႃႉ
    private var isConsonantSwapped = false
    private var isMedialSwapped = false

    private val ZWSP = "\u200B" // Zero Width Space

    fun handleInput(primaryCode: Int): String? {
        val charBefore = ic.getTextBeforeCursor(1, 0)?.toString() ?: ""
        val charCodeBefore = if (charBefore.isNotEmpty()) charBefore[0].code else -1

        // *** ထတ်းတူၺ်းဝႃႈ လိၵ်ႈတေလူႉႁႃႉ? ***
        if (isInvalidSequence(charCodeBefore, primaryCode)) {
            // တူင်ႉၼိုင် (Vibrate) ဢိတ်းၼိုင်ႈ ၼင်ႇႁိုဝ်ၵူၼ်းၸႂ်ႉတေႁူႉဝႃႈ ၼိပ်ႉၽိတ်းယဝ်ႉ
            return null
        }

        // 1. Common Reordering Rules (Always Apply)
        checkReorderingRules(charCodeBefore, primaryCode)?.let { return it }

        // 2. Handwriting / Visual Typing Mode Logic (ေ + ၵ -> ၵေ)
        return handleVisualOrdering(charCodeBefore, primaryCode)
    }

    private fun checkReorderingRules(prev: Int, current: Int): String? {
        return when {
            // Reorder ေႂ (Asat + Medial Wa -> Wa + Asat)
            prev == ASAT && current == 0x1082 -> {
                ic.deleteSurroundingText(1, 0)
                "${0x1082.toChar()}${ASAT.toChar()}"
            }
            // Reorder ႆၢ (Tone-5 + AA -> AA + Tone-5)
            prev == 0x1086 && current == 0x1062 -> {
                ic.deleteSurroundingText(1, 0)
                "${0x1062.toChar()}${0x1086.toChar()}"
            }
            // Reorder ိူ (U + II -> II + U)
            prev == 0x1030 && current == 0x102D -> {
                ic.deleteSurroundingText(1, 0)
                "${0x102D.toChar()}${0x1030.toChar()}"
            }

            else -> null
        }
    }

    private fun handleVisualOrdering(prev: Int, current: Int): String {
        val currentChar = current.toChar()

        // သင်ၼိပ်ႉ "ေ" ဢွၼ်တၢင်း (Start of leading vowel logic)
        if (current == MY_E || current == SH_E) {
            isConsonantSwapped = false
            isMedialSwapped = false
            return "$ZWSP$currentChar"
        }

        // သင်တူဝ်ၽၢႆႇၼႃႈပဵၼ် "ေ"
        if (prev == MY_E || prev == SH_E) {
            when {
                isShanConsonant(current) -> {
                    if (!isConsonantSwapped) {
                        ic.deleteSurroundingText(2, 0) // Delete ZWSP + E
                        isConsonantSwapped = true
                        return "$currentChar${prev.toChar()}"
                    }
                }

                isShanMedial(current) -> {
                    if (isConsonantSwapped && !isMedialSwapped) {
                        ic.deleteSurroundingText(1, 0) // Delete E
                        isMedialSwapped = true
                        return "$currentChar${prev.toChar()}"
                    }
                }
            }
        }

        // Default state reset
        if (isOthers(current)) {
            isConsonantSwapped = false
            isMedialSwapped = false
        }

        return currentChar.toString()
    }

    private fun isShanConsonant(code: Int) = code in 0x1000..0x1021 || code in 0xAA60..0xAA7A
    private fun isShanMedial(code: Int) = code == 0x103B || code == 0x103C
    private fun isOthers(code: Int) = code in listOf(0x1086, 0x1087, 0x1088, 0x1089, 0x108A, 0x1062)

    fun handleShanDelete() {

        // 1. ထတ်းတူၺ်းတူဝ်လိၵ်ႈ 2 တူဝ် ၽၢႆႇၼႃႈ Cursor
        val before = ic.getTextBeforeCursor(2, 0) ?: ""

        if (before.length >= 2) {
            val firstChar = before[0] // တူဝ်ထႅၼ်ႈ 2 (secPrev)
            val secondChar = before[1] // တူဝ်ထႅၼ်ႈ 1 (charBeforeCursor)

            when {
                // Case A: သင်ပဵၼ် [ZWSP + ေ] -> လူတ်းပႅတ်ႈတင်းသွင်တူဝ်
                secondChar.code == 0x1031 && firstChar.code == 8203 -> {
                    ic.deleteSurroundingText(2, 0)
                    resetReorderFlags()
                }
                // Case B: သင်ပဵၼ် [ၵ + ေ] (Swap ယဝ်ႉ) -> လူတ်းပႅတ်ႈတင်းသွင်တူဝ်
                // ၼင်ႇႁိုဝ်ၵူၼ်းၸႂ်ႉတေလႅၵ်ႈတူဝ် Consonant မႂ်ႇလႆႈငၢႆႈ
                (secondChar.code == 0x1031) && isShanConsonant(firstChar.code) -> {
                    ic.deleteSurroundingText(2, 0)
                    // လူတ်းယဝ်ႉ ၸၢင်ႈထႅမ်သႂ်ႇ ZWSP + ေ ၶိုၼ်း ၼင်ႇႁိုဝ်တေတႅမ်ႈ Consonant မႂ်ႇလႆႈၵမ်းလဵဝ်
                    ic.commitText("\u200B\u1031", 1)
                    resetReorderFlags()
                }

                else -> {
                    ic.deleteSurroundingText(1, 0)
                }
            }
        } else {
            // Default Delete
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun resetReorderFlags() {
        isConsonantSwapped = false
        isMedialSwapped = false
    }


    fun isInvalidSequence(prev: Int, current: Int): Boolean {
        val prevChar = prev.toChar()
        val currChar = current.toChar()

        return when {
            // Rule 1: ယႃႇႁႂ်ႈၼိပ်ႉ ဝၼ်ၵျုၵ်ႉ မိူၼ်ၵၼ် သွင်ပွၵ်ႈ (No double tones)
            prevChar == currChar && (ShanScript.TONES.contains(currChar)) -> true

            // Rule 2: သင်မီး ဝၼ်ၵျုၵ်ႉဝႆႉယဝ်ႉ ယႃႇႁႂ်ႈသႂ်ႇ ဝၼ်ၵျုၵ်ႉထႅင်ႈ (No stacking tones)
            ShanScript.TONES.contains(prevChar) && ShanScript.TONES.contains(currChar) -> true

            // Rule 3: သင်မီး တၢင်ႇလၢႆ/တိုတ်း ဝႆႉယဝ်ႉ ယႃႇႁႂ်ႈသႂ်ႇ တၢင်ႇလၢႆ/တိုတ်း ထႅင်ႈ
            ShanScript.UPPER_VOWELS.contains(prevChar) && ShanScript.UPPER_VOWELS.contains(currChar) -> true

            else -> false
        }
    }

    fun convertZawgyi() {

        ic.performContextMenuAction(android.R.id.selectAll)
        val charSequence = ic.getSelectedText(0)
        val convertedText: String?
        val selectedText: String?
        if (!TextUtils.isEmpty(charSequence)) {
            selectedText = charSequence.toString()
            // FOR SHAN CONVERTER
            if (ShanLanguageDetector.isShanZawgyi(selectedText)) {
                convertedText = ShanZawgyiConverter.zg2uni(selectedText)
            } else {
                convertedText = ShanZawgyiConverter.uni2zg(selectedText)
            }

            ic.commitText(convertedText, 1)
        }
    }

    fun insertWithZWSP(text: String) {
        val charBefore = ic.getTextBeforeCursor(1, 0)?.toString() ?: ""
        val asat = "\u103A" // ်

        // 1. သင်ၼိပ်ႉ ASAT (်) -> လူဝ်ႇၵႂႃႇလၢင်ႉ ZWSP ဢၼ်ၸၢင်ႈမီးၽၢႆႇၼႃႈ Consonant
        if (text == asat) {
            handleAsat(ic)
            return
        }

        // 2. Logic သႂ်ႇ ZWSP တွၼ်ႈတႃႇ Consonant ယူႇယူႇ
        if (ShanLanguageDetector.isShanUnicode(charBefore) && isShanConsonant(text[0].code)) {
            ic.commitText("\u200B$text", 1)
        } else {
            ic.commitText(text, 1)
        }
    }

    private fun handleAsat(ic: InputConnection) {
        val asat = "\u103A"
        // ထတ်းတူၺ်း 2 တူဝ်ၽၢႆႇၼႃႈ (မိူၼ်ၼင်ႇ \u200B + ၵ)
        val before = ic.getTextBeforeCursor(2, 0)?.toString() ?: ""

        if (before.length == 2 && before[0] == '\u200B') {
            // သင်မီး ZWSP ဝႆႉၽၢႆႇၼႃႈ Consonant တႄႉတႄႉ
            val consonant = before[1].toString()

            // လူတ်းပႅတ်ႈတင်းသွင်တူဝ် (ZWSP + Consonant)
            ic.deleteSurroundingText(2, 0)

            // သူင်ႇ Consonant ၶိုၼ်း (ဢမ်ႇပႃး ZWSP) သေၸင်ႇသႂ်ႇ ASAT
            ic.commitText(consonant + asat, 1)
        } else {
            // သင်ဢမ်ႇမီး ZWSP ဝႆႉၵေႃႈ သူင်ႇ ASAT ၵႂႃႇယူႇယူႇ
            ic.commitText(asat, 1)
        }
    }

}