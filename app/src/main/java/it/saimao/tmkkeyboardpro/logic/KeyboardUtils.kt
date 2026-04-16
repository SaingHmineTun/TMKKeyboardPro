package it.saimao.tmkkeyboardpro.logic

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {
    // ၸႅတ်ႈတူၺ်းဝႃႈ Keyboard ႁဝ်းထုၵ်ႇပိုတ်ႇဝႆႉၼႂ်း System ယဝ်ႉႁႃႉ?
    fun isKeyboardEnabled(context: Context): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.enabledInputMethodList.any { it.packageName == context.packageName }
    }

    // ၸႅတ်ႈတူၺ်းဝႃႈ ယၢမ်းလဵဝ်ၸႂ်ႉ Keyboard ႁဝ်းဝႆႉယူႇႁႃႉ?
    fun isKeyboardSelected(context: Context): Boolean {
        val currentId = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        return currentId?.contains(context.packageName) == true
    }
}