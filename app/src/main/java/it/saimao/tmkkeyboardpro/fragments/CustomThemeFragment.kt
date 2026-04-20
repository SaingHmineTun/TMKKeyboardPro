package it.saimao.tmkkeyboardpro.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import it.saimao.tmkkeyboardpro.activities_services.ChooseThemeActivity
import it.saimao.tmkkeyboardpro.databinding.FragmentCustomThemeBinding
import it.saimao.tmkkeyboardpro.logic.KeyboardTheme
import it.saimao.tmkkeyboardpro.logic.ThemeManager
import androidx.core.graphics.toColorInt
import yuku.ambilwarna.AmbilWarnaDialog

class CustomThemeFragment : Fragment() {

    private lateinit var binding: FragmentCustomThemeBinding

    private lateinit var currentCustomTheme: KeyboardTheme

    // 1. သၢင်ႈ File Picker Launcher
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // လႅၵ်ႈ URI ပဵၼ် String သေသိမ်း (လူဝ်ႇ Take Persistable Permission သင်ပဵၼ် Service)
                currentCustomTheme = currentCustomTheme.copy(bg = it.toString())
                saveAndRefreshPreview()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomThemeBinding.inflate(inflater, container, false)
        currentCustomTheme = ThemeManager.getCustomKeyboardTheme(requireContext())
        setupClickListeners()
        initSelectedThemeColor()
        return binding.root
    }

    private fun initSelectedThemeColor() {

        binding.previewKeyBg.background.setTint(currentCustomTheme.key.toColorInt())
        binding.previewKeyText.background.setTint(currentCustomTheme.txt.toColorInt())
        binding.previewPressed.background.setTint(currentCustomTheme.pressed.toColorInt())
        binding.previewSpecialBg.background.setTint(currentCustomTheme.special.toColorInt())
    }

    private fun setupClickListeners() {
        binding.btnBgImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnBgColor.setOnClickListener {
            openColorPicker(initialColor = currentCustomTheme.bg) { color ->
                currentCustomTheme = currentCustomTheme.copy(bg = color)
                saveAndRefreshPreview()
            }
        }

        // 3. လိူၵ်ႈသီ Key Background
        binding.btnKeyBgColor.setOnClickListener {
            openColorPicker(initialColor = currentCustomTheme.key) { color ->
                currentCustomTheme = currentCustomTheme.copy(key = color)
                saveAndRefreshPreview()
                binding.previewKeyBg.background.setTint(color.toColorInt())
            }
        }

        // 4. လိူၵ်ႈသီ Text
        binding.btnKeyTextColor.setOnClickListener {
            openColorPicker(initialColor = currentCustomTheme.txt) { color ->
                currentCustomTheme = currentCustomTheme.copy(txt = color)
                saveAndRefreshPreview()
                binding.previewKeyText.background.setTint(color.toColorInt())
            }
        }

        binding.btnPressedColor.setOnClickListener {
            openColorPicker(initialColor = currentCustomTheme.pressed) { color ->
                currentCustomTheme = currentCustomTheme.copy(pressed = color)
                saveAndRefreshPreview()
                binding.previewPressed.background.setTint(color.toColorInt())
            }
        }

        binding.btnSpecialBgColor.setOnClickListener {
            openColorPicker(initialColor = currentCustomTheme.special) { color ->
                currentCustomTheme = currentCustomTheme.copy(special = color)
                saveAndRefreshPreview()
                binding.previewSpecialBg.background.setTint(color.toColorInt())
            }
        }

        // 5. တွၼ်ႈတႃႇ Save Custom Theme
        binding.btnSaveCustomTheme.setOnClickListener {
            showSaveDialog()
        }
    }

    // Helper တွၼ်ႈတႃႇ Update Preview ၽၢႆႇၼိူဝ် (Activity)
    private fun updatePreview() {
        (activity as? ChooseThemeActivity)?.updatePreview()
    }

    private fun saveAndRefreshPreview() {
        ThemeManager.saveCustomKeyboardTheme(requireContext(), currentCustomTheme)
        updatePreview()
    }

    // တီႈၼႆႈ ၸဝ်ႈၵဝ်ႇၸၢင်ႈၸႂ်ႉ Color Picker Library ဢၼ်ၸဝ်ႈၵဝ်ႇလွၵ်ႇၸႂ်
    // 2. Function ပိုတ်ႇ Color Picker Dialog
    private fun openColorPicker(initialColor: String, onColorSelected: (String) -> Unit) {
        val colorInt = try {
            initialColor.toColorInt()
        } catch (e: Exception) {
            Color.BLACK
        }

        // AmbilWarnaDialog(Context, InitialColor, SupportsAlpha, Listener)
        val dialog = AmbilWarnaDialog(
            requireContext(),
            colorInt,
            true,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {}

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    // လႅၵ်ႈသီ Int ပဵၼ် Hex String (#AARRGGBB)
                    val hexColor = String.format("#%08X", color)
                    onColorSelected(hexColor)
                }
            })
        dialog.show()
    }

    private fun showSaveDialog() {
        // TODO: Show AlertDialog with EditText to get Theme Name
    }
}