package it.saimao.tmkkeyboardpro

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.saimao.tmkkeyboardpro.databinding.FragmentSettingsBinding
import androidx.core.content.edit

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
    }

    private fun updateUI() {
        // လူတ်ႇသီ Theme ယၢမ်းလဵဝ်
        val currentTheme = prefs.getString("keyboard_theme", "GOLD")
        binding.tvCurrentTheme.text = when(currentTheme) {
            "DARK" -> "Dark Knight"
            "BLUE" -> "Ocean Blue"
            "WHITE" -> "Pure White"
            else -> "Gold (TMK)"
        }

        // လူတ်ႇၽႃႇသႃႇယၢမ်းလဵဝ်
        val currentLang = prefs.getString("default_language", "SHN")
        binding.tvCurrentLang.text = when(currentLang) {
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

    private fun showLanguageSelector() {
        val langs = arrayOf("Shan (တႆး)", "Myanmar (ဗမာ)", "English")
        val langValues = arrayOf("SHN", "MY", "EN")

        val currentLang = prefs.getString("default_language", "SHN")
        val checkedItem = langValues.indexOf(currentLang)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Default Language")
            .setSingleChoiceItems(langs, checkedItem) { dialog, which ->
                prefs.edit { putString("default_language", langValues[which]) }
                binding.tvCurrentLang.text = langs[which]
                dialog.dismiss()
            }
            .show()
    }
}