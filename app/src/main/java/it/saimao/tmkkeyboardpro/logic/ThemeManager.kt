package it.saimao.tmkkeyboardpro.logic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.internal.FlowLayout
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.utils.getKeyboardTheme
import it.saimao.tmkkeyboardpro.utils.saveKeyboardTheme

data class KeyboardTheme(
    val bg: String,        // Background
    val key: String,       // Normal Key
    val pressed: String,   // Pressed Key
    val txt: String,       // Text Color
    val special: String    // Shift, Del, Enter
)

object ThemeManager {
    val themes = mapOf(
        "GOLD" to KeyboardTheme("#1A1A1A", "#D4AF37", "#B8860B", "#FFFFFF", "#333333"),
        "DARK" to KeyboardTheme("#000000", "#1E1E1E", "#333333", "#FFFFFF", "#121212"),
        "BLUE" to KeyboardTheme("#0D1B2A", "#1B263B", "#415A77", "#E0E1DD", "#003566"),
        "PURPLE" to KeyboardTheme("#240046", "#3C096C", "#5A189A", "#FFFFFF", "#10002B"),
        "GREEN" to KeyboardTheme("#081C15", "#1B4332", "#2D6A4F", "#D8F3DC", "#000000"),
        "ROSE" to KeyboardTheme("#250902", "#4B1208", "#801100", "#FAD2E1", "#000000"),
        "OCEAN" to KeyboardTheme("#0077B6", "#0096C7", "#48CAE4", "#FFFFFF", "#023E8A"),
        "GUM" to KeyboardTheme("#FF85A1", "#FF91AF", "#FFAFCC", "#FFFFFF", "#FF5C8A"),
        "SILVER" to KeyboardTheme("#E5E5E5", "#FFFFFF", "#CCCCCC", "#000000", "#D4D4D4"),
        "MODERN" to KeyboardTheme("#212529", "#343A40", "#495057", "#F8F9FA", "#000000")
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


    //    @SuppressLint("RestrictedApi")
//    fun applyTheme(context: Context, view: View) {
//        val keyNormalColor: Int
//        val keyPressedColor: Int
//        val backgroundColor: Int
//        val themeType = getTheme(context)
//
//        if (themeType == "GOLD") {
//            keyNormalColor = context.getColor(R.color.gold_key_normal)
//            keyPressedColor = context.getColor(R.color.gold_key_pressed)
//            backgroundColor = context.getColor(R.color.gold_background)
//        } else {
//            keyNormalColor = context.getColor(R.color.blue_key_normal)
//            keyPressedColor = context.getColor(R.color.blue_key_pressed)
//            backgroundColor = context.getColor(R.color.blue_background)
//        }
//
//        // 1. သင်ပဵၼ် Root View ႁႂ်ႈလႅၵ်ႈသီ Background
//        if (view.id == R.id.keyboard_root || view is FlowLayout) {
//            view.setBackgroundColor(backgroundColor)
//        }
//
//
//        val typeface = FontManager.getActiveTypeface(context)
//
//        // 2. ၸႂ်ႉ Recursion တႃႇႁႃ Buttons ၼႂ်းၵူႈ Container
//        if (view is ViewGroup) {
//            for (i in 0 until view.childCount) {
//                val child = view.getChildAt(i)
//
//                if (child is Button) {
//                    // လႅၵ်ႈသီတုမ်ႇၼဵၵ်ႉ
//                    val states = arrayOf(
//                        intArrayOf(android.R.attr.state_pressed),
//                        intArrayOf()
//                    )
//                    val colors = intArrayOf(keyPressedColor, keyNormalColor)
//                    child.backgroundTintList = ColorStateList(states, colors)
//
//                    // လွင်ႈယႂ်ႇ: ႁႂ်ႈ Tint Mode မၼ်းပဵၼ် SRC_IN ၼင်ႇႁိုဝ်တေႁၼ်သီမႂ်ႇ
//                    child.backgroundTintMode = PorterDuff.Mode.SRC_IN
//                    if (typeface != null) {
//                        child.typeface = typeface
//                    }
//                } else if (child is ViewGroup) {
//                    // သင်ၺႃး FrameLayout ဢမ်ႇၼၼ် LinearLayout တၢင်ႇဢၼ် ႁႂ်ႈၶဝ်ႈၵႂႃႇႁႃထႅင်ႈ
//                    applyTheme(context,child)
//                }
//            }
//        }
//    }
    @SuppressLint("RestrictedApi")
    fun applyTheme(context: Context, view: View) {
        val themeName = getTheme(context)
        val theme = themes[themeName] ?: themes["GOLD"]!! // Default to GOLD

        val bgColor = Color.parseColor(theme.bg)
        val keyColor = Color.parseColor(theme.key)
        val pressedColor = Color.parseColor(theme.pressed)
        val textColor = Color.parseColor(theme.txt)
        val specialColor = Color.parseColor(theme.special)

        // 1. Set Background for Root/Container
        if (view.id == R.id.keyboard_root || view is FlowLayout) {
            view.setBackgroundColor(bgColor)
        }

        val typeface = FontManager.getActiveTypeface(context)

        // 2. Recursive Loop to apply to Buttons
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)

                if (child is Button) {
                    val isSpecial = isSpecialKey(child.id)
                    val normalColor = if (isSpecial) specialColor else keyColor

                    // ColorStateList for Normal/Pressed states
                    val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf())
                    val colors = intArrayOf(pressedColor, normalColor)

                    child.backgroundTintList = ColorStateList(states, colors)
                    child.backgroundTintMode = PorterDuff.Mode.SRC_IN
                    child.setTextColor(textColor)

                    if (typeface != null) child.typeface = typeface

                } else if (child is ViewGroup) {
                    applyTheme(context, child)
                }
            }
        }
    }

    // Helper တွၼ်ႈတႃႇၸႅၵ်ႇတုမ်ႇၼဵၵ်ႉ
    private fun isSpecialKey(id: Int): Boolean {
        return id == R.id.key_del || id == R.id.key_shift || id == R.id.key_unshift || id == R.id.key_enter ||
                id == R.id.key_lang || id == R.id.key_123 || id == R.id.key_emoji || id == R.id.key_speech ||
                id == R.id.key_dot
    }


}
