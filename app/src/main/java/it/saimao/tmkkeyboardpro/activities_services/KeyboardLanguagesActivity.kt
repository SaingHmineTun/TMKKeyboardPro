package it.saimao.tmkkeyboardpro.activities_services;

import static it.saimao.tmkkeyboardpro.utils.SharedPreferenceManagerKt.getKeyboardLanguageState;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import it.saimao.tmkkeyboardpro.R;
import it.saimao.tmkkeyboardpro.adapters.LanguageAdapter;
import it.saimao.tmkkeyboardpro.model.KeyboardLanguage;
import it.saimao.tmkkeyboardpro.model.Language;

public class KeyboardLanguagesActivity extends AppCompatActivity {

    private RecyclerView rvLanguages;
    private LanguageAdapter adapter;
    private List<KeyboardLanguage> languageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_keyboard_languages);

        // 1. Setup Toolbar & Back Button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 2. Window Insets (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3. Initialize Data & RecyclerView
        initLanguageData();
        setupRecyclerView();
    }

    private void initLanguageData() {
        languageList = new ArrayList<>();

        // ထႅမ်သႂ်ႇၽႃႇသႃႇတင်း 6 မဵဝ်း ၸွမ်းၼင်ႇ Keyboard ၸဝ်ႈၵဝ်ႇ
        // English ပဵၼ် Default (isEnabled = true, isDefault = true)
        languageList.add(new KeyboardLanguage(Language.EN, true, true));

        // ၽႃႇသႃႇဢၼ်ၵိုတ်း လုၵ်ႉတီႈ SharedPreferences (Default = false)
        languageList.add(new KeyboardLanguage(Language.SHN, getKeyboardLanguageState(this, Language.SHN.name()), false));
        languageList.add(new KeyboardLanguage(Language.TDD, getKeyboardLanguageState(this, Language.TDD.name()), false));
        languageList.add(new KeyboardLanguage(Language.TH, getKeyboardLanguageState(this, Language.TH.name()), false));
        languageList.add(new KeyboardLanguage(Language.LO, getKeyboardLanguageState(this, Language.LO.name()), false));
        languageList.add(new KeyboardLanguage(Language.MY, getKeyboardLanguageState(this, Language.MY.name()), false));
        languageList.add(new KeyboardLanguage(Language.AHM, getKeyboardLanguageState(this, Language.AHM.name()), false));
    }

    private void setupRecyclerView() {
        rvLanguages = findViewById(R.id.rv_languages);
        adapter = new LanguageAdapter(languageList);
        rvLanguages.setLayoutManager(new LinearLayoutManager(this));
        rvLanguages.setAdapter(adapter);
    }
}