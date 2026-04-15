package it.saimao.tmkkeyboardpro

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import it.saimao.tmkkeyboardpro.logic.ShanLanguageEngine
import kotlin.properties.Delegates


class ShanKeyboardService : InputMethodService() {


    private var lastShiftClickTime: Long = 0
    private val CAPS_LOCK_THRESHOLD = 500 // 500 milliseconds (0.5 sec)
    private val languages = listOf("EN", "SHN", "MY")
    private var currentLanguageIndex = 0

    // ဢဝ်ပဵၼ် Property တႃႇႁွင်ႉၸႂ်ႉငၢႆႈ
    val currentLanguage: String
        get() = languages[currentLanguageIndex]

    enum class ShiftState {
        OFF, ON, CAPS_LOCK
    }

    private var currentShiftState = ShiftState.OFF

    private lateinit var keysContainer: FrameLayout
    private lateinit var suggestionBarContainer: FrameLayout

    private lateinit var candidateContainer: LinearLayout
    private lateinit var currentInputView: View

    private var backgroundColor by Delegates.notNull<Int>()

    // --- တွၼ်ႈတႃႇ Suggestion Logic ---

    private lateinit var shanDictionary: DictionaryManager

    private lateinit var myanmarDictionary: DictionaryManager
    private lateinit var englishDictionary: DictionaryManager

    override fun onCreate() {
        super.onCreate()
        shanDictionary = ShanDictionaryManager(this)
        myanmarDictionary = MyanmarDictionaryManager(this)
        englishDictionary = EnglishDictionaryManager(this)
    }

    override fun onCreateInputView(): View {
        currentInputView = layoutInflater.inflate(R.layout.keyboard_root, null)

        keysContainer = currentInputView.findViewById(R.id.keys_container)
        suggestionBarContainer = currentInputView.findViewById(R.id.suggestion_bar_container)

        val candidateView =
            layoutInflater.inflate(R.layout.candidate_view, suggestionBarContainer, false)
        suggestionBarContainer.addView(candidateView)
        candidateContainer = candidateView.findViewById(R.id.candidate_container)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            currentInputView.setOnApplyWindowInsetsListener { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                // သႂ်ႇ Padding ၽၢႆႇတႂ်ႈ ၸွမ်းၼင်ႇတၢင်းသုင်ၶွင် Navigation Bar တႄႉတႄႉ
                view.setPadding(0, 0, 0, systemBars.bottom)

                insets
            }
        }

        updateKeyboardLayout()

