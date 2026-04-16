package it.saimao.tmkkeyboardpro.utils

fun getPopupCharsFor(language: String, baseChar: String): List<String> {
    if (language == "SHN") {
        return when (baseChar) {
            "ၵ" -> listOf("ၷ")
            "ၶ" -> listOf("ꧠ")
            "တ" -> listOf("ၻ", "ꩦ", "ꩨ")
            "ထ" -> listOf("ꩪ", "ꩧ", "ꩩ")
            "ပ" -> listOf("ၿ")
            "သ" -> listOf("ႀ")
            "ေ" -> listOf("ဵ")
            "ႄ" -> listOf("ႅ")
            "ိ" -> listOf("ီ")
            "ု" -> listOf("ို")
            "ူ" -> listOf("ိူ")
            "်" -> listOf("ႂ်")
            "ၼ" -> listOf("ꧣ")
            "ၽ" -> listOf("ၾ", "ꧤ")
            "လ" -> listOf("ꩮ")
            "ၢ" -> listOf("ႃ")
            "ၸ" -> listOf("ꩡ", "ၹ")
            "႑" -> listOf("1", "၁", "¹")
            "႒" -> listOf("2", "၂", "²")
            "႓" -> listOf("3", "၃", "³")
            "႔" -> listOf("4", "၄", "⁴")
            "႕" -> listOf("5", "၅", "⁵")
            "႖" -> listOf("6", "၆", "⁶")
            "႗" -> listOf("7", "၇", "⁷")
            "႘" -> listOf("8", "၈", "⁸")
            "႙" -> listOf("9", "၉", "⁹")
            "႐" -> listOf("0", "၀", "⁰")
            "။" -> listOf("{", "}", "(", ")", "\"", "\'", ":", ";", "#", "!", "?", "။")
            else -> emptyList()
        }
    }
    return emptyList()
}