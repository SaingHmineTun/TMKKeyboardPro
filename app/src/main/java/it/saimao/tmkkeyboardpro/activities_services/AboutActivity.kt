package it.saimao.tmkkeyboardpro.activities_services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import it.saimao.tmkkeyboardpro.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Website Link
        findViewById(R.id.btn_web).setOnClickListener(v ->
                openUrl("http://www.tmkacademy.com"));

        // Facebook Link
        findViewById(R.id.btn_fb).setOnClickListener(v ->
                openUrl("https://www.facebook.com/profile.php?id=61569069823862"));

        // GitHub Link
        findViewById(R.id.btn_github).setOnClickListener(v ->
                openUrl("https://github.com/SaingHmineTun/TMKKeyboardPro"));

        // Email Intent
        findViewById(R.id.btn_email).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:tmk.muse@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for TMK Keyboard Pro");
            startActivity(intent);
        });
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}