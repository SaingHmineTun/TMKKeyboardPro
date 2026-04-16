package it.saimao.tmkkeyboardpro.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.saimao.tmkkeyboardpro.activities_services.MainActivity
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.databinding.FragmentSettingsBinding
import it.saimao.tmkkeyboardpro.logic.FontManager

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val prefs by lazy {
        requireContext().getSharedPreferences("TMK_PREFS", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. လူတ်ႇ Data ၵဝ်ႇမႃးၼႄၼိူဝ် UI
        updateUI()

        // 2. Setup Listeners တွၼ်ႈတႃႇ Switches
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit { putBoolean("vibrate_on_keypress", isChecked) }
        }

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit { putBoolean("sound_on_keypress", isChecked) }
        }

        // 3. Setup Click Listeners တွၼ်ႈတႃႇ Dialogs
        binding.btnSelectTheme.setOnClickListener {
            showThemeSelector()
        }

        binding.btnSelectLanguage.setOnClickListener {
            showLanguageSelector()
        }

        binding.btnManageFonts.setOnClickListener {
            (requireActivity() as MainActivity).replaceFragment(FontFragment())
        }

        binding.btnFeedback.setOnClickListener {
            sendFeedback()
        }

        setupPreviewArea()

    }

    private fun setupPreviewArea() {
        // 1. ဢဝ် Font ဢၼ် Active ယူႇယၢမ်းလဵဝ်မႃးတမ်းပၼ်
        val typeface = FontManager.getActiveTypeface(requireContext())
        if (typeface != null) {
            binding.etPreview.typeface = typeface
        }

        // 2. ႁဵတ်းႁႂ်ႈ Keyboard ပိုတ်ႇမႃးၵမ်းလဵဝ် မိူဝ်ႉၼိပ်ႉၺႃး EditText
        binding.etPreview.requestFocus()
    }

    private fun updateUI() {
        // လူတ်ႇသီ Theme ယၢမ်းလဵဝ်
        val currentTheme = prefs.getString("keyboard_theme", "GOLD")
        binding.tvCurrentTheme.text = when (currentTheme) {
            "DARK" -> "Dark Knight"
            "BLUE" -> "Ocean Blue"
            "WHITE" -> "Pure White"
            else -> "Gold (TMK)"
        }

        // လူတ်ႇၽႃႇသႃႇယၢမ်းလဵဝ်
        val currentLang = prefs.getString("default_language", "SHN")
        binding.tvCurrentLang.text = when (currentLang) {
            "MY" -> "Myanmar (ဗမာ)"
            "EN" -> "English"
            else -> "Shan (တႆး)"
        }

        // လူတ်ႇ State ၶွင် Switch
        binding.switchVibration.isChecked = prefs.getBoolean("vibrate_on_keypress", true)
        binding.switchSound.isChecked = prefs.getBoolean("sound_on_keypress", false)
    }

    private fun showThemeSelector() {
        val themes = arrayOf("Gold (TMK)", "Dark Knight", "Ocean Blue", "Pure White")
        val themeValues = arrayOf("GOLD", "DARK", "BLUE", "WHITE")

        val currentTheme = prefs.getString("keyboard_theme", "GOLD")
        val checkedItem = themeValues.indexOf(currentTheme)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Keyboard Theme")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                prefs.edit { putString("keyboard_theme", themeValues[which]) }
                binding.tvCurrentTheme.text = themes[which]
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        // ၵူႈပွၵ်ႈဢၼ်ပွၵ်ႈမႃးၼႃႈၼႆႉ ႁႂ်ႈ Update Font ထႅင်ႈပွၵ်ႈၼိုင်ႈ
        setupPreviewArea()
        updateLanguageUI()
    }

    private fun showLanguageSelector() {
        val langs = arrayOf("English", "Shan (တႆး)", "Myanmar (ဗမာ)")
        val langTags = arrayOf("en", "shn", "my")

        // ဢၢၼ်ႇတူၺ်း Locale ယၢမ်းလဵဝ်
        val currentTag = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
        val checkedItem = langTags.indexOf(currentTag)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.select_language))
            .setSingleChoiceItems(langs, checkedItem) { dialog, which ->
                val selectedTag = langTags[which]

                // 1. Save သႂ်ႇ SharedPreferences (Optional)
                prefs.edit { putString("app_language", selectedTag) }

                // 2. *** Force Change App Language ***
                val appLocales: LocaleListCompat = LocaleListCompat.forLanguageTags(selectedTag)
                AppCompatDelegate.setApplicationLocales(appLocales)

                dialog.dismiss()
            }
            .show()
    }

    private fun updateLanguageUI() {
        val currentTag = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
        binding.tvCurrentLang.text = when (currentTag) {
            "shn" -> "Shan (တႆး)"
            "my" -> "Myanmar (ဗမာ)"
            else -> "English"
        }
    }

    private fun sendFeedback() {
        val deviceName = android.os.Build.MODEL
        val androidVersion = android.os.Build.VERSION.RELEASE
        val appVersion = getString(R.string.version)

        val info = "\n\n--- Device Info ---\n" +
                "Model: $deviceName\n" +
                "Android: $androidVersion\n" +
                "App: $appVersion"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:tmk.muse@gmail.com") // ဢီးမေးလ်ၸဝ်ႈၵဝ်ႇ
            putExtra(Intent.EXTRA_SUBJECT, "TMK Keyboard Feedback")
            putExtra(Intent.EXTRA_TEXT, "$info")
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No Email app found!", Toast.LENGTH_SHORT).show()
        }
    }

}