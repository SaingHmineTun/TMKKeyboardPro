package it.saimao.tmkkeyboardpro.logic

import android.content.Context
import it.saimao.tmkkeyboardpro.utils.getKeyboardTheme
import it.saimao.tmkkeyboardpro.utils.saveKeyboardTheme

object ThemeManager {

    val themes = mapOf(
        "GOLD" to "Gold (TMK)",
        "DARK" to "Dark Knight",
        "BLUE" to "Ocean Blue",
        "WHITE" to "Pure White"
    )


    fun getCheckedThemeIndex(context: Context): Int {
        val currentTheme = getKeyboardTheme(context)
        return themes.keys.toList().indexOf(currentTheme)
    }

    fun saveTheme(context: Context, selectedTheme: String) {
        saveKeyboardTheme(context, selectedTheme)
    }

    fun getTheme(context: Context): String {
        return getKeyboardTheme(context)
    }

}
