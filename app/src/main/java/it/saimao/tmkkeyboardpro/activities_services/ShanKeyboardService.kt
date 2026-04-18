package it.saimao.tmkkeyboardpro.activities_services

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
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
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.FlowLayout
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.logic.DictionaryManager
import it.saimao.tmkkeyboardpro.logic.EmojiKeyboard
import it.saimao.tmkkeyboardpro.logic.EnglishDictionaryManager
import it.saimao.tmkkeyboardpro.logic.FontManager
import it.saimao.tmkkeyboardpro.logic.MyanmarDictionaryManager
import it.saimao.tmkkeyboardpro.logic.ShanDictionaryManager
import it.saimao.tmkkeyboardpro.logic.ShanKeyboard
import it.saimao.tmkkeyboardpro.logic.ShanLanguageEngine
import it.saimao.tmkkeyboardpro.logic.ThemeManager.applyTheme
import it.saimao.tmkkeyboardpro.utils.getHandWritingSystem
import it.saimao.tmkkeyboardpro.utils.getPopupCharsFor
import it.saimao.tmkkeyboardpro.utils.getSoundOnKeyPress
import it.saimao.tmkkeyboardpro.utils.getVibrateOnKeyPress
import kotlin.math.abs
import kotlin.properties.Delegates

class ShanKeyboardService : InputMethodService() {


    private var lastShiftClickTime: Long = 0
    private val CAPS_LOCK_THRESHOLD = 500 // 500 milliseconds (0.5 sec)
    private val languages = listOf("EN", "SHN", "MY")
    private var currentLanguageIndex = 0

    enum class SymbolState {
        OFF, LAYER_1, LAYER_2
    }

    private var currentSymbolState = SymbolState.OFF

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

    private var _shanLanguageEngine: ShanLanguageEngine? = null
    private val shanLanguageEngine: ShanLanguageEngine
        get() {
            if (_shanLanguageEngine == null) {
                _shanLanguageEngine = ShanLanguageEngine(currentInputConnection)
            }
            return _shanLanguageEngine!!
        }


    // --- တွၼ်ႈတႃႇ Suggestion Logic ---

    private lateinit var shanDictionary: DictionaryManager
    private lateinit var myanmarDictionary: DictionaryManager
    private lateinit var englishDictionary: DictionaryManager

    override fun onCreate() {
        super.onCreate()
        // Pre-cache layouts ၼင်ႇႁိုဝ်မိူဝ်ႈ User ၼိပ်ႉလႅၵ်ႈၽႃႇသႃႇ တေဢမ်ႇ Lag သေဢိတ်း
        layoutCache[R.layout.layout_en_normal] =
            layoutInflater.inflate(R.layout.layout_en_normal, null).also { registerKeys(it) }
        shanDictionary = ShanDictionaryManager(this)
        myanmarDictionary = MyanmarDictionaryManager(this)
        englishDictionary = EnglishDictionaryManager(this)
        setupVoiceInput()
        setupClipboard()
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

    private lateinit var shanKeyboardView: View
    private lateinit var englishKeyboardView: View
    private lateinit var myanmarKeyboardView: View


    private fun setSuggestions(suggestions: List<String>) {


        if (!::candidateContainer.isInitialized) return

        // 3. ၼႄဢွၵ်ႇၼိူဝ် Candidate Bar
        candidateContainer.removeAllViews()

        if (suggestions.isNotEmpty()) {
            for (word in suggestions) {
                val tv = TextView(this).apply {
                    text = word
                    textSize = 18f
                    setPadding(30, 0, 30, 0)
                    gravity = Gravity.CENTER
                    // *** တူဝ်ယႂ်ႇ: လူဝ်ႇသႂ်ႇ LayoutParams တႅတ်ႈတေႃး ***
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    typeface = FontManager.getActiveTypeface(this@ShanKeyboardService)

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

    fun updateSuggestions() {

        // 1. ဢဝ်ၶေႃႈၵႂၢမ်းဢၼ်တိုၵ်ႉတႅမ်ႈဝႆႉ (Current Word)
        val currentWord = getCurrentWordBeforeCursor()


        // 2. ႁႃၶေႃႈၵႂၢမ်းလႅတ်း လုၵ်ႉၼႂ်း Dictionary
        val suggestions = when (currentLanguage) {
            "SHN" -> shanDictionary.getSuggestions(currentWord)
            "MY" -> myanmarDictionary.getSuggestions(currentWord)
            "EN" -> englishDictionary.getSuggestions(currentWord)
            else -> englishDictionary.getSuggestions(currentWord)
        }

        setSuggestions(suggestions)
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


    private var initialX = 0f
    private val SWIPE_THRESHOLD = 30 // တၢင်းၵႆ (Pixels) ဢၼ်တေၼပ်ႉပဵၼ် 1 တူဝ်လိၵ်ႈ


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

    private val deleteHandler = Handler(Looper.getMainLooper())
    private var deleteRunnable: Runnable? = null
    private val INITIAL_DELAY = 500L // ပႂ်ႉ 0.5 Sec ၵွၼ်ႇတေတႄႇ Repeat
    private val REPEAT_DELAY = 50L   // ဝႆး 0.05 Sec ၵူႈပွၵ်ႈဢၼ် Delete

    private fun startContinuousDelete() {
        deleteRunnable = object : Runnable {
            override fun run() {
                sendDelete()
                updateSuggestions()
                // ႁွင်ႉတူဝ်မၼ်းၶိုၼ်း ႁႂ်ႈပဵၼ် Loop
                deleteHandler.postDelayed(this, REPEAT_DELAY)
            }
        }
        // တႄႇႁဵတ်းၵၢၼ် ဝၢႆးလင် INITIAL_DELAY
        deleteHandler.postDelayed(deleteRunnable!!, INITIAL_DELAY)
    }

    private fun stopContinuousDelete() {
        deleteRunnable?.let { deleteHandler.removeCallbacks(it) }
        deleteRunnable = null
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
                        val popupChars = getPopupCharsFor(currentLanguage, child.text.toString())
                        if (popupChars.isNotEmpty()) {
                            showPopup(child, popupChars)
                            true // တွၼ်ႈတႃႇလၢတ်ႈၼႄဝႃႈ ႁဝ်းၸတ်းၵၢၼ်ယဝ်ႉ
                        } else if (child.id == R.id.key_enter) {
                            if (currentLanguage == "SHN") {
                                shanLanguageEngine.convertZawgyi()
                                true
                            } else {
                                false
                            }
                        } else if (child.id == R.id.key_emoji) {
                            showClipboardHistory()
                            true
                        } else {
                            false
                        }
                    }


                    // 3. Setup ONE Combined Touch Listener (လွင်ႈယႂ်ႇသုတ်း)
                    child.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                if (child.id == R.id.key_del) {
                                    if (currentLanguage == "SHN") {
                                        ShanKeyboard.getInstance(this)
                                            .handleShanDelete(currentInputConnection)
                                    } else {
                                        sendDelete()
                                    }
                                    updateSuggestions()
                                    playClickSound()
                                    startContinuousDelete()
                                    return@setOnTouchListener true // Handle Delete ၵမ်းလဵဝ်
                                }


                                // --- တွၼ်ႈတႃႇ Space Swipe ---
                                if (child.id == R.id.key_space) {
                                    initialX = event.x // သိမ်းတီႈတႄႇၼိပ်ႉ
                                }
                            }

                            MotionEvent.ACTION_MOVE -> {
                                if (popupWindow?.isShowing == true) {
                                    checkPopupSelection(event.rawX, event.rawY)
                                }
                                if (child.id == R.id.key_space) {
                                    val deltaX = event.x - initialX
                                    if (abs(deltaX) > SWIPE_THRESHOLD) {
                                        if (deltaX > 0) moveCursorRight() else moveCursorLeft()
                                        initialX = event.x // Reset တီႈၼပ်ႉမႂ်ႇ
                                        return@setOnTouchListener true // ႁႂ်ႈမၼ်း Handle Swipe ၵမ်းလဵဝ်
                                    }
                                }
                            }

                            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                // ယႅတ်း Repeat Delete သင်ပဵၼ်တုမ်ႇ Delete
                                if (child.id == R.id.key_del) {
                                    stopContinuousDelete()
                                }

                                // ၸတ်းၵၢၼ် Popup သင်မၼ်းပိုတ်ႇဝႆႉ
                                if (popupWindow?.isShowing == true) {
                                    val selectedChar = getSelectedPopupChar()
                                    if (selectedChar != null) sendText(selectedChar)
                                    popupWindow?.dismiss()
                                }
                            }
                        }
                        false // ႁႂ်ႈ LongClickListener/ClickListener တိုၵ်ႉႁဵတ်းၵၢၼ်လႆႈ
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
        var cText = text;
        triggerVibration(view)
        playClickSound()

        when (val viewId = view.id) {

            R.id.key_space -> {
                sendText(" ")
                candidateContainer.removeAllViews() // လၢင်ႉ Bar မိူဝ်ႈၼိပ်ႉ Space
            }

            R.id.key_emoji -> switchToEmoji()

            // lang => sym1
            // sym2 => sym1
            R.id.key_123, R.id.key_2_1 -> {
                currentSymbolState = SymbolState.LAYER_1
                updateKeyboardLayout()
            }

            // sym1 => sym2
            R.id.key_1_2 -> {
                currentSymbolState = SymbolState.LAYER_2
                updateKeyboardLayout()
            }

            // sym1, sym2 => lang
            R.id.key_switch_abc -> {
                currentSymbolState = SymbolState.OFF
                updateKeyboardLayout()
            }

            R.id.key_speech -> {
                handleVoiceKey()
            }

            R.id.key_lang -> toggleLanguage()
            R.id.key_enter -> sendKeyAction(KeyEvent.KEYCODE_ENTER)
            else -> {
                if (viewId == R.id.key_unshift) {
                    handleShift()
                } else if (viewId == R.id.key_shift) {
                    handleShift()

                } else {

                    if (currentLanguage == "SHN") {

                        val primaryCode = if (cText.isNotEmpty()) cText.first().code else -1
                        cText = ShanKeyboard.getInstance(this)
                            .handleShanInputText(primaryCode, currentInputConnection)
                    }
                    sendText(cText)


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
        applyTheme(this, currentInputView)
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

    @SuppressLint("RestrictedApi")
    private fun showPopup(anchorView: View, popupCharacters: List<String>) {
        val cardView = CardView(this).apply {
            cardElevation = 20f
            radius = 24f
        }

        val layout = FlowLayout(this).apply {
            setPadding(10, 10, 10, 10)
        }

        popupButtons.clear()

        // တွၼ်ႈတႃႇႁႂ်ႈ ၼိုင်ႈ Row မီး 6 တူဝ်:
        // ႁဝ်းတေၸႂ်ႉ Width ဢၼ်ၵပ်းၸွမ်း Screen Width / 6
        val screenWidth = resources.displayMetrics.widthPixels
        val btnWidth = (screenWidth / 6.5).toInt() // ၵပ်းႁႂ်ႈမၼ်းၵႅပ်ႈ တွၼ်ႈတႃႇ 6 Buttons

        for (char in popupCharacters) {
            val btn = Button(this).apply {
                text = char
                textSize = 18f
                setTextColor(Color.WHITE)
                background = null
                setPadding(0, 0, 0, 0)

                // *** FORCE WIDTH & HEIGHT ***
                layoutParams = LinearLayout.LayoutParams(btnWidth, 140) // Width, Height

                // Remove Android Default Constraints
                minWidth = 0
                minimumWidth = 0
                minHeight = 0
                minimumHeight = 0
            }
            layout.addView(btn)
            popupButtons.add(btn)
        }
        cardView.addView(layout)

        popupWindow = PopupWindow(
            cardView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        ).apply {
            elevation = 30f
            isClippingEnabled = true
        }

        // --- POSITIONING WITH OVERFLOW CHECK ---
        cardView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val pWidth = cardView.measuredWidth
        val pHeight = cardView.measuredHeight

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val keyX = location[0]
        val keyY = location[1]

        // ၵပ်း X Offset
        var xOffset = (anchorView.width - pWidth) / 2
        if (keyX + xOffset + pWidth > screenWidth) xOffset = screenWidth - keyX - pWidth - 10
        if (keyX + xOffset < 0) xOffset = -keyX + 10

        // *** THE Y-AXIS FIX (Check if it goes above screen) ***
        var yOffset = -(anchorView.height + pHeight + 20)

        // သင် (KeyY + yOffset) တႅမ်ႇလိူဝ် 0 ပွင်ႇဝႃႈမၼ်းပူၼ်ႉၸေႃးၽၢႆႇၼိူဝ်
        if (keyY + yOffset < 50) {
            yOffset = -keyY + 50 // Force ႁႂ်ႈမၼ်းယူႇတႅမ်ႇ Status Bar ဢိတ်းၼိုင်ႈ
        }

        applyTheme(this, cardView)

        popupWindow?.showAsDropDown(anchorView, xOffset, yOffset)
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

    // သိမ်း View ၸိူဝ်း Inflate ယဝ်ႉဝႆႉၼႂ်းၼႆႉ
    private val layoutCache = HashMap<Int, View>()
    private fun updateKeyboardLayout() {


        val layoutToLoad = when (currentSymbolState) {
            SymbolState.LAYER_1 -> R.layout.layout_symbols
            SymbolState.LAYER_2 -> R.layout.layout_symbols_2
            SymbolState.OFF -> {
                when (currentLanguage) {
                    "EN" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_en_normal else R.layout.layout_en_shifted
                    "MY" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_my_normal else R.layout.layout_my_shifted
                    "SHN" -> if (currentShiftState == ShiftState.OFF) R.layout.layout_shn_normal else R.layout.layout_shn_shifted
                    else -> R.layout.layout_en_normal
                }
            }
        }

        loadLayout(layoutToLoad) // Function ဢၼ်ႁဝ်းတႅမ်ႈဝႆႉၼႂ်း Lesson 15
    }

    fun loadLayout(layoutId: Int) {
        keysContainer.removeAllViews()

        // 1. ၸႅတ်ႈတူၺ်းဝႃႈ ၼႂ်း Cache မီးယဝ်ႉႁႃႉ?
        var cachedView = layoutCache[layoutId]

        if (cachedView == null) {
            // 2. သင်ပႆႇမီး ၸင်ႇ Inflate (ပွၵ်ႈလဵဝ်ၵွၺ်း)
            cachedView = layoutInflater.inflate(layoutId, null)

            // ၵွင်ႉ Click Listener ဝႆႉၵမ်းလဵဝ်
            registerKeys(cachedView)

            // သိမ်းဝႆႉၼႂ်း Cache
            layoutCache[layoutId] = cachedView
        }

        // 3. ဢဝ် View ဢၼ်မီးဝႆႉယဝ်ႉ ထႅမ်သႂ်ႇ (AddView တေဝႆးလိူဝ် Inflate ၼမ်ၶႃႈ)
        keysContainer.addView(cachedView)

        // 4. မႄးသီ Theme (ဢၼ်ၼႆႉလူဝ်ႇမႄးတႃႇသေႇ ယွၼ်ႉ User ၸၢင်ႈလႅၵ်ႈ Theme)
        applyTheme(this, cachedView)
    }


    fun triggerVibration(view: View) {
        // ၸႅတ်ႈတူၺ်းဝႃႈ ၵူၼ်းၸႂ်ႉပိုတ်ႇ Vibration ဝႆႉၼႂ်း Settings ႁႃႉ?
        val isVibrateEnabled = getVibrateOnKeyPress(this)

        if (isVibrateEnabled) {
            view.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING // ႁႂ်ႈမၼ်းတူင်ႉ ဢမ်ႇဝႃႈ System တေပိၵ်ႉဝႆႉၵေႃႈယဝ်ႉ
            )
        }
    }


    fun playClickSound() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager
        val isSoundEnabled = getSoundOnKeyPress(this)

        if (isSoundEnabled) {
            // ၸႂ်ႉ .1f တွၼ်ႈတႃႇ Volume (မၢင် Version လူဝ်ႇပၼ် Volume Parameter)
            am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1f)
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

    // 1. Declare ဝႆႉၼင်ႇ Lazy ႁႂ်ႈမၼ်းသၢင်ႈမိူဝ်ႈတေၸႂ်ႉတႄႉတႄႉၵွၺ်း
    private val emojiKeyboard by lazy {
        EmojiKeyboard(
            context = this,
            layoutInflater = layoutInflater,
            onEmojiPressed = { emoji -> currentInputConnection?.commitText(emoji, 1) },
            onGoback = { updateKeyboardLayout() },
            onDelete = { currentInputConnection?.deleteSurroundingText(1, 0) },
            onEnter = {
                currentInputConnection?.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER
                    )
                )
            },
            onSpace = { currentInputConnection?.commitText(" ", 1) },
        )
    }

    // 2. မိူဝ်ႈတေလႅၵ်ႈၸႂ်ႉ Emoji
    private fun switchToEmoji() {
        // ၵွၺ်းႁွင်ႉ showIn() မၼ်းတေဢမ်ႇ Inflate XML မႂ်ႇယဝ်ႉ
        emojiKeyboard.showIn(keysContainer)
    }


    private lateinit var speechRecognizer: SpeechRecognizer

    // ၵၢၼ်ပိၵ်ႉ SpeechRecognizer မိူဝ်ႈဢမ်ႇၸႂ်ႉ (Memory Management)
    override fun onDestroy() {
        if (::speechRecognizer.isInitialized) {
            speechRecognizer.destroy()
        }
        super.onDestroy()
    }

    private fun setupVoiceInput() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.e("VoiceInput", "Speech recognition is not available on this device")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                // တီႈၼႆႈ ၸဝ်ႈၵဝ်ႇၸၢင်ႈလႅၵ်ႈသီ Mic Button ပဵၼ်သီလႅင် တွၼ်ႈတႃႇၼႄဝႃႈ "တိုၵ်ႉထွမ်ႇယူႇ"
                Log.d("VoiceInput", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceInput", "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // တီႈၼႆႈ ၸဝ်ႈၵဝ်ႇၸၢင်ႈႁဵတ်း Animation ႁႂ်ႈ Mic တူင်ႉၸွမ်း သဵင်ၼၵ်း/မဝ်
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                // မိူဝ်ႈၵူၼ်းၸႂ်ႉၵတ်းၵႂႃႇ ႁႂ်ႈလႅၵ်ႈသီ Mic ပွၵ်ႈပဵၼ်သီၵဝ်ႇ
                Log.d("VoiceInput", "End of speech")
            }

            override fun onError(error: Int) {
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    else -> "Unknown error"
                }
                Log.e("VoiceInput", errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    // တွၼ်ႈတႃႇ Final Result: သႂ်ႇ Space ၽၢႆႇလင်ဢိတ်းၼိုင်ႈ
                    currentInputConnection?.commitText(matches[0] + " ", 1)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    // ၼႄလိၵ်ႈလႅတ်း (Suggestion Bar) ၸွမ်းၼင်ႇသဵင်ဢၼ်ထွမ်ႇလႆႈၵမ်းလဵဝ်
                    val partialText = matches[0]
                    setSuggestions(listOf(partialText))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }


    private fun handleVoiceKey() {
        // 1. ၸႅတ်ႈတူၺ်း Permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // တမ်း Locale ၸွမ်းၼင်ႇ Language ဢၼ်ၸႂ်ႉယူႇယၢမ်းလဵဝ်
            val langTag = when (currentLanguage) {
                "MY" -> "my-MM"
                "EN" -> "en-US"
                else -> "en-US" // Shan ပႆႇမီး Speech Engine ႁင်းၵူၺ်း
            }

            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }

            startVoiceRecognition(voiceIntent)
        } else {
            // ပႆႇပၼ် Permission -> လၢတ်ႈၼႄၵူၼ်းၸႂ်ႉ သေပိုတ်ႇ Settings
            showPermissionToast()
            openAppSettings()
        }
    }

    private fun startVoiceRecognition(voiceIntent: Intent) {
        try {
            speechRecognizer.startListening(voiceIntent)
            triggerVibration(currentInputView) // Feedback ႁႂ်ႈႁူႉဝႃႈတႄႇယဝ်ႉ
        } catch (e: Exception) {
            Log.e("VoiceInput", "Failed to start: ${e.message}")
        }
    }

    private fun showPermissionToast() {
        Toast.makeText(
            this,
            "ၶႅၼ်းတေႃႈ ပၼ်ၶေႃႈၶႂၢင်ႉ Microphone တွၼ်ႈတႃႇၸႂ်ႉ Voice Typing ၶႃႈ",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private lateinit var clipboard: ClipboardManager
    private val clipHistory = mutableListOf<String>()

    private fun setupClipboard() {
        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // ထတ်းတူၺ်းမိူဝ်ႈၵူၼ်းၸႂ်ႉ Copy လိၵ်ႈ
        clipboard.addPrimaryClipChangedListener {
            val clipData = clipboard.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val text = clipData.getItemAt(0).text.toString()
                if (text.isNotEmpty() && !clipHistory.contains(text)) {
                    // ထႅမ်သႂ်ႇၼႂ်း History (ဢဝ်တမ်းဝႆႉၽၢႆႇၼႃႈသုတ်း)
                    clipHistory.add(0, text)
                    // ၵဵပ်းဝႆႉၵွၺ်း 10-20 ထႅဝ် ၼင်ႇႁိုဝ် Memory တေဢမ်ႇတဵမ်
                    if (clipHistory.size > 20) clipHistory.removeAt(20)
                }
            }
        }
    }

    private fun showClipboardHistory() {
        // ၵွင်ႉၸူး Container ဢၼ်မီးၼႂ်း Suggestion Bar (ဢၼ်ႁဝ်းသၢင်ႈဝႆႉၼႂ်း Lesson 27)
        val container = currentInputView.findViewById<LinearLayout>(R.id.candidate_container)
        container.removeAllViews()

        if (clipHistory.isEmpty()) {
            val tv = TextView(this).apply { text = "Clipboard Empty" }
            container.addView(tv)
            return
        }

        clipHistory.forEach { text ->
            val item = TextView(this).apply {
                this.text = text
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                setPadding(30, 0, 30, 0)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setBackgroundResource(R.drawable.key_background) // ၸႂ်ႉ Background ဢၼ်မီးဝႆႉ

                setOnClickListener {
                    currentInputConnection?.commitText(text, 1)
                }
            }
            container.addView(item)
        }
    }


}