package it.saimao.tmkkeyboardpro.activities_services

import ThemeAdapter
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.fragments.CustomThemeFragment
import it.saimao.tmkkeyboardpro.fragments.PredefinedThemeFragment
import it.saimao.tmkkeyboardpro.logic.KeyboardTheme
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
        setupViewPager2()

    }

    private fun setupViewPager2() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        // 1. Setup ViewPager & TabLayout မိူၼ်ၵဝ်ႇ
        val adapter = ChooseThemePagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "Predefined" else "Custom"
        }.attach()

        // 2. ၸႅတ်ႈတူၺ်းဝႃႈ Current Theme ပဵၼ် Custom ႁႃႉ?
        val currentTheme = ThemeManager.getTheme(this)
        if (currentTheme == "custom_theme") {
            // ၼဵၵ်းလိူၵ်ႈ Tab ဢၼ်မီး Position 1 (Custom Fragment) ၵမ်းလဵဝ်
            viewPager.setCurrentItem(1, false) // false = ဢမ်ႇလူဝ်ႇ Smooth Scroll မိူဝ်ႈတႄႇပိုတ်ႇ
        }
    }


    fun updatePreview() {
        // ၸႂ်ႉ ThemeManager ဢၼ် Optimize ဝႆႉၼၼ်ႉ တွၼ်ႈတႃႇ Apply သီ
        ThemeManager.applyTheme(this, previewContainer)

        // Apply ၸူး Background ၶွင်ၼႃႈၼႆႉၸွမ်း ၼင်ႇႁိုဝ်တေတူၺ်းသၢင်ႇထုၵ်ႇ
        ThemeManager.applySingleViewTheme(this, findViewById(R.id.main_layout))
    }


    inner class ChooseThemePagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2 // 2 Fragments

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PredefinedThemeFragment() // ဢၼ်ပႃး RecyclerView တူဝ်ၵဝ်ႇ
                1 -> {

                    ThemeManager.saveTheme(this@ChooseThemeActivity, "custom_theme")
                    updatePreview()
                    CustomThemeFragment()
                }     // ဢၼ်ပႃး Options မႄးသီႁင်းၵူၺ်း
                else -> PredefinedThemeFragment()
            }
        }
    }

}