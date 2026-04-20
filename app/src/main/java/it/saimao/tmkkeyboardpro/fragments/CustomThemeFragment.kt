package it.saimao.tmkkeyboardpro.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
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
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File

class CustomThemeFragment : Fragment() {

    private lateinit var binding: FragmentCustomThemeBinding

    private lateinit var currentCustomTheme: KeyboardTheme

    // 1. သၢင်ႈ File Picker Launcher
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // မိူဝ်ႈလိူၵ်ႈႁၢင်ႈယဝ်ႉ ပိုတ်ႇ uCrop
                startCrop(uri)
            }
        }

    // 2. Launcher တွၼ်ႈတႃႇႁပ်ႉ Result လုၵ်ႉတီႈ uCrop
    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let { uri ->
                    // *** တီႈၼႆႈ ၸဝ်ႈၵဝ်ႇလူဝ်ႇ Take Persistable Permission သင်ၸႂ်ႉ ImageURI ၼႂ်း Service ***
                    currentCustomTheme = currentCustomTheme.copy(bg = uri.toString())
                    saveAndRefreshPreview()
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                cropError?.printStackTrace()
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
        // မိူဝ်ႈတဵၵ်းလိူၵ်ႈ image Background
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

    // 3. Helper Function တွၼ်ႈတႃႇ Setup uCrop
    private fun startCrop(sourceUri: Uri) {
        val destinationFileName = "tmk_cropped_bg_${System.currentTimeMillis()}.jpg"
        // သိမ်းႁၢင်ႈဢၼ်တတ်းယဝ်ႉဝႆႉၼႂ်း Cache Folder ၶွင် App ႁင်းၵူၺ်း
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, destinationFileName))

        // Setup uCrop Options
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG) // သႂ်ႇ Format
            setCompressionQuality(90) // လူတ်း Quality ၼင်ႇႁိုဝ် File တေဢမ်ႇယႂ်ႇ
            // တတ်းပဵၼ်သီႇၸဵင်ႈပႅတ်ႈ (AspectRatio) တႅတ်ႈတေႃး (တွၼ်ႈတႃႇ Keyboard View)
            setAspectRatioOptions(
                0,

                AspectRatio("Keyboard (4:3)", 4f, 3f),
                AspectRatio("Keyboard (3:2)", 3f, 2f),
                AspectRatio("Keyboard (16:9)", 16f, 9f),
            )
            setHideBottomControls(false) // ၼႄ controls တွၼ်ႈတႃႇ rotate/scale
        }

        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .withMaxResultSize(
                1080,
                1920
            ) // *** လူတ်းယွမ်းတၢင်းယႂ်ႇ (Resize) ႁႂ်ႈမၼ်းဢမ်ႇပူၼ်ႉတီႈ ***
            .getIntent(requireContext())

        cropLauncher.launch(uCropIntent) // ပိုတ်ႇ uCrop Editor

    }

    private fun showSaveDialog() {
        // TODO: Show AlertDialog with EditText to get Theme Name
    }
}