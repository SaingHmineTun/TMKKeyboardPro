package it.saimao.tmkkeyboardpro.activities_services

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.adapters.LanguageAdapter
import it.saimao.tmkkeyboardpro.databinding.ActivityKeyboardLanguagesBinding
import it.saimao.tmkkeyboardpro.model.KeyboardLanguage
import it.saimao.tmkkeyboardpro.model.Language
import it.saimao.tmkkeyboardpro.utils.getKeyboardLanguageState

class KeyboardLanguagesActivity : AppCompatActivity() {
    private lateinit var adapter: LanguageAdapter
    private lateinit var languageList: List<KeyboardLanguage>

    private lateinit var binding: ActivityKeyboardLanguagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        this.binding = ActivityKeyboardLanguagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Setup Toolbar & Back Button
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // 2. Window Insets (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
        ) { v: View?, insets: WindowInsetsCompat? ->
            val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
            v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. Initialize Data & RecyclerView
        initLanguageData()
        setupRecyclerView()
    }

    private fun initLanguageData() {
        languageList = listOf(
            KeyboardLanguage(Language.EN, isEnabled = true, isDefault = true),
            KeyboardLanguage(
                Language.SHN,
                getKeyboardLanguageState(this, Language.SHN.name),
                false
            ),

            KeyboardLanguage(
                Language.TDD,
                getKeyboardLanguageState(this, Language.TDD.name),
                false
            ),

            KeyboardLanguage(
                Language.TH,
                getKeyboardLanguageState(this, Language.TH.name),
                false
            ),

            KeyboardLanguage(
                Language.LO,
                getKeyboardLanguageState(this, Language.LO.name),
                false
            ),

            KeyboardLanguage(
                Language.MY,
                getKeyboardLanguageState(this, Language.MY.name),
                false
            ),

            KeyboardLanguage(
                Language.AHM,
                getKeyboardLanguageState(this, Language.AHM.name),
                false
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = LanguageAdapter(languageList)
        binding.rvLanguages.setLayoutManager(LinearLayoutManager(this))
        binding.rvLanguages.setAdapter(adapter)
    }
}