package it.saimao.tmkkeyboardpro.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.databinding.FragmentFontBinding
import it.saimao.tmkkeyboardpro.logic.FontManager
import it.saimao.tmkkeyboardpro.logic.ShanFont
import it.saimao.tmkkeyboardpro.utils.getSavedFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FontFragment : Fragment() {
    private lateinit var binding: FragmentFontBinding
    private var downloadId: Long = -1
    private lateinit var rbNonSelected: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFontBinding.inflate(inflater, container, false)
        setupFontRadioButtons()
        return binding.root
    }

    private fun setupFontRadioButtons() {
        // 1. Clear တူဝ်ၵဝ်ႇဢၼ်မီးဝႆႉၼႂ်း XML ဢွၼ်တၢင်း (သင်မီး)
        binding.rgFonts.removeAllViews()
        rbNonSelected = RadioButton(requireContext()).apply {
            text = "None"
            textSize = 16f
            setPadding(20, 10, 20, 10)
        }
        binding.rgFonts.addView(rbNonSelected)
        rbNonSelected.isChecked = true


        // 2. Loop ပၼ်ႇတူၺ်း Fonts တင်းမူတ်း
        FontManager.fonts.forEachIndexed { index, font ->
            val radioButton = RadioButton(requireContext()).apply {
                id = View.generateViewId() // သၢင်ႈ ID ႁင်းၵူၺ်း
                text = font.name
                tag = font // သိမ်း Object font ဝႆႉၼႂ်း Tag တွၼ်ႈတႃႇဢဝ်မႃးၸႂ်ႉငၢႆႈ

                // သႂ်ႇ Styling ဢိတ်းၼိုင်ႈ ႁႂ်ႈတူၺ်းႁၢင်ႈလီ
                textSize = 16f
                setPadding(20, 10, 20, 10)

            }

            // 3. Add ၶဝ်ႈၵႂႃႇၼႂ်း RadioGroup
            binding.rgFonts.addView(radioButton)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // apply the appropriate radio buttons
        val savedFontName = getSavedFont(requireContext())
        Log.d("TAGY", "Saved Font Name: $savedFontName")

        if (savedFontName != null) {
            val savedFont =
                FontManager.fonts.firstOrNull() { font -> font.fileName == savedFontName }
            if (savedFont != null) {
                val radioButton = binding.rgFonts.findViewWithTag<RadioButton>(savedFont)
                radioButton?.isChecked = true
            }
        }

        rbNonSelected.setOnCheckedChangeListener { _, isChecked ->
            binding.btnDownload.isEnabled = !isChecked
        }
        binding.btnDownload.setOnClickListener {

            // ႁႃ RadioButton တူဝ်ဢၼ် Checked ဝႆႉ
            val checkedId = binding.rgFonts.checkedRadioButtonId
            val selectedRb = binding.rgFonts.findViewById<RadioButton>(checkedId)

            // ဢဝ် Font Object လုၵ်ႉတီႈ Tag ဢၼ်ႁဝ်းသႂ်ႇဝႆႉၼၼ်ႉမႃး
            val selectedFont = selectedRb.tag as ShanFont

            if (FontManager.isFontDownloaded(requireContext(), selectedFont)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Font Already Exists")
                    .setMessage("${selectedFont.name} is already downloaded. Do you want to download it again?")
                    .setPositiveButton("Apply") { dialog, _ ->
                        FontManager.applyFont(requireContext(), selectedFont)
                        dialog.dismiss()
                    }
                    .setNeutralButton("Redownload") { dialog, _ ->
                        startDownload(selectedFont)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                startDownload(selectedFont)
            }

        }
    }

    private fun startDownload(selectedFont: ShanFont) {

        downloadId = FontManager.downloadFont(requireContext(), selectedFont)
        showProgressDialog()
    }

    @SuppressLint("Range")
    private fun showProgressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_progress, null)
        val progressBar = dialogView.findViewById<LinearProgressIndicator>(R.id.progress_bar)
        val tvPercent = dialogView.findViewById<TextView>(R.id.tv_percent)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Downloading Font...")
            .setView(dialogView)
            .show()

        // Track Progress
        lifecycleScope.launch {
            val manager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var downloading = true
            while (downloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val bytesDownloaded =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }

                    val progress =
                        if (bytesTotal > 0) (bytesDownloaded * 100L / bytesTotal).toInt() else 0
                    withContext(Dispatchers.Main) {
                        progressBar.progress = progress
                        tvPercent.text = "$progress%"
                        if (!downloading) {
                            dialog.dismiss()
                            Toast.makeText(
                                context,
                                "Font Downloaded & Applied!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                cursor.close()
                delay(500) // ၸႅတ်ႈၵူႈ 0.5 Sec
            }
        }
    }
}