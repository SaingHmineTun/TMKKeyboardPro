package it.saimao.tmkkeyboardpro.utils

import android.content.Context
import android.preference.PreferenceManager

fun getSavedTheme(context: Context): String {

    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString("keyboard_theme", "BLUE") ?: "BLUE"
}