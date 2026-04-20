package it.saimao.tmkkeyboardpro.logic

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.android.material.internal.FlowLayout
import com.google.android.material.tabs.TabLayout
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.utils.getKeyboardTheme
import it.saimao.tmkkeyboardpro.utils.saveKeyboardTheme
import androidx.core.graphics.toColorInt
import it.saimao.tmkkeyboardpro.utils.getCustomTheme
import it.saimao.tmkkeyboardpro.utils.saveCustomTheme
import androidx.core.net.toUri

data class KeyboardTheme(
    val bg: String,        // Background
    val key: String,       // Normal Key
    val pressed: String,   // Pressed Key
    val txt: String,       // Text Color
    val special: String    // Shift, Del, Enter
)

object ThemeManager {
    val themes = mapOf(
        // 1. DARK (OLED Deep Black) - Special Keys ၸႂ်ႉသီလမ်ဢွၼ်ႇ
        "DARK" to KeyboardTheme("#000000", "#1A1A1A", "#333333", "#E0E0E0", "#0D0D0D"),

        // 2. GOLD (Luxury Black & Metallic Gold) - Special Keys ၸႂ်ႉသီထဝ်ႇလႅတ်း
        "GOLD" to KeyboardTheme("#121212", "#D4AF37", "#AA8831", "#FFFFFF", "#2A2A2A"),

        // 3. BLUE (Deep Navy & Cyan Accent) - Special Keys ၸႂ်ႉသီၵမ်ႇ
        "BLUE" to KeyboardTheme("#0B132B", "#1C2541", "#3A506B", "#FFFFFF", "#080E1E"),

        // 4. PURPLE (Cyberpunk Neon) - Special Keys ၸႂ်ႉသီၵေႃႉၵႄႈ
        "PURPLE" to KeyboardTheme("#10002B", "#3C096C", "#5A189A", "#FFFFFF", "#240046"),

        // 5. GREEN (Forest Emerald) - Special Keys ၸႂ်ႉသီၶဵဝ်ၵမ်ႇ
        "GREEN" to KeyboardTheme("#081C15", "#1B4332", "#2D6A4F", "#D8F3DC", "#040E0B"),

        // 6. ROSE (Midnight Wine) - Special Keys ၸႂ်ႉသီလႅင်ၵမ်ႇ
        "ROSE" to KeyboardTheme("#1A0501", "#4B1208", "#801100", "#FAD2E1", "#0F0301"),

        // 7. OCEAN (Sea Blue & White) - Special Keys ၸႂ်ႉသီသွမ်ႇလႅတ်း
        "OCEAN" to KeyboardTheme("#CAF0F8", "#90E0EF", "#00B4D8", "#03045E", "#ADE8F4"),

        // 8. GUM (Sweet Strawberry) - Special Keys ၸႂ်ႉသီပူဝ်ႇလႅတ်း
        "GUM" to KeyboardTheme("#FFF0F3", "#FFB7C5", "#FF85A1", "#590D22", "#FFCCD5"),

        // 9. SILVER (Minimalist Apple Style) - Special Keys ၸႂ်ႉသီထဝ်ႇလႅင်း
        "SILVER" to KeyboardTheme("#F8F9FA", "#FFFFFF", "#E9ECEF", "#212529", "#DEE2E6"),

        // 10. MODERN (Material You - Slate) - Special Keys ၸႂ်ႉသီထဝ်ႇၵမ်ႇ
        "MODERN" to KeyboardTheme("#212529", "#343A40", "#495057", "#F8F9FA", "#1A1D20")
    )

    fun getCheckedThemeIndex(context: Context): Int =
        themes.keys.toList().indexOf(getKeyboardTheme(context))

    fun saveTheme(context: Context, selectedTheme: String) =
        saveKeyboardTheme(context, selectedTheme)

    fun getTheme(context: Context): String = getKeyboardTheme(context)

