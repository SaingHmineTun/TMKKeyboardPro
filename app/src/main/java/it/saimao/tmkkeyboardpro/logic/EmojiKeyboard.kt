package it.saimao.tmkkeyboardpro.logic

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.saimao.tmkkeyboardpro.R

class EmojiKeyboard(
    val context: Context,
    private val layoutInflater: LayoutInflater,
    private val onEmojiPressed: (String) -> Unit,
    private val onGoback: () -> Unit,
    private val onDelete: () -> Unit,
    private val onEnter: () -> Unit,
    private val onSpace: () -> Unit,
) {

    init {

    }

    // သိမ်း View ဝႆႉၼင်ႇႁိုဝ်တေဢမ်ႇလႆႈ Inflate သွၼ်ႉၵၼ်
    private var emojiView: View? = null


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

    private val peopleList = listOf(
        // Faces (People)
        "👶", "👧", "🧒", "👦", "👩", "🧑", "👨", "👩‍🦱", "👨‍🦱", "👩‍🦰",
        "👨‍🦰", "👱‍♀️", "👱‍♂️", "👩‍🦳", "👨‍脫", "👩‍ bald", "👨‍ bald", "👵", "🧓", "👴",
        "👲", "👳‍♀️", "👳‍♂️", "🧕", "👮‍♀️", "👮‍♂️", "👷‍♀️", "👷‍♂️", "💂‍♀️", "💂‍♂️",

        // Hands & Body (Gestures)
        "👋", "🤚", "🖐", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
        "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
        "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
        "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦵", "🦿", "🦶", "👂",
        "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀", "👁", "👅", "👄"
    )

    private val symbolList = listOf(
        // Hearts & Emotions
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
        "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟",

        // Geometric & Signs
        "🚫", "❌", "⭕️", "🛑", "⛔️", "📛", "💢", "♨️", "🚷", "🚯",
        "⚠️", "📢", "🔔", "🔕", "🎼", "🎵", "🎶", "⚛️", "🕉", "✝️",
        "☸️", "☪️", "🔯", "🕎", "☯️", "☦️", "🛐", "⛎", "♈️", "♉️",

        // Math & Numbers
        "➕", "➖", "➗", "✖️", "♾", "💲", "💱", "™️", "©️", "®️",
        "〰️", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "✔️", "☑️",
        "🔘", "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⚫️", "⚪️", "🟤",

        // Arrows & Directions
        "⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️", "↕️", "↔️",
        "↩️", "↪️", "⤴️", "⤵️", "🔀", "🔁", "🔂", "🔄", "🔃"
    )


    // Categorized Emojis
    private val categories = listOf(
        "Recent" to getRecentEmojis(),
        "Smiley" to smileyList,
        "Nature" to natureList,
        "People" to peopleList, // ထႅမ်သႂ်ႇထႅင်ႈၶႃႈ
        "Symbols" to symbolList
    )

    private fun initView(): View {
        if (emojiView == null) {

            // 1. သၢင်ႈ Context ဢၼ်မီး Theme (ၸႂ်ႉ Theme.Material3 ဢမ်ႇၼၼ် Theme.AppCompat)
            val contextThemeWrapper = ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_Material3_DayNight
            )

            // 2. ၸႂ်ႉ LayoutInflater ဢၼ်လုၵ်ႉတီႈ Wrapper ၼၼ်ႉမႃး Inflate
            val themedInflater = layoutInflater.cloneInContext(contextThemeWrapper)

            // 3. Inflate Layout လူၺ်ႈၸႂ်ႉ themedInflater
            emojiView = themedInflater.inflate(R.layout.emoji_picker, null)

            val viewPager = emojiView!!.findViewById<ViewPager2>(R.id.emoji_viewpager)
            val tabLayout = emojiView!!.findViewById<TabLayout>(R.id.emoji_tabs)

            // ႁႃတၢင်းသုင် key_height လုၵ်ႉတီႈ dimens.xml
            val keyHeight = context.resources.getDimensionPixelSize(R.dimen.key_height)
            val padding = context.resources.getDimensionPixelSize(R.dimen.keyboard_padding)

            // Total Height = (5 Rows * keyHeight) + (2 * Padding)
            val totalHeight = (keyHeight * 5) + (padding * 2)

            // Force တၢင်းသုင် Layout ႁႂ်ႈမိူၼ် Keyboard တႅတ်ႈတေႃး
            emojiView!!.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                totalHeight
            )

            // Setup ViewPager Adapter
            viewPager.adapter = object : RecyclerView.Adapter<EmojiPageViewHolder>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): EmojiPageViewHolder {
                    val grid = GridView(context).apply {
                        numColumns = 8
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                    return EmojiPageViewHolder(grid)
                }

                override fun onBindViewHolder(holder: EmojiPageViewHolder, position: Int) {
                    val list = categories[position].second
                    holder.grid.adapter = EmojiAdapter(context, list) { emoji ->
                        onEmojiPressed(emoji)
                        saveToRecentEmojis(emoji)
                    }
                }

                override fun getItemCount(): Int = categories.size
            }

            // Connect TabLayout with ViewPager2
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = categories[position].first
                // ၸဝ်ႈၵဝ်ႇၸၢင်ႈသႂ်ႇ Icon တႅၼ်း Text လႆႈၶႃႈ: tab.icon = getIcon(position)
            }.attach()

            // Setup Listeners ၵွၺ်းပွၵ်ႈလဵဝ်
            emojiView!!.apply {
                findViewById<Button>(R.id.key_back_to_kb).setOnClickListener {
                    onGoback()
                }
                findViewById<Button>(R.id.key_del).setOnClickListener {
                    onDelete()
                }
                findViewById<Button>(R.id.key_enter).setOnClickListener {
                    onEnter()
                }
                findViewById<Button>(R.id.key_emoji_space).setOnClickListener { onSpace() }
            }
        }
        ThemeManager.applyTheme(context, emojiView!!)
        return emojiView!!
    }


    // Function မႂ်ႇတွၼ်ႈတႃႇသူင်ႇ View ၵႂႃႇၼႄ
    fun showIn(container: FrameLayout) {
        container.removeAllViews()
        container.addView(initView())
    }

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

    class EmojiPageViewHolder(val grid: GridView) : RecyclerView.ViewHolder(grid)


    class EmojiAdapter(
        private val context: Context,
        private val emojis: List<String>,
        val onClick: (String) -> Unit
    ) : BaseAdapter() {
        private val inflater = LayoutInflater.from(context)

        override fun getCount(): Int = emojis.size
        override fun getItem(position: Int) = emojis[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: inflater.inflate(R.layout.item_emoji, parent, false)
            val button = view.findViewById<Button>(R.id.emoji_button)

            button.text = emojis[position]

            // --- Apply Theme ၸူး Button ၵမ်းလဵဝ် ---
            ThemeManager.applyTheme(context, view)

            button.setOnClickListener { onClick(emojis[position]) }

            return view
        }
    }


}