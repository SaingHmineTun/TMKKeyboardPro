package it.saimao.tmkkeyboardpro.activities_services

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import it.saimao.tmkkeyboardpro.R
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Website Link
        findViewById<View>(R.id.btn_web).setOnClickListener {
            openUrl(
                "http://www.tmkacademy.com"
            )
        }

        // Facebook Link
        findViewById<View>(R.id.btn_fb).setOnClickListener {
            openUrl(
                "https://www.facebook.com/profile.php?id=61569069823862"
            )
        }

        // GitHub Link
        findViewById<View>(R.id.btn_github).setOnClickListener {
            View.OnClickListener { v: View? -> }
            openUrl(
                "https://github.com/SaingHmineTun/TMKKeyboardPro"
            )
        }

        // Email Intent
        findViewById<View>(R.id.btn_email).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData("mailto:tmk.muse@gmail.com".toUri())
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for TMK Keyboard Pro")
            startActivity(intent)
        }
    }

    private fun openUrl(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, url?.toUri())
        startActivity(intent)
    }
}