    /**
     * Method လူင်တွၼ်ႈတႃႇ Apply Theme တင်း Layout (Recursive)
     */
    @SuppressLint("RestrictedApi")
    fun applyTheme(context: Context, view: View) {
        val getTheme: String = getKeyboardTheme(context)
        val theme: KeyboardTheme = if (getTheme == "custom_theme") {
            getCustomKeyboardTheme(context)
        } else {
            themes[getTheme(context)] ?: themes["DARK"]!!
        }
        val typeface = FontManager.getActiveTypeface(context)

        // 1. Apply ၸူးတူဝ် View မၼ်းႁင်းၵူၺ်းဢွၼ်တၢင်း
        applyToSingleView(view, theme, typeface)

        // 2. သင်ပဵၼ် ViewGroup ႁႂ်ႈ Loop ၸူးတူဝ်လုၵ်ႈမၼ်းတင်းမူတ်း
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                applyTheme(context, child) // Recursive call
            }
        }
    }

    /**
     * Helper Method တွၼ်ႈတႃႇ Apply Theme ၸူး View ၼိုင်ႈဢၼ် (Reduce Redundancy)
     */
    @SuppressLint("RestrictedApi")
    fun applyToSingleView(
        view: View,
        theme: KeyboardTheme,
        typeface: android.graphics.Typeface?
    ) {
        val textColor = theme.txt.toColorInt()

        when (view) {
            // Check Background Containers
            is ViewGroup -> {
                val themeBg = theme.bg
                if (view.id == R.id.keys_container || view.id == R.id.keyboard_preview_container) {
                    if (themeBg.startsWith("#")) {
                        view.setBackgroundColor(themeBg.toColorInt())
                    } else {

                        try {
                            val uri = themeBg.toUri()
                            val inputStream = view.context.contentResolver.openInputStream(uri)
                            val drawable = Drawable.createFromStream(inputStream, themeBg)
                            view.background = drawable
                        } catch (e: Exception) {
                            // သင်ပိုတ်ႇႁၢင်ႈဢမ်ႇလႆႈ ႁႂ်ႈသႂ်ႇသီလမ်ဝႆႉပဵၼ် Default
                            view.setBackgroundColor(Color.BLACK)
                        }
                    }
                } else if (view is CardView) {
                    val specialColor = theme.special.toColorInt()
                    val cardBgColor = Color.rgb(
                        Color.red(specialColor.red),
                        Color.green(specialColor.green),
                        Color.blue(specialColor.blue)
                    )
                    view.setCardBackgroundColor(cardBgColor)
                } else if (view is TabLayout) {
//                    view.setBackgroundColor(theme.bg.toColorInt())
                    view.setTabTextColors(textColor, textColor)
                    view.setSelectedTabIndicatorColor(textColor)
                    view.tabIconTint = ColorStateList.valueOf(textColor)
                }
            }

            // Check Buttons
            is Button -> {
                val isSpecial = isSpecialKey(view.id)
                val normalColor = (if (isSpecial) theme.special else theme.key).toColorInt()
                val pressedColor = theme.pressed.toColorInt()

                val states = arrayOf(intArrayOf(android.R.attr.state_pressed), intArrayOf())
                val colors = intArrayOf(pressedColor, normalColor)

                view.backgroundTintList = ColorStateList(states, colors)
                view.backgroundTintMode = PorterDuff.Mode.SRC_IN
                view.foregroundTintList = ColorStateList.valueOf(textColor) // For Icons
                view.setTextColor(textColor)
                if (typeface != null) view.typeface = typeface
            }

            // Check TextPreviews or Labels
            is TextView -> {
                view.setTextColor(textColor)
                if (typeface != null) view.typeface = typeface
            }
        }
    }

    // ၸဝ်ႈၵဝ်ႇၸၢင်ႈ Public Method ၼႆႉဝႆႉ သင်လူဝ်ႇ Apply ၵွၺ်း 1 View
    fun applySingleViewTheme(context: Context, view: View) {
        val theme = themes[getTheme(context)] ?: themes["DARK"]!!
        val typeface = FontManager.getActiveTypeface(context)
        applyToSingleView(view, theme, typeface)
    }

    private fun isSpecialKey(id: Int): Boolean {
        return id == R.id.key_del || id == R.id.key_shift || id == R.id.key_unshift || id == R.id.key_enter || id == R.id.key_lang || id == R.id.key_123 || id == R.id.key_emoji || id == R.id.key_speech || id == R.id.key_dot || id == R.id.key_switch_abc || id == R.id.key_1_2 || id == R.id.key_2_1 || id == R.id.key_comma || id == R.id.key_period || id == R.id.key_convert || id == R.id.key_back_to_kb || id == R.id.key_emoji_space
    }

    fun getCustomKeyboardTheme(requireContext: Context): KeyboardTheme =
        getCustomTheme(requireContext)

    fun saveCustomKeyboardTheme(requireContext: Context, theme: KeyboardTheme) {
        saveCustomTheme(requireContext, theme)
    }
}