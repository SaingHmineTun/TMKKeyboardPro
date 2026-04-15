package it.saimao.tmkkeyboardpro

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import it.saimao.tmkkeyboardpro.databinding.ActivityMainBinding
import it.saimao.tmkkeyboardpro.fragments.SettingsFragment
import it.saimao.tmkkeyboardpro.fragments.SetupFragment
import it.saimao.tmkkeyboardpro.utils.KeyboardUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // *** ၸႂ်ႉတူဝ်ၼႆႉ တႅၼ်း onResume ***
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // မိူဝ်ႈ Dialog လိူၵ်ႈ Keyboard ပိၵ်ႉၵႂႃႇ သေၵူၼ်းၸႂ်ႉပွၵ်ႈမႃးၼႂ်း App
            // Focus တေပဵၼ် true သေ မၼ်းတေႁွင်ႉ Function ၼႆႉၶႃႈ
            checkKeyboardState()
        }
    }

    private fun checkKeyboardState() {
        val isEnabled = KeyboardUtils.isKeyboardEnabled(this)
        val isSelected = KeyboardUtils.isKeyboardSelected(this)


        Log.d(
            "TAGY",
            "Check Keyboard States \n${if (isEnabled) "Enabled" else "Not yet enabled"}\n${if (isSelected) "Selected" else "Not yet selected"}"
        )

        if (isEnabled && isSelected) {
            // သင်ယဝ်ႉမူတ်းယဝ်ႉ -> ၼႄ Settings
            showFragment(SettingsFragment())
        } else {
            // သင်ပႆႇယဝ်ႉ -> ၼႄ Setup Wizard
            showFragment(SetupFragment())
        }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_container,
                fragment
            ) // ႁႂ်ႈလႅၵ်ႈ id @+id/main ပဵၼ် FrameLayout ဢၼ်ၼိုင်ႈၶႃႈ
            .commit()
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // သႂ်ႇ Animation ဢိတ်းၼိုင်ႈ ႁႂ်ႈမၼ်း Slide မႃး
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.main_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null) // တွၼ်ႈတႃႇႁႂ်ႈၼိပ်ႉ Back သေပွၵ်ႈမႃး Settings ၶိုၼ်းလႆႈ
        }
        transaction.commit()
    }

}