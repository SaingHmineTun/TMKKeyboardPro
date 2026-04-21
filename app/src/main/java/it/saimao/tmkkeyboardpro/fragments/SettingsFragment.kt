package it.saimao.tmkkeyboardpro.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.activities_services.ChooseThemeActivity
import it.saimao.tmkkeyboardpro.activities_services.KeyboardLanguagesActivity
import it.saimao.tmkkeyboardpro.activities_services.MainActivity
import it.saimao.tmkkeyboardpro.databinding.FragmentSettingsBinding
import it.saimao.tmkkeyboardpro.logic.FontManager
import it.saimao.tmkkeyboardpro.logic.ThemeManager
import it.saimao.tmkkeyboardpro.logic.ThemeManager.themes
import it.saimao.tmkkeyboardpro.utils.enableSuggestion

import it.saimao.tmkkeyboardpro.utils.getAppLanguage
import it.saimao.tmkkeyboardpro.utils.getHandWritingSystem
import it.saimao.tmkkeyboardpro.utils.getKeyboardTheme
import it.saimao.tmkkeyboardpro.utils.getSoundOnKeyPress
import it.saimao.tmkkeyboardpro.utils.getVibrateOnKeyPress
import it.saimao.tmkkeyboardpro.utils.isEnabledSuggestion

import it.saimao.tmkkeyboardpro.utils.saveAppLanguage
import it.saimao.tmkkeyboardpro.utils.saveHandWritingSystem
import it.saimao.tmkkeyboardpro.utils.saveSoundOnKeyPress
import it.saimao.tmkkeyboardpro.utils.saveVibrateOnKeyPress
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

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
            saveVibrateOnKeyPress(requireContext(), isChecked)
        }

        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            saveSoundOnKeyPress(requireContext(), isChecked)
        }

        binding.switchHandWriting.setOnCheckedChangeListener { _, isChecked ->
            saveHandWritingSystem(requireContext(), isChecked)
        }

        binding.switchSuggestions.setOnCheckedChangeListener { _, isChecked ->
            enableSuggestion(requireContext(), isChecked)
        }


        binding.btnKeyboardLanguages.setOnClickListener {
            val intent = Intent(requireContext(), KeyboardLanguagesActivity::class.java)
            startActivity(intent)
        }

        binding.btnSelectTheme.setOnClickListener {
            val intent = Intent(requireContext(), ChooseThemeActivity::class.java)
            startActivity(intent)

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
        val currentTheme = getKeyboardTheme(requireContext())
        binding.tvCurrentTheme.text = currentTheme

        // လူတ်ႇၽႃႇသႃႇယၢမ်းလဵဝ်
        val currentLang = getAppLanguage(requireContext())
        binding.tvCurrentLang.text = when (currentLang) {
            "MY" -> "Myanmar (ဗမာ)"
            "EN" -> "English"
            else -> "Shan (တႆး)"
        }


        // လူတ်ႇ State ၶွင် Switch
        binding.switchVibration.isChecked = getVibrateOnKeyPress(requireContext())
        binding.switchSound.isChecked = getSoundOnKeyPress(requireContext())
        binding.switchHandWriting.isChecked = getHandWritingSystem(requireContext())
        binding.switchSuggestions.isChecked = isEnabledSuggestion(requireContext())
    }

    private fun showThemeSelector() {
        // 1. ႁူတ်းဢဝ် Keys (Theme Names) မႃးပဵၼ် Array
        val themes = themes.keys.toList()
        val themesArray = themes.toTypedArray() // လႅၵ်ႈပဵၼ် Array<String>

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Keyboard Theme")
            // 2. သူင်ႇ themesArray ၶဝ်ႈၵႂႃႇ (မၼ်းတေႁပ်ႉ CharSequenceArray)
            .setSingleChoiceItems(
                themesArray,
                ThemeManager.getCheckedThemeIndex(requireContext())
            ) { dialog, which ->
                // 3. ဢဝ် Theme Name ဢၼ်လိူၵ်ႈၼၼ်ႉၵႂႃႇ Save
                val selectedTheme = themes[which]
                ThemeManager.saveTheme(requireContext(), selectedTheme)

                // Update UI
                binding.tvCurrentTheme.text = selectedTheme

                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
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
//                prefs.edit { putString("app_language", selectedTag) }
                saveAppLanguage(requireContext(), selectedTag)

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