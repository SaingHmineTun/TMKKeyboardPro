package it.saimao.tmkkeyboardpro.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private fun getSharedPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences("TMK_PREFS", Context.MODE_PRIVATE)
}

fun getSavedTheme(context: Context): String {

    val prefs = getSharedPrefs(context)
    return prefs.getString("keyboard_theme", "BLUE") ?: "BLUE"
}

fun saveFont(context: Context, fontName: String) {
    val prefs = getSharedPrefs(context)
    prefs.edit {
        putString("active_font", fontName)
    }
}

fun getSavedFont(context: Context): String? {
    val prefs = getSharedPrefs(context)
    return prefs.getString("active_font", null)
}