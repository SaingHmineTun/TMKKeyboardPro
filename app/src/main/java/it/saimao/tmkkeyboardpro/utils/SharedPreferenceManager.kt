package it.saimao.tmkkeyboardpro.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// 1. သႂ်ႇ Constants တွၼ်ႈတႃႇၸိုဝ်ႈ Prefs ၼင်ႇႁိုဝ်တေဢမ်ႇတႅမ်ႈၽိတ်း
private const val PREFS_NAME = "TMK_PREFS"

private fun getSharedPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

// 2. Save Method (ၸႂ်ႉ inline/reified ၵေႃႈလႆႈ သင်ၶႂ်ႈႁႂ်ႈမၼ်းမိူၼ်ၵၼ်)
private fun <T> save(context: Context, key: String, value: T) {
    getSharedPrefs(context).edit {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
        // KTX edit block တေ Auto-apply ပၼ်ၶႃႈ
    }
}

// 3. Retrieve Method (Fixed Null Safety)
fun <T> retrieve(context: Context, key: String, defaultValue: T): T {
    val prefs = getSharedPrefs(context)

    // ၸႂ်ႉ 'when' ၸႅတ်ႈတူၺ်း 'defaultValue' တႅၼ်း 'T::class'
    return when (defaultValue) {
        is String -> (prefs.getString(key, defaultValue) ?: defaultValue)
        is Int -> prefs.getInt(key, defaultValue)
        is Boolean -> prefs.getBoolean(key, defaultValue)
        is Long -> prefs.getLong(key, defaultValue)
        is Float -> prefs.getFloat(key, defaultValue)
        else -> throw IllegalArgumentException("Unsupported type: ${defaultValue?.let { it::class.java.name }}")
    } as T
}

fun getSavedTheme(context: Context): String {

    val prefs = getSharedPrefs(context)
    return prefs.getString("keyboard_theme", "BLUE") ?: "BLUE"
}

fun saveFont(context: Context, fontName: String) {
    save(context, "active_font", fontName)
}

fun getSavedFont(context: Context): String {
    return retrieve<String>(context, "active_font", "default")
}

fun saveVibrateOnKeyPress(context: Context, vibrate: Boolean) {
    save(context, "vibrate_on_keypress", vibrate)
}

fun getVibrateOnKeyPress(context: Context): Boolean {
    return retrieve(context, "vibrate_on_keypress", false)
}

fun saveSoundOnKeyPress(context: Context, sound: Boolean) {
    save(context, "sound_on_keypress", sound)
}

fun getSoundOnKeyPress(context: Context): Boolean {
    return retrieve(context, "sound_on_keypress", false)
}

fun saveAppLanguage(context: Context, language: String) {
    save(context, "app_language", language)
}

fun getAppLanguage(context: Context): String {
    return retrieve(context, "app_language", "SHN")
}

fun saveKeyboardTheme(context: Context, theme: String) {
    save(context, "keyboard_theme", theme)
}

fun getKeyboardTheme(context: Context): String {
    return retrieve<String>(context, "keyboard_theme", "GOLD")
}