package it.saimao.tmkkeyboardpro.logic

import android.app.DownloadManager
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import java.io.File

data class ShanFont(val name: String, val url: String, val fileName: String)

object FontManager {
    val fonts = listOf(
        ShanFont(
            "Shan",
            "https://shan-font-library.vercel.app/fonts/shan_regular.ttf",
            "shan.ttf"
        ),
        ShanFont(
            "PangLong",
            "https://shan-font-library.vercel.app/fonts/panglong.ttf",
            "panglong.ttf"
        ),
        ShanFont(
            "A J Kunheing E-T-M 05",
            "https://shan-font-library.vercel.app/fonts/aj05_etm_regular.ttf",
            "aj_kunheing_05.ttf"
        )
    )

    fun downloadFont(context: Context, font: ShanFont): Long {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(font.url))
            .setTitle("Downloading ${font.name}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalFilesDir(context, null, font.fileName)
            .setAllowedOverMetered(true)

        // Save selected font name to preferences to use in Keyboard
        context.getSharedPreferences("TMK_PREFS", Context.MODE_PRIVATE).edit()
            .putString("active_font_file", font.fileName).apply()

        return manager.enqueue(request)
    }

    fun getActiveTypeface(context: Context): Typeface? {
        val prefs = context.getSharedPreferences("TMK_PREFS", Context.MODE_PRIVATE)
        val fileName = prefs.getString("active_font_file", "namkhone.ttf")
        val file = File(context.getExternalFilesDir(null), fileName)
        return if (file.exists()) Typeface.createFromFile(file) else null
    }

}