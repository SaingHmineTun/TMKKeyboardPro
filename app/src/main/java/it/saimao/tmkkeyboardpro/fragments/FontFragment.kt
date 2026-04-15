package it.saimao.tmkkeyboardpro.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.databinding.FragmentFontBinding
import it.saimao.tmkkeyboardpro.utils.FontManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FontFragment : Fragment() {
    private lateinit var binding: FragmentFontBinding
    private var downloadId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFontBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnDownload.setOnClickListener {
            val selectedFont = when (binding.rgFonts.checkedRadioButtonId) {
                R.id.rb_panglong -> FontManager.fonts[1]
                R.id.rb_kunheing -> FontManager.fonts[2]
                else -> FontManager.fonts[0]
            }

            downloadId = FontManager.downloadFont(requireContext(), selectedFont)
            showProgressDialog()
        }
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
            val manager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var downloading = true
            while (downloading) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }

                    val progress = if (bytesTotal > 0) (bytesDownloaded * 100L / bytesTotal).toInt() else 0
                    withContext(Dispatchers.Main) {
                        progressBar.progress = progress
                        tvPercent.text = "$progress%"
                        if (!downloading) {
                            dialog.dismiss()
                            Toast.makeText(context, "Font Downloaded & Applied!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                cursor.close()
                delay(500) // ၸႅတ်ႈၵူႈ 0.5 Sec
            }
        }
    }
}