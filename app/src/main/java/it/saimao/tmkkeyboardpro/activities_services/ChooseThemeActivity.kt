package it.saimao.tmkkeyboardpro.activities_services

import ThemeAdapter
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.logic.ThemeManager

class ChooseThemeActivity : AppCompatActivity() {

    private lateinit var previewContainer: FrameLayout
    private lateinit var keyboardPreview: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_theme)

        // 1. Setup Toolbar
        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 2. Setup Keyboard Preview (English Layout)
        previewContainer = findViewById(R.id.keyboard_preview_container)
        keyboardPreview = layoutInflater.inflate(R.layout.layout_en_normal, previewContainer, false)
        previewContainer.addView(keyboardPreview)

        // 3. Initialize Preview with current theme
        updatePreview()

        // 4. Setup Themes List
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val rvThemes = findViewById<RecyclerView>(R.id.rv_themes)
        val themeNames = ThemeManager.themes.keys.toList()

        rvThemes.layoutManager = GridLayoutManager(this, 2) // ၼႄ 2 ၶွၼ် (Columns)
        rvThemes.adapter = ThemeAdapter(themeNames) { selectedTheme ->
            // မိူဝ်ႈ User တိၵ်းလိူၵ်ႈသီ
            ThemeManager.saveTheme(this, selectedTheme)
            updatePreview()
        }
    }

    private fun updatePreview() {
        // ၸႂ်ႉ ThemeManager ဢၼ် Optimize ဝႆႉၼၼ်ႉ တွၼ်ႈတႃႇ Apply သီ
        ThemeManager.applyTheme(this, keyboardPreview)

        // Apply ၸူး Background ၶွင်ၼႃႈၼႆႉၸွမ်း ၼင်ႇႁိုဝ်တေတူၺ်းသၢင်ႇထုၵ်ႇ
        ThemeManager.applySingleViewTheme(this, findViewById(R.id.main_layout))
    }
}