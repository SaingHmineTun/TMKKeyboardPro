package it.saimao.tmkkeyboardpro.logic

import android.app.DownloadManager
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import it.saimao.tmkkeyboardpro.utils.saveFont
import java.io.File
import androidx.core.net.toUri
import it.saimao.tmkkeyboardpro.utils.getSavedFont

data class ShanFont(val name: String, val url: String, val fileName: String)

object FontManager {
    val fonts = listOf(
        ShanFont(
            "Shan",
            "https://shan-font-library.vercel.app/fonts/shan_regular.ttf",
            "shan.ttf"
        ),
        ShanFont(
            "Pang Long",
            "https://shan-font-library.vercel.app/fonts/panglong.ttf",
            "panglong.ttf"
        ),
        ShanFont(
            "A J Kunheing E-T-M 05",
            "https://shan-font-library.vercel.app/fonts/aj05_etm_regular.ttf",
            "aj_kunheing_05.ttf"
        ),
        ShanFont(
            "KT Unicode 03",
            "https://shan-font-library.vercel.app/fonts/kt_03.ttf",
            "kt03.ttf"
        ),
    )

    fun downloadFont(context: Context, font: ShanFont): Long {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(font.url.toUri())
            .setTitle("Downloading ${font.name}")
            .setDescription("Please wait while the font is being downloaded.")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//            .setDestinationInExternalFilesDir(context, null, font.fileName) // /storage/emulated/0/Android/data/it.saimao.tmkkeyboardpro/files/myfont.ttf
            .setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                font.fileName
            ) // /storage/emulated/0/Android/data/it.saimao.tmkkeyboardpro/files/Download/myfont.ttf
            .setAllowedOverMetered(true)


        applyFont(context, font)
        return manager.enqueue(request)
    }

    fun applyFont(context: Context, font: ShanFont) {

        saveFont(context, font.fileName)
    }


    fun isFontDownloaded(context: Context, font: ShanFont): Boolean {
        // ၸႅတ်ႈတူၺ်း File ၼႂ်း External Files Directory ၶွင် App
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), font.fileName)
        return file.exists() && file.length() > 0
    }

    fun getActiveTypeface(context: Context): Typeface? {
        val fileName = getSavedFont(context)
        if (fileName.isEmpty()) return null

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
        return if (file.exists()) Typeface.createFromFile(file) else null
    }

}