        return currentInputView
    }

    // Helper Function တႃႇလႅၵ်ႈ Keyboard (EN, MM, SHN)
    fun loadLayout(layoutId: Int) {
        keysContainer.removeAllViews() // လၢင်ႉ View ဢၼ်ၵဝ်ႇပႅတ်ႈ
        val newKeysView = layoutInflater.inflate(layoutId, null)
        keysContainer.addView(newKeysView)

        // *** ၵွင်ႉ Click Listener ႁႂ်ႈ Buttons တင်းသဵင်ႈ မႃးႁဵတ်းၵၢၼ်တီႈၼႆႈ ***
        registerKeys(newKeysView)

        // မႄးသီ Theme ပႃးၵမ်းလဵဝ်
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTheme = prefs.getString("keyboard_theme", "BLUE") ?: "BLUE"
        applyTheme(newKeysView, currentTheme)

    }


    fun updateSuggestions() {

        if (!::candidateContainer.isInitialized) return
        // 1. ဢဝ်ၶေႃႈၵႂၢမ်းဢၼ်တိုၵ်ႉတႅမ်ႈဝႆႉ (Current Word)
        val currentWord = getCurrentWordBeforeCursor()


        // 2. ႁႃၶေႃႈၵႂၢမ်းလႅတ်း လုၵ်ႉၼႂ်း Dictionary
        val suggestions = when (currentLanguage) {
            "SHN" -> shanDictionary.getSuggestions(currentWord)
            "MY" -> myanmarDictionary.getSuggestions(currentWord)
            "EN" -> englishDictionary.getSuggestions(currentWord)
            else -> englishDictionary.getSuggestions(currentWord)
        }


        // 3. ၼႄဢွၵ်ႇၼိူဝ် Candidate Bar
        candidateContainer.removeAllViews()

        if (suggestions.isNotEmpty()) {
            for (word in suggestions) {
                val tv = TextView(this).apply {
                    text = word
                    textSize = 18f
                    setTextColor(Color.WHITE)
                    setPadding(30, 0, 30, 0)
                    gravity = android.view.Gravity.CENTER
                    // *** တူဝ်ယႂ်ႇ: လူဝ်ႇသႂ်ႇ LayoutParams တႅတ်ႈတေႃး ***
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    setOnClickListener {
                        // မိူဝ်ႈၼိပ်ႉလိၵ်ႈလႅတ်း ႁႂ်ႈတႅၼ်းတီႈၶေႃႈၵဝ်ႇ
                        replaceCurrentWordWith(word)
                        candidateContainer.removeAllViews()
                    }
                }
                candidateContainer.addView(tv)
            }
        }
    }

    // Function တႃႇဢဝ် Word တူဝ်သုတ်းၽၢႆႇၼႃႈ Cursor
    private fun getCurrentWordBeforeCursor(): String {
        val ic = currentInputConnection ?: return ""
        // ဢဝ် Text မႃး 15 တူဝ် (ဢမ်ႇၼၼ် ၸွမ်းၼင်ႇၶေႃႈၵႂၢမ်းယၢဝ်းသုတ်း)
        val extractedText = ic.getTextBeforeCursor(15, 0) ?: return ""

        // တႅၵ်ႇဢဝ် Word သုတ်းထၢႆး (Split by Space or New Line)
        val words = extractedText.split(Regex("\\s+"))
        return if (words.isNotEmpty()) words.last() else ""
    }

    // Function တႃႇတႅၼ်း Word ဢၼ်တႅမ်ႈၽိတ်း/တႅမ်ႈပႆႇယဝ်ႉ လူၺ်ႈ Suggestion
    private fun replaceCurrentWordWith(word: String) {
        val ic = currentInputConnection ?: return
        val currentWord = getCurrentWordBeforeCursor()

        if (currentWord.isNotEmpty()) {
            // 1. လူတ်း Word ဢၼ်တိုၵ်ႉတႅမ်ႈဝႆႉၼၼ်ႉပႅတ်ႈ
            ic.deleteSurroundingText(currentWord.length, 0)

            // 2. သႂ်ႇ Word မႂ်ႇ ဢၼ်လိူၵ်ႈဝႆႉၶဝ်ႈၵႂႃႇ
            ic.commitText(word + " ", 1)
        }
    }


    fun applyTheme(view: View, themeType: String) {
        val keyNormalColor: Int
        val keyPressedColor: Int

        if (themeType == "GOLD") {
            keyNormalColor = getColor(R.color.gold_key_normal)
            keyPressedColor = getColor(R.color.gold_key_pressed)
            backgroundColor = getColor(R.color.gold_background)
        } else {
            keyNormalColor = getColor(R.color.blue_key_normal)
            keyPressedColor = getColor(R.color.blue_key_pressed)
            backgroundColor = getColor(R.color.blue_background)
        }

        // 1. သင်ပဵၼ် Root View ႁႂ်ႈလႅၵ်ႈသီ Background
        if (view.id == R.id.keyboard_root) {
            view.setBackgroundColor(backgroundColor)
        }

        // 2. ၸႂ်ႉ Recursion တႃႇႁႃ Buttons ၼႂ်းၵူႈ Container
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)

                if (child is Button) {
                    if (child.id == R.id.key_space) {
                        setupSpaceBarSwipeLogic(child);
                    }
                    // လႅၵ်ႈသီတုမ်ႇၼဵၵ်ႉ
                    val states = arrayOf(
                        intArrayOf(android.R.attr.state_pressed),
                        intArrayOf()
                    )
                    val colors = intArrayOf(keyPressedColor, keyNormalColor)
                    child.backgroundTintList = ColorStateList(states, colors)

                    // လွင်ႈယႂ်ႇ: ႁႂ်ႈ Tint Mode မၼ်းပဵၼ် SRC_IN ၼင်ႇႁိုဝ်တေႁၼ်သီမႂ်ႇ
                    child.backgroundTintMode = android.graphics.PorterDuff.Mode.SRC_IN
                } else if (child is ViewGroup) {
                    // သင်ၺႃး FrameLayout ဢမ်ႇၼၼ် LinearLayout တၢင်ႇဢၼ် ႁႂ်ႈၶဝ်ႈၵႂႃႇႁႃထႅင်ႈ
                    applyTheme(child, themeType)
                }
            }
        }
    }

    private var initialX = 0f
    private val SWIPE_THRESHOLD = 30 // တၢင်းၵႆ (Pixels) ဢၼ်တေၼပ်ႉပဵၼ် 1 တူဝ်လိၵ်ႈ

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSpaceBarSwipeLogic(spaceButton: Button) {
        spaceButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.x
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - initialX

                    // သင်ထူၺ်းၵႂႃႇၵႆလိူဝ် Threshold
                    if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                        if (deltaX > 0) {
                            moveCursorRight()
                        } else {
                            moveCursorLeft()
                        }
                        // Reset initialX ၼင်ႇႁိုဝ်တေၼပ်ႉတူဝ်ၼႃႈထႅင်ႈ
                        initialX = event.x
                    }
                }
            }
            false // ႁႂ်ႈ OnClickListener တိုၵ်ႉႁဵတ်းၵၢၼ်လႆႈယူႇ
        }
    }

    private fun moveCursorLeft() {
        val ic = currentInputConnection
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT))
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT))
    }

    private fun moveCursorRight() {
        val ic = currentInputConnection
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT))
        ic?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT))
    }

    @SuppressLint("ClickableViewAccessibility")
    fun registerKeys(view: View) {
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)

                if (child is Button) {


                    child.setOnClickListener {
                        val text = child.text.toString()
                        handleKeyPress(child, text)
                    }

                    // Long Click ပိုတ်ႇ Pop-up
                    child.setOnLongClickListener {
                        val popupChars = getPopupCharsFor(child.text.toString())
                        if (popupChars.isNotEmpty()) {
                            showPopup(child, popupChars)
                            true // တွၼ်ႈတႃႇလၢတ်ႈၼႄဝႃႈ ႁဝ်းၸတ်းၵၢၼ်ယဝ်ႉ
                        } else if (child.id == R.id.key_enter) {
                            if (currentLanguage == "SHN") {
                                ShanLanguageEngine(ic = currentInputConnection).convertZawgyi()
                                true
                            } else {
                                false
                            }
                        } else {
                            false
                        }
                    }

                    child.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_MOVE -> {
                                if (popupWindow?.isShowing == true) {
                                    // ထတ်းတူၺ်းဝႃႈ မိုဝ်းတိုၵ်ႉၺႃး Button လႂ်ၼႂ်း Popup
                                    checkPopupSelection(event.rawX, event.rawY)
                                }
                            }

                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                if (popupWindow?.isShowing == true) {
                                    val selectedChar = getSelectedPopupChar()
                                    if (selectedChar != null) {
                                        sendText(selectedChar)
                                    }
                                    popupWindow?.dismiss()
                                }
                            }
                        }
                        false // ႁႂ်ႈ LongClickListener တိုၵ်ႉႁဵတ်းၵၢၼ်လႆႈယူႇ
                    }

                } else if (child is ViewGroup) {
                    // သင်ၺႃး FrameLayout ႁႂ်ႈၶဝ်ႈၵႂႃႇႁႃ Buttons တၢင်ႇဢၼ်ထႅင်ႈ
                    registerKeys(child)
                }
            }
        }
    }

    private fun handleKeyPress(view: Button, text: String) {
        // 1. Trigger Feedback
        triggerVibration(view)
        playClickSound()

        when (val viewId = view.id) {
            R.id.key_del -> {
                sendDelete() // ဢၼ်ႁဝ်းတႅမ်ႈဝႆႉၼႂ်း Lesson 14
                updateSuggestions() // ႁွင်ႉၵူႈပွၵ်ႈမိူဝ်ႈ Delete
            }

            R.id.key_space -> {
                sendText(" ")
                candidateContainer.removeAllViews() // လၢင်ႉ Bar မိူဝ်ႈၼိပ်ႉ Space
            }

            R.id.key_lang -> toggleLanguage()
            R.id.key_enter -> sendKeyAction(KeyEvent.KEYCODE_ENTER)
            else -> {
                if (viewId == R.id.key_unshift || viewId == R.id.key_my_unshift || viewId == R.id.key_shn_unshift) {
                    handleShift()
                } else if (viewId == R.id.key_shift || viewId == R.id.key_my_shift || viewId == R.id.key_shn_shift) {
                    handleShift()

                } else {

                    // 1. ဢဝ် Text ဢၼ်လုၵ်ႉတီႈ Button မႃးလႅၵ်ႈပဵၼ် Unicode Code
                    val primaryCode = if (text.isNotEmpty()) text.first().code else -1

                    if (currentLanguage == "SHN" && primaryCode != -1) {

                        // 2. သူင်ႇ Code ၶဝ်ႈၵႂႃႇၼႂ်း Engine
                        val engine = ShanLanguageEngine(currentInputConnection!!)
                        val resultText = engine.handleInput(primaryCode)
                        if (resultText != null) {
                            sendText(resultText)
                        }

                    } else {
                        // ပဵၼ် English ဢမ်ႇၼၼ် တူဝ်လိၵ်ႈယူႇယူႇ
                        sendText(text)
                    }

                    // သင်ပဵၼ် Shift ON (ဢမ်ႇၸႂ်ႉ Caps Lock) ႁႂ်ႈပိၵ်ႉၶိုၼ်း မိူဝ်ႈတႅမ်ႈယဝ်ႉ 1 တူဝ်
                    if (currentShiftState == ShiftState.ON) {
                        currentShiftState = ShiftState.OFF
                        updateKeyboardLayout()
                    }
                }

                // သႂ်ႇဝႆႉၽၢႆႇတႂ်ႈသုတ်း မိူဝ်ႈတႅမ်ႈတူဝ်လိၵ်ႈ
                updateSuggestions()
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val currentTheme = prefs.getString("keyboard_theme", "GOLD") ?: "GOLD"

        Log.d("TAGY", currentTheme)
        // Apply theme ၵူႈပွၵ်ႈဢၼ် Keyboard ပိုတ်ႇဢွၵ်ႇမႃး
        applyTheme(currentInputView, currentTheme)
    }

    override fun onWindowShown() {
        super.onWindowShown()
        // ႁဵတ်းႁႂ်ႈ Window ၶွင် Keyboard ဢမ်ႇလႅၼ်ႈၶဝ်ႈၵႂႃႇတႂ်ႈ Navigation Bar
        window?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }
    }


    private fun handleShift() {

        val currentTime = System.currentTimeMillis()

        // 1. ထတ်းတူၺ်းဝႃႈ ၼိပ်ႉသွင်ပွၵ်ႈၼႂ်း 500ms ႁႃႉ?
        if (currentTime - lastShiftClickTime <= CAPS_LOCK_THRESHOLD) {
            // ၼိပ်ႉၽၢႆ: ပိုတ်ႇ Caps Lock
            currentShiftState = ShiftState.CAPS_LOCK
        } else {
            // ၼိပ်ႉယူႇယူႇ: လႅၵ်ႈ ON / OFF
            currentShiftState = if (currentShiftState == ShiftState.OFF) {
                ShiftState.ON
            } else {
                ShiftState.OFF
            }
        }

        // 2. ၵဵပ်းၶိင်ႇယၢမ်းပွၵ်ႈၼႆႉဝႆႉ တႃႇၸႂ်ႉပွၵ်ႈၼႃႈ
        lastShiftClickTime = currentTime

        // 3. Update Layout ႁႂ်ႈလႅၵ်ႈ XML ၸွမ်း
        updateKeyboardLayout()

    }

    // သူင်ႇလိၵ်ႈယူႇယူႇ
    fun sendText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    // ၵၢၼ်လူတ်းလိၵ်ႈ (Backspace)
    fun sendDelete() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    // သူင်ႇ Key Event (တႃႇ Enter, Tab)
    fun sendKeyAction(keyCode: Int) {
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }


    override fun onEvaluateFullscreenMode(): Boolean {
        // ပိၵ်ႉ Fullscreen Mode ဝႆႉ ၼင်ႇႁိုဝ်တေတူၺ်းႁၼ် App ၽၢႆႇလင်ယူႇတႃႇသေႇ
        // (တွၼ်ႈတႃႇ Landscape mode ၵေႃႈ ႁဝ်းတေတူၺ်းႁၼ်လိၵ်ႈယူႇ)
        return false
    }

    override fun onUpdateExtractingVisibility(ei: EditorInfo?) {
        // ႁဵတ်းႁႂ်ႈ Keyboard ဢမ်ႇၵိၼ်ၼႃႈၸေႃးတင်းသဵင်ႈ မိူဝ်ႈတႅမ်ႈ Landscape
        setExtractViewShown(false)
    }

    private var popupWindow: PopupWindow? = null
    private val popupButtons = mutableListOf<Button>()

    private fun showPopup(anchorView: View, popupCharacters: List<String>) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            backgroundColor = Color.DKGRAY
            elevation = 10f
        }

        popupButtons.clear()
        for (char in popupCharacters) {
            val btn = Button(this).apply {
                text = char
                background = null // ႁႂ်ႈမၼ်းတူၺ်းငၢႆႈ
                setTextColor(Color.WHITE)
            }
            layout.addView(btn)
            popupButtons.add(btn)
        }

        popupWindow = PopupWindow(
            layout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // ၼႄ Popup တီႈၼိူဝ် Anchor View
        popupWindow?.showAsDropDown(anchorView, 0, -anchorView.height * 2)
    }

    private fun checkPopupSelection(x: Float, y: Float) {
        for (btn in popupButtons) {
            val location = IntArray(2)
            btn.getLocationOnScreen(location)
            val rect = Rect(
                location[0], location[1],
                location[0] + btn.width, location[1] + btn.height
            )

            if (rect.contains(x.toInt(), y.toInt())) {
                btn.setBackgroundColor(Color.LTGRAY) // Highlight မိူဝ်ႈထူၺ်းၺႃး
                btn.tag = "selected"
            } else {
                btn.background = null
                btn.tag = null
            }
        }
    }

    private fun getSelectedPopupChar(): String? {
        return popupButtons.find { it.tag == "selected" }?.text?.toString()
    }

    private fun updateKeyboardLayout() {
        val layoutToLoad = when (currentLanguage) {
            "EN" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_en_normal else R.layout.layout_en_shifted
            "MY" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_my_normal else R.layout.layout_my_shifted
            "SHN" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_shn_normal else R.layout.layout_shn_shifted
            else -> R.layout.layout_en_normal
        }

        loadLayout(layoutToLoad) // Function ဢၼ်ႁဝ်းတႅမ်ႈဝႆႉၼႂ်း Lesson 15
    }

    fun triggerVibration(view: View) {
        // ၸႅတ်ႈတူၺ်းဝႃႈ ၵူၼ်းၸႂ်ႉပိုတ်ႇ Vibration ဝႆႉၼႂ်း Settings ႁႃႉ?
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isVibrateEnabled = prefs.getBoolean("vibrate_on_keypress", true)

        if (isVibrateEnabled) {
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING // ႁႂ်ႈမၼ်းတူင်ႉ ဢမ်ႇဝႃႈ System တေပိၵ်ႉဝႆႉၵေႃႈယဝ်ႉ
            )
        }
    }

    private fun updateShiftIcon(shiftButton: Button) {
        if (shiftButton.id in intArrayOf(
                R.id.key_shift,
                R.id.key_unshift,
                R.id.key_my_shift,
                R.id.key_my_unshift,
                R.id.key_shn_shift,
                R.id.key_shn_unshift
            )
        ) {

            when (currentShiftState) {
                ShiftState.OFF -> {
                    shiftButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_shift_off,
                        0,
                        0,
                        0
                    )
                    shiftButton.backgroundTintList = null // သီယူႇယူႇ
                }

                ShiftState.ON -> {
                    shiftButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_shift_on,
                        0,
                        0,
                        0
                    )
                }

                ShiftState.CAPS_LOCK -> {
                    shiftButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_shift_caps,
                        0,
                        0,
                        0
                    )
                    // သႂ်ႇသီပၼ် ၼင်ႇႁိုဝ်တေႁူႉဝႃႈမၼ်း "ၶမ်" (Locked) ဝႆႉ
                    shiftButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FFD700")) // Gold for Pro!
                }
            }
        }
    }

    fun playClickSound() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isSoundEnabled = prefs.getBoolean("sound_on_keypress", true)

        if (isSoundEnabled) {
            // လိူၵ်ႈသဵင်ၸွမ်းၼင်ႇ Keyboard Standard
            val soundType = AudioManager.FX_KEYPRESS_STANDARD
            am.playSoundEffect(soundType)
        }
    }

    fun toggleLanguage() {
        // ပၼ်ႇ Index (EN 0 -> SHN 1 -> MY 2 -> EN 0)
        currentLanguageIndex = (currentLanguageIndex + 1) % languages.size

        // Reset Shift State မိူဝ်ႈလႅၵ်ႈၽႃႇသႃႇ ၼင်ႇႁိုဝ်တေဢမ်ႇယုင်ႈ
        currentShiftState = ShiftState.OFF

        // Update Layout ၸွမ်းၼင်ႇၽႃႇသႃႇမႂ်ႇ
        updateKeyboardLayout()
    }


}