package it.saimao.tmkkeyboardpro.logic

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridView
import androidx.core.content.edit
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.adapters.EmojiAdapter

class EmojiKeyboard(
    val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onPressed: (String) -> Unit,
    private val onGoback: () -> Unit
) {

    // သိမ်း View ဝႆႉၼင်ႇႁိုဝ်တေဢမ်ႇလႆႈ Inflate သွၼ်ႉၵၼ်
    private var emojiView: View? = null
    private lateinit var grid: GridView
    private val smileyList = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
        "🙂", "🙃", "😉", "😍", "🥰", "😘", "😗", "😙", "😚", "😋",
        "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "🥳",
        "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖",
        "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "🤯",
        "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔"
    )
    private val natureList = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐻‍❄️", "🐨",
        "🐯", "🦁", "🐮", "🐷", "🐽", "🐸", "🐵", "🙊", "🙉", "🙈",
        "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉",
        "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞",
        "🐜", "🦟", "🦗", "🕷", "🕸", "🦂", "🐢", "🐍", "🦎", "🦖",
        "🦕", "🐙", "🦑", "🦐", "🦞", "🦀", "🐡", "🐠", "🐟", "🐬"
    )

    private fun initView(): View {
        if (emojiView == null) {
            emojiView = layoutInflater.inflate(R.layout.emoji_picker, null)
            grid = emojiView!!.findViewById(R.id.emoji_grid)

            // Setup Listeners ၵွၺ်းပွၵ်ႈလဵဝ်
            emojiView!!.apply {
                findViewById<Button>(R.id.btn_emoji_smiley).setOnClickListener { updateGrid(smileyList) }
                findViewById<Button>(R.id.btn_emoji_nature).setOnClickListener { updateGrid(natureList) }
                findViewById<Button>(R.id.btn_emoji_recent).setOnClickListener { updateGrid(getRecentEmojis()) }
                findViewById<Button>(R.id.btn_emoji_back).setOnClickListener { onGoback() }
            }
        }
        // ၵူႈပွၵ်ႈဢၼ်ပိုတ်ႇ ႁႂ်ႈမၼ်းၼႄ Smileys ဢွၼ်တၢင်း
        updateGrid(smileyList)
        return emojiView!!
    }

    private fun updateGrid(list: List<String>) {
        grid.adapter = EmojiAdapter(context, list) { emoji ->
            onPressed(emoji)
            saveToRecentEmojis(emoji)
        }
    }

    // Function မႂ်ႇတွၼ်ႈတႃႇသူင်ႇ View ၵႂႃႇၼႄ
    fun showIn(container: FrameLayout) {
        container.removeAllViews()
        container.addView(initView())
    }

//    init {
//        keysContainer.removeAllViews()
//        val emojiView = layoutInflater.inflate(R.layout.emoji_picker, null)
//        keysContainer.addView(emojiView)
//
//
//        val grid: GridView = emojiView.findViewById(R.id.emoji_grid)
//
//
//
//        // Helper function တႃႇလႅၵ်ႈ Emoji ၼႂ်း Grid
//        fun updateGrid(list: List<String>) {
//            grid.adapter = EmojiAdapter(context, list) { emoji ->
//                onPressed(emoji)
//                saveToRecentEmojis(emoji)
//            }
//        }
//
//        // 2. Default: ၼႄ Smileys မိူဝ်ႈတႄႇပိုတ်ႇ
//        updateGrid(smileyList)
//
//        // 3. ၵွင်ႉ Click Listener တွၼ်ႈတႃႇ Tabs ၽၢႆႇတႂ်ႈ
//        emojiView.findViewById<Button>(R.id.btn_emoji_smiley).setOnClickListener {
//            updateGrid(smileyList)
//        }
//
//        emojiView.findViewById<Button>(R.id.btn_emoji_nature).setOnClickListener {
//            updateGrid(natureList)
//        }
//
//        // ၵွင်ႉ Click Listener တွၼ်ႈတႃႇတုမ်ႇ Recent
//        emojiView.findViewById<Button>(R.id.btn_emoji_recent).setOnClickListener {
//            updateGrid(getRecentEmojis()) // လူတ်ႇ Data မႂ်ႇတႃႇသေႇ
//        }
//
//        // တုမ်ႇပွၵ်ႈၶိုၼ်းၼႃႈ Keyboard ယူႇယူႇ
//        emojiView.findViewById<Button>(R.id.btn_emoji_back).setOnClickListener {
//            onGoback()
//        }
//
//    }

    private fun saveToRecentEmojis(emoji: String) {
        val prefs = context.getSharedPreferences("EmojiPrefs", MODE_PRIVATE)
        val recentString = prefs.getString("recent_emojis", "") ?: ""

        // 1. တႅၵ်ႇဢဝ် List ၵဝ်ႇမႃး
        val recentList = recentString.split(",").filter { it.isNotEmpty() }.toMutableList()

        // 2. သင်မီးဝႆႉယဝ်ႉ ႁႂ်ႈထွၼ်ဢွၵ်ႇၵွၼ်ႇ (တႃႇဢဝ်မႃးတမ်းၽၢႆႇၼႃႈသုတ်း)
        recentList.remove(emoji)
        recentList.add(0, emoji)

        // 3. ၵဵပ်းဝႆႉၵွၺ်း 20 တူဝ် (ဢမ်ႇၼၼ် ၸွမ်းၼင်ႇမၵ်ႉမၼ်ႈဝႆႉ)
        val updatedList = recentList.take(20)

        // 4. Save ၶိုၼ်းၼႂ်း SharedPreferences
        prefs.edit { putString("recent_emojis", updatedList.joinToString(",")) }
    }


    private fun getRecentEmojis(): List<String> {
        val prefs = context.getSharedPreferences("EmojiPrefs", MODE_PRIVATE)
        val recentString = prefs.getString("recent_emojis", "") ?: ""
        return recentString.split(",").filter { it.isNotEmpty() }
    }

}