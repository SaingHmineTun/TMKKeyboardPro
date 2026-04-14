package it.saimao.tmkkeyboardpro

fun getPopupCharsFor(baseChar: String): List<String> {
    return when (baseChar) {
        "ၵ" -> listOf("ၷ")
        "တ" -> listOf("ၻ")
        "ပ" -> listOf("ၿ")
        "ၸ" -> listOf("ꩡ", "ၹ")
        "1" -> listOf("႑", "¹")
        "2" -> listOf("႒", "²")
        "3" -> listOf("႔", "³")
        "4" -> listOf("႔", "⁴")
        "5" -> listOf("႕", "⁵")
        "6" -> listOf("႖", "⁶")
        "7" -> listOf("႗", "⁷")
        "8" -> listOf("႘", "⁸")
        "9" -> listOf("႙", "⁹")
        "0" -> listOf("႐", "⁰")
        else -> emptyList()
    }
}