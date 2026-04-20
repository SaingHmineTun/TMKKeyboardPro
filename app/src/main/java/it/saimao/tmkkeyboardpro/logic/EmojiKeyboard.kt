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
import androidx.recyclerview.widget.GridLayoutManager
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
    private lateinit var emojiView: View


    private val smileyList by lazy {
        listOf(
            // --- 1. GRINNING & HAPPY ---
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇",
            "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚",
            "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "🥳",

            // --- 2. AFFECTION & LOVE ---
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "💌",

            // --- 3. NEUTRAL & SKEPTICAL ---
            "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖",
            "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "🤯",
            "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔",
            "🤭", "🤫", "🤥", "😶", "😶‍🌫️", "😐", "😑", "😬", "🙄", "😯",
            "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "😵‍💫",
            "🤐", "🥴", "🤢", "🤮", "🤧", "😷", "🤒", "🤕", "🤑", "🤠",

            // --- 4. HANDS & GESTURES ---
            "👋", "🤚", "🖐️", "✋", "🖖", "👌", "🤌", "🤏", "✌️", "🤞",
            "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "👍",
            "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "👐", "🤲", "🤝",
            "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦵", "🦿", "🦶", "👂",

            // --- 5. MONKEYS & CATS ---
            "🙈", "🙉", "🙊", "🐵", "🐒", "🦍", "🦧", "🐶", "🐕", "🦮",
            "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",
        )
    }
    private val natureList by lazy {
        listOf(
            // --- DOMESTIC & FARM ANIMALS ---
            "🐶", "🐕", "🦮", "🐕‍🦺", "🐩", "🐱", "🐈", "🐈‍⬛", "🐭", "🐁",
            "🐀", "🐹", "🐰", "🐇", "🐻", "🐻‍❄️", "🐨", "🐼", "🐸", "🐒",
            "🐵", "🐔", "🐓", "🐣", "🐤", "🐥", "🐦", "🐧", "🕊️", "🦅",
            "🦆", "🦢", "🦉", "🦤", "🐦‍⬛", "🐦‍🔥", "🪶", "🐺", "🦊", "🦝",
            "🐗", "🐴", "🦄", "🦓", "🦌", "🐮", "🐄", "🐂", "🐃", "🐏",
            "🐑", "🐐", "🐪", "🐫", "🦙", "🦒", "🐘", "🦣", "🦏", "🦛",
            "🐭", "🐹", "🐰", "🦫", "🦔", "🦇", "🐻", "🐻‍❄️", "🐨", "🐼",

            // --- WILD ANIMALS ---
            "🦥", "🦦", "🦨", "🦘", "🦡", "🐾", "🦃", "🐓", "🐣", "🐤",
            "🐥", "🐦", "🐧", "🕊️", "🦅", "🦆", "🦢", "🦉", "🦤", "🦩",
            "🦚", "🦜", "🐸", "🐊", "🐢", "🦎", "🐍", "🐲", "🐉", "🦕",
            "🦖", "🐳", "🐋", "🐬", "🦭", "🐟", "🐠", "🐡", "🦈", "🐙",
            "🦑", "🦐", "🦞", "🦀", "🐚", "🐌", "🦋", "🐛", "🐜", "🪰",
            "🪲", "🪳", "🐝", "🪷", "🐞", "🦗", "🪳", "🕷️", "🕸️", "🦂",

            // --- CATS (from smiley list) ---
            "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾",

            // --- MONKEYS (from smiley list) ---
            "🙈", "🙉", "🙊",

            // --- PLANTS & FLOWERS ---
            "🌸", "💐", "🌸", "🌼", "🌻", "🌺", "🪻", "🌹", "🥀", "🪷",
            "🌿", "🍀", "🍁", "🍂", "🍃", "🌱", "🌲", "🌳", "🌴", "🪵",
            "🌵", "🌾", "🌽", "🍄", "🪸", "🪨",

            // --- WEATHER & SKY ---
            "☀️", "☁️", "⛅", "🌤️", "🌥️", "🌦️", "🌧️", "⛈️", "🌨️", "❄️",
            "☃️", "⛄", "🌬️", "💨", "🌪️", "🌈", "☔", "💧", "💦", "☄️",
            "✨", "⭐", "🌟", "🌠", "🪐", "🌞", "🌝", "🌛", "🌜", "🌚",
            "🌕", "🌖", "🌗", "🌘", "🌑", "🌒", "🌓", "🌔", "🌙", "🌎",
            "🌍", "🌏", "🪺", "🪹",

            // --- NATURE SCENES ---
            "🏔️", "⛰️", "🌋", "🗻", "🏕️", "🏖️", "🏜️", "🏝️", "🏞️", "🏟️",
            "🌅", "🌄", "🌇", "🌆", "🏙️", "🌃", "🌌", "🎑", "🗾",
        )
    }

    private val places by lazy {
        listOf(
            // --- BUILDINGS & HOUSING ---
            "🏠", "🏡", "🏘️", "🏚️", "🏗️", "🏢", "🏬", "🏣", "🏤", "🏥",
            "🏦", "🏨", "🏩", "🏪", "🏫", "🏛️", "💒", "⛪", "🕌", "🕍",
            "🕋", "⛩️", "🏟️", "🎡", "🎢", "🎠", "⛲", "⛱️", "🏖️", "🏝️",
            "🏜️", "⛰️", "🏔️", "🗻", "🌋", "🛖", "⛺", "🏕️", "🛕", "🏯",
            "🏰", "🗼", "🗽", "🏛️", "🕋", "🛐", "☸️", "🕉️", "✡️", "🔯",
            "🛗", "🚪", "🪞", "🪟", "🛏️", "🛋️", "🚽", "🚿", "🛁", "🧴",

            // --- CITY & NATURE SCENERY ---
            "🌅", "🌄", "🌇", "🌆", "🏙️", "🌃", "🌌", "🎑", "🗾", "🏞️",
            "🌁", "🏝️", "🏖️", "🏜️", "⛰️", "🏔️", "🗻", "🌋", "⛲", "🏞️",

            // --- GROUND TRANSPORTATION ---
            "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐",
            "🛻", "🚚", "🚛", "🚜", "🛵", "🏍️", "🚲", "🛴", "🛹", "🛼",
            "🚂", "🚃", "🚄", "🚅", "🚆", "🚇", "🚈", "🚉", "🚊", "🚝",
            "🚞", "🚋", "🚟", "🚠", "🚡", "🚘", "🚖", "🚔", "🚍", "🚡",
            "🦽", "🦼", "🛺", "🚒", "🚑", "🚓", "🚕", "🚙", "🚌", "🚎",

            // --- AIR & SPACE TRAVEL ---
            "🚁", "✈️", "🛩️", "🛫", "🛬", "🪂", "💺", "🚀", "🛸", "🛰️",
            "🛸", "🚀", "🛰️", "🛸", "☄️", "🌠", "🪐", "🌌",

            // --- WATER TRANSPORTATION ---
            "⛵", "🛶", "🚤", "🚢", "🛳️", "⛴️", "🛥️", "🚣", "🏄", "🛟",
            "⚓", "🚏", "🚦", "🚥", "🗺️", "🧭", "📍", "📌", "🚧", "🚨",

            // --- MAPS & GEOGRAPHY ---
            "🧭", "🗺️", "🌍", "🌎", "🌏", "🗾", "📍", "📌", "🚩", "🏁",
            "🎌", "🏳️", "🏴", "🏳️‍🌈", "🏳️‍⚧️", "🏴‍☠️",

            // --- WORLD FLAGS (more complete set) ---
            "🇲🇲", "🇹🇭", "🇱🇦", "🇰🇭", "🇻🇳", "🇸🇬", "🇲🇾", "🇵🇭", "🇮🇩", "🇧🇳",
            "🇯🇵", "🇰🇷", "🇨🇳", "🇮🇳", "🇺🇸", "🇬🇧", "🇫🇷", "🇩🇪", "🇮🇹", "🇷🇺",
            "🇦🇺", "🇨🇦", "🇧🇷", "🇲🇽", "🇪🇸", "🇸🇦", "🇹🇷", "🇿🇦", "🇪🇬", "🇳🇬",
            "🇦🇪", "🇶🇦", "🇰🇼", "🇴🇲", "🇧🇭", "🇯🇴", "🇱🇧", "🇮🇱", "🇵🇸", "🇾🇪",
            "🇦🇫", "🇵🇰", "🇧🇩", "🇱🇰", "🇳🇵", "🇧🇹", "🇲🇻", "🇰🇿", "🇺🇿", "🇹🇲",
            "🇰🇬", "🇹🇯", "🇲🇳", "🇬🇪", "🇦🇲", "🇦🇿", "🇺🇦", "🇵🇱", "🇨🇿", "🇸🇰",
            "🇭🇺", "🇷🇴", "🇧🇬", "🇬🇷", "🇦🇱", "🇲🇰", "🇷🇸", "🇲🇪", "🇧🇦", "🇭🇷",
            "🇸🇮", "🇦🇹", "🇨🇭", "🇱🇮", "🇳🇱", "🇧🇪", "🇱🇺", "🇲🇨", "🇫🇷", "🇦🇩",
            "🇵🇹", "🇪🇸", "🇬🇮", "🇮🇪", "🇮🇸", "🇳🇴", "🇸🇪", "🇫🇮", "🇩🇰", "🇪🇪",
            "🇱🇻", "🇱🇹", "🇧🇾", "🇲🇩", "🇬🇪", "🇦🇲", "🇦🇿", "🇨🇾", "🇲🇹", "🇮🇹",
            "🇻🇦", "🇸🇲", "🇫🇷", "🇲🇨", "🇦🇩", "🇪🇸", "🇵🇹", "🇬🇷", "🇲🇰", "🇧🇬",
            "🇷🇴", "🇭🇺", "🇦🇹", "🇨🇿", "🇸🇰", "🇵🇱", "🇨🇭", "🇱🇮", "🇩🇪", "🇱🇺",
            "🇧🇪", "🇳🇱", "🇬🇧", "🇮🇪", "🇮🇸", "🇩🇰", "🇳🇴", "🇸🇪", "🇫🇮", "🇪🇪",
            "🇱🇻", "🇱🇹", "🇧🇾", "🇺🇦", "🇲🇩", "🇬🇪", "🇦🇲", "🇦🇿", "🇰🇿", "🇺🇿",
            "🇹🇲", "🇰🇬", "🇹🇯", "🇲🇳", "🇨🇳", "🇹🇼", "🇭🇰", "🇲🇴", "🇰🇵", "🇰🇷",
            "🇯🇵", "🇲🇲", "🇹🇭", "🇱🇦", "🇰🇭", "🇻🇳", "🇲🇾", "🇸🇬", "🇧🇳", "🇮🇩",
            "🇵🇭", "🇹🇱", "🇦🇺", "🇳🇿", "🇵🇬", "🇫🇯", "🇸🇧", "🇻🇺", "🇼🇸", "🇰🇮",
            "🇹🇴", "🇫🇲", "🇲🇭", "🇵🇼", "🇳🇷", "🇹🇻", "🇨🇦", "🇺🇸", "🇲🇽", "🇬🇹",
            "🇧🇿", "🇭🇳", "🇸🇻", "🇳🇮", "🇨🇷", "🇵🇦", "🇨🇺", "🇯🇲", "🇭🇹", "🇩🇴",
            "🇵🇷", "🇧🇸", "🇹🇹", "🇧🇧", "🇱🇨", "🇻🇨", "🇬🇩", "🇰🇳", "🇦🇬", "🇩🇲",
            "🇧🇴", "🇨🇴", "🇪🇨", "🇵🇪", "🇧🇷", "🇨🇱", "🇦🇷", "🇺🇾", "🇵🇾", "🇻🇪",
            "🇬🇾", "🇸🇷", "🇬🇫", "🇫🇰", "🇦🇷", "🇨🇱", "🇵🇪", "🇧🇴", "🇨🇴", "🇻🇪",
            "🇪🇬", "🇱🇾", "🇹🇳", "🇩🇿", "🇲🇦", "🇲🇷", "🇸🇳", "🇬🇲", "🇬🇳", "🇬🇼",
            "🇲🇱", "🇧🇫", "🇳🇪", "🇹🇩", "🇸🇩", "🇪🇷", "🇩🇯", "🇸🇴", "🇪🇹", "🇰🇪",
            "🇺🇬", "🇷🇼", "🇧🇮", "🇹🇿", "🇲🇼", "🇿🇲", "🇿🇼", "🇲🇿", "🇦🇴", "🇳🇦",
            "🇧🇼", "🇿🇦", "🇸🇿", "🇱🇸", "🇲🇬", "🇰🇲", "🇸🇨", "🇲🇺", "🇨🇻", "🇸🇹",
            "🇬🇶", "🇨🇫", "🇨🇬", "🇩🇷", "🇨🇩", "🇷🇼", "🇧🇮", "🇺🇬", "🇰🇪", "🇹🇿",
            "🇦🇴", "🇿🇲", "🇲🇼", "🇲🇿", "🇳🇦", "🇧🇼", "🇿🇦", "🇸🇿", "🇱🇸", "🇲🇬",
        )
    }

// Note: Some regional duplicates exist across flag lists (e.g., France, Spain, etc.)
// because they appear in multiple regions. Remove manually if you want absolute uniqueness.    }

    private val foodList by lazy {
        listOf(
            // --- FRUITS ---
            "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍈",
            "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🫒",
            "🥥", "🍈", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍒",
            "🍑", "🥭", "🍍", "🥥", "🥝",

            // --- VEGETABLES ---
            "🥦", "🥬", "🥒", "🌶️", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠",
            "🫘", "🥜", "🌰", "🍄", "🥦", "🥬", "🥒", "🌶️", "🌽", "🥕",
            "🧄", "🧅", "🥔", "🍠", "🫑", "🥗",

            // --- BAKERY & GRAINS ---
            "🍞", "🥐", "🥖", "🥨", "🥯", "🥞", "🧇", "🍪", "🎂", "🍰",
            "🧁", "🥧", "🍩", "🍪", "🥨", "🥖", "🥐", "🍞", "🥯", "🥞",
            "🧇", "🍕", "🍔", "🌭", "🥪", "🌮", "🌯", "🥙", "🧆", "🥚",
            "🍳", "🧈", "🥓", "🥩", "🍗", "🍖", "🍤", "🍣", "🍱", "🍘",
            "🍙", "🍚", "🍛", "🍜", "🍝", "🍠", "🍢", "🍥", "🥮", "🍡",
            "🥟", "🥠", "🥡",

            // --- DAIRY & CONDIMENTS ---
            "🧀", "🧈", "🥛", "🍼", "☕", "🍵", "🧃", "🥤", "🧋", "🍶",
            "🍺", "🍻", "🥂", "🥃", "🫗", "🧂", "🥄", "🍽️", "🍴", "🥢",
            "🔪", "🏺", "🧊",

            // --- FAST FOOD & DISHES ---
            "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯", "🥙", "🧆", "🥚",
            "🍳", "🥘", "🍲", "🥣", "🥗", "🍿", "🧈", "🧂", "🍱", "🍘",
            "🍙", "🍚", "🍛", "🍜", "🍝", "🍠", "🍢", "🍣", "🍤", "🍥",
            "🥮", "🍡", "🥟", "🥠", "🥡",

            // --- DESSERTS & SWEETS ---
            "🍦", "🍧", "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫",
            "🍬", "🍭", "🍮", "🍯", "🍬", "🍫", "🍭", "🍮", "🍯", "🍩",
            "🍦", "🍧", "🍨", "🥮", "🍡", "🧁", "🥧", "🍰", "🎂", "🍪",

            // --- DRINKS ---
            "🍼", "🥛", "☕", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺",
            "🍻", "🥂", "🥃", "🧃", "🥤", "🧋", "🫗", "🧉", "🧊", "🥛",
            "☕", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂",
            "🥃",

            // --- UTENSILS & KITCHEN ---
            "🍽️", "🍴", "🥄", "🔪", "🏺", "🥢", "🧂", "🧈", "🧊", "🥄",
            "🍴", "🍽️", "🔪", "🏺", "🥢",
        )
    }

    private val objectList by lazy {
        listOf(
            // --- SPORTS & ACTIVITIES ---
            "⚽", "🏀", "🏈", "⚾", "🥎", "🎾", "🏐", "🏉", "🥏", "🎱",
            "🏓", "🏸", "🏒", "🏑", "🥍", "🏏", "🪃", "🥅", "⛳", "🪁",
            "🏹", "🎣", "🤿", "🥊", "🥋", "⛸️", "🎿", "🛷", "🥌", "🎯",
            "🎳", "🏂", "🏄", "🚣", "🏊", "🧗", "🚴", "🚵", "🤸", "🤼",
            "🤽", "🤾", "🤺", "⛹️", "🏌️", "🏇", "🧘", "🪢", "🎽", "🥅",

            // --- MUSIC & ENTERTAINMENT ---
            "🎹", "🎸", "🎻", "🎺", "🎷", "🥁", "🪘", "🎤", "🎧", "🎼",
            "🎵", "🎶", "🎙️", "🎚️", "🎛️", "🎚️", "🎛️", "📻", "🎬", "🎮",
            "🕹️", "🎲", "♟️", "🎭", "🎨", "🖌️", "🖍️", "🖼️", "🎞️", "📽️",

            // --- ELECTRONICS & TECH ---
            "⌚", "📱", "📲", "💻", "⌨️", "🖱️", "🖨️", "🖥️", "🖨️", "📷",
            "📸", "📹", "📼", "🔍", "🔎", "💡", "🔦", "🏮", "🪔", "📺",
            "📻", "🕹️", "🎮", "📟", "☎️", "📞", "📠", "⏰", "⏲️", "⏱️",
            "🧭", "📡", "🔋", "🪫", "🔌", "💾", "💿", "📀", "🧲", "💽",

            // --- OFFICE & SCHOOL SUPPLIES ---
            "📔", "📕", "📖", "📗", "📘", "📙", "📚", "📓", "📒", "📃",
            "📜", "📄", "📰", "🗞️", "📑", "🔖", "🏷️", "💰", "🪙", "💴",
            "💵", "💶", "💷", "💸", "💳", "🧾", "✉️", "📧", "📨", "📩",
            "📤", "📥", "📦", "📫", "📪", "📬", "📭", "✏️", "✒️", "🖋️",
            "🖊️", "🖍️", "🖌️", "📝", "📏", "📐", "✂️", "📎", "🖇️", "📌",
            "📍", "📆", "📅", "🗓️", "📈", "📉", "📊", "🗂️", "📁", "📂",
            "🗃️", "🗄️", "🖨️", "⌨️", "🖱️", "🖥️",

            // --- HOUSEHOLD & FURNITURE ---
            "🏠", "🏡", "🏢", "🏪", "🏫", "🏩", "💒", "🏛️", "⛪", "🕌",
            "🕍", "🕋", "⛩️", "🏟️", "🎡", "🎢", "🎠", "⛲", "⛱️", "🏖️",
            "🏝️", "🏜️", "⛰️", "🏔️", "🗻", "🌋", "🛖", "⛺", "🛕", "🏯",
            "🏰", "🗼", "🗽", "🛐", "🛗", "🚪", "🪞", "🪟", "🛏️", "🛋️",
            "🚽", "🚿", "🛁", "🧴", "🪥", "🪒", "🧹", "🧺", "🧻", "🚰",

            // --- CLOTHING & ACCESSORIES ---
            "👕", "👖", "🧣", "🧤", "🧥", "🧦", "👗", "👘", "🥻", "🩱",
            "🩲", "🩳", "👙", "👚", "👛", "👜", "👝", "🎒", "👞", "👟",
            "🥾", "🥿", "👠", "👡", "👢", "👑", "👒", "🎩", "🎓", "🧢",
            "⛑️", "📿", "💍", "💎", "🧣", "🧤", "🧥", "🧦", "🧵", "🧶",
            "👓", "🕶️", "🥽", "👔",

            // --- TOOLS & CONSTRUCTION ---
            "🔧", "🔨", "⚒️", "🛠️", "⛏️", "🔩", "⚙️", "🧰", "🧲", "🔫",
            "🧨", "💣", "🔪", "🗡️", "⚔️", "🛡️", "🚬", "⚰️", "🪦", "⚱️",
            "🔮", "🪄", "🧿", "💈", "🛎️", "🧯", "🔑", "🗝️", "🧸", "🪆",

            // --- VEHICLE PARTS & ROAD ITEMS ---
            "🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐",
            "🛻", "🚚", "🚛", "🚜", "🛵", "🏍️", "🚲", "🛴", "🛹", "🛼",
            "🚂", "🚃", "🚄", "🚅", "🚆", "🚇", "🚈", "🚉", "🚊", "🚝",
            "🚞", "🚋", "🚟", "🚠", "🚡", "🚁", "✈️", "🛩️", "🛫", "🛬",
            "🪂", "💺", "🚀", "🛸", "🛰️", "⚓", "⛵", "🛶", "🚤", "🚢",
            "🛳️", "⛴️", "🛥️", "🚏", "🚦", "🚥", "🗺️", "🧭", "📍", "🚧",
            "🚨", "⛽", "🅿️", "♿", "🛗", "🪝", "🛞",

            // --- SYMBOLS & MISCELLANEOUS ---
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "💌",
            "⭐", "🌟", "✨", "⚡", "🔥", "💧", "💨", "💥", "💫", "💦",
            "💤", "💢", "💣", "💬", "💭", "🗯️", "♠️", "♥️", "♦️", "♣️",
            "🃏", "🎴", "🀄", "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⚫",
            "⚪", "🟤", "🔘", "🛑", "⭕", "❌", "❎", "✅", "✔️", "➗",
            "✖️", "➕", "➖", "🔢", "🔣", "🔠", "🔡", "🔤", "🅰️", "🅱️",
            "🆎", "🅾️", "💠", "♻️", "⚜️", "🔱", "📛", "🔰", "✅", "❌",
        )
    }
    private val peopleList by lazy {
        listOf(
            // --- BABIES & CHILDREN ---
            "👶", "👶🏻", "👶🏼", "👶🏽", "👶🏾", "👶🏿", "🧒", "🧒🏻", "🧒🏼", "🧒🏽",
            "🧒🏾", "🧒🏿", "👦", "👦🏻", "👦🏼", "👦🏽", "👦🏾", "👦🏿", "👧", "👧🏻",
            "👧🏼", "👧🏽", "👧🏾", "👧🏿",

            // --- ADULTS (GENDER NEUTRAL, MALE, FEMALE) ---
            "🧑", "🧑🏻", "🧑🏼", "🧑🏽", "🧑🏾", "🧑🏿", "👨", "👨🏻", "👨🏼", "👨🏽",
            "👨🏾", "👨🏿", "👩", "👩🏻", "👩🏼", "👩🏽", "👩🏾", "👩🏿",

            // --- HAIR STYLES ---
            "👩‍🦱", "👨‍🦱", "👩‍🦰", "👨‍🦰", "👩‍🦳", "👨‍🦳", "👩‍🦲", "👨‍🦲", "🧑‍🦱", "🧑‍🦰",
            "🧑‍🦳", "🧑‍🦲", "👱", "👱🏻", "👱🏼", "👱🏽", "👱🏾", "👱🏿", "👱‍♀️", "👱‍♂️",

            // --- ELDERLY ---
            "👵", "👵🏻", "👵🏼", "👵🏽", "👵🏾", "👵🏿", "👴", "👴🏻", "👴🏼", "👴🏽",
            "👴🏾", "👴🏿", "🧓", "🧓🏻", "🧓🏼", "🧓🏽", "🧓🏾", "🧓🏿",

            // --- PROFESSIONALS & ROLES ---
            "👲", "👲🏻", "👲🏼", "👲🏽", "👲🏾", "👲🏿", "🧕", "🧕🏻", "🧕🏼", "🧕🏽",
            "🧕🏾", "🧕🏿", "👳", "👳🏻", "👳🏼", "👳🏽", "👳🏾", "👳🏿", "👳‍♀️", "👳‍♂️",
            "👮", "👮🏻", "👮🏼", "👮🏽", "👮🏾", "👮🏿", "👮‍♀️", "👮‍♂️", "👷", "👷🏻",
            "👷🏼", "👷🏽", "👷🏾", "👷🏿", "👷‍♀️", "👷‍♂️", "💂", "💂🏻", "💂🏼", "💂🏽",
            "💂🏾", "💂🏿", "💂‍♀️", "💂‍♂️", "🕵️", "🕵️‍♀️", "🕵️‍♂️", "👩‍⚕️", "👨‍⚕️", "👩‍🎓",
            "👨‍🎓", "👩‍🏫", "👨‍🏫", "👩‍⚖️", "👨‍⚖️", "👩‍🌾", "👨‍🌾", "👩‍🍳", "👨‍🍳", "👩‍🔧",
            "👨‍🔧", "👩‍🏭", "👨‍🏭", "👩‍💼", "👨‍💼", "👩‍🔬", "👨‍🔬", "👩‍💻", "👨‍💻", "👩‍🎨",
            "👨‍🎨", "👩‍✈️", "👨‍✈️", "👩‍🚀", "👨‍🚀", "👩‍🚒", "👨‍🚒", "👮‍♀️", "👮‍♂️", "🕵️‍♀️",
            "🕵️‍♂️", "💂‍♀️", "💂‍♂️", "👷‍♀️", "👷‍♂️", "👳‍♀️", "👳‍♂️",

            // --- FANTASY & CHARACTERS ---
            "🧙", "🧙‍♀️", "🧙‍♂️", "🧚", "🧚‍♀️", "🧚‍♂️", "🧛", "🧛‍♀️", "🧛‍♂️", "🧜",
            "🧜‍♀️", "🧜‍♂️", "🧝", "🧝‍♀️", "🧝‍♂️", "🧞", "🧞‍♀️", "🧞‍♂️", "🧟", "🧟‍♀️",
            "🧟‍♂️", "🦸", "🦸‍♀️", "🦸‍♂️", "🦹", "🦹‍♀️", "🦹‍♂️", "🧑‍🎄", "🤶", "🎅",
            "🎅🏻", "🎅🏼", "🎅🏽", "🎅🏾", "🎅🏿", "🤶🏻", "🤶🏼", "🤶🏽", "🤶🏾", "🤶🏿",
            "🧑‍🎄", "🦌", "🧣", "🎄",

            // --- FAMILY & GROUPS ---
            "👨‍👩‍👦", "👨‍👩‍👧", "👨‍👩‍👧‍👦", "👨‍👩‍👦‍👦", "👨‍👩‍👧‍👧", "👩‍👩‍👦", "👩‍👩‍👧", "👩‍👩‍👧‍👦",
            "👩‍👩‍👦‍👦", "👩‍👩‍👧‍👧", "👨‍👨‍👦", "👨‍👨‍👧", "👨‍👨‍👧‍👦", "👨‍👨‍👦‍👦", "👨‍👨‍👧‍👧",
            "👩‍👦", "👩‍👧", "👩‍👧‍👦", "👩‍👦‍👦", "👩‍👧‍👧", "👨‍👦", "👨‍👧", "👨‍👧‍👦",
            "👨‍👦‍👦", "👨‍👧‍👧", "🧑‍🤝‍🧑", "👭", "👫", "👬", "💏", "👩‍❤️‍💋‍👨", "👨‍❤️‍💋‍👨",
            "👩‍❤️‍💋‍👩", "💑", "👩‍❤️‍👨", "👨‍❤️‍👨", "👩‍❤️‍👩", "👪", "👨‍👩‍👦", "👨‍👩‍👧",

            // --- HANDS & GESTURES (all skin tones) ---
            "👋", "👋🏻", "👋🏼", "👋🏽", "👋🏾", "👋🏿", "🤚", "🤚🏻", "🤚🏼", "🤚🏽",
            "🤚🏾", "🤚🏿", "🖐️", "🖐🏻", "🖐🏼", "🖐🏽", "🖐🏾", "🖐🏿", "✋", "✋🏻",
            "✋🏼", "✋🏽", "✋🏾", "✋🏿", "🖖", "🖖🏻", "🖖🏼", "🖖🏽", "🖖🏾", "🖖🏿",
            "👌", "👌🏻", "👌🏼", "👌🏽", "👌🏾", "👌🏿", "🤌", "🤌🏻", "🤌🏼", "🤌🏽",
            "🤌🏾", "🤌🏿", "🤏", "🤏🏻", "🤏🏼", "🤏🏽", "🤏🏾", "🤏🏿", "✌️", "✌🏻",
            "✌🏼", "✌🏽", "✌🏾", "✌🏿", "🤞", "🤞🏻", "🤞🏼", "🤞🏽", "🤞🏾", "🤞🏿",
            "🤟", "🤟🏻", "🤟🏼", "🤟🏽", "🤟🏾", "🤟🏿", "🤘", "🤘🏻", "🤘🏼", "🤘🏽",
            "🤘🏾", "🤘🏿", "🤙", "🤙🏻", "🤙🏼", "🤙🏽", "🤙🏾", "🤙🏿", "👈", "👈🏻",
            "👈🏼", "👈🏽", "👈🏾", "👈🏿", "👉", "👉🏻", "👉🏼", "👉🏽", "👉🏾", "👉🏿",
            "👆", "👆🏻", "👆🏼", "👆🏽", "👆🏾", "👆🏿", "🖕", "🖕🏻", "🖕🏼", "🖕🏽",
            "🖕🏾", "🖕🏿", "👇", "👇🏻", "👇🏼", "👇🏽", "👇🏾", "👇🏿", "☝️", "☝🏻",
            "☝🏼", "☝🏽", "☝🏾", "☝🏿", "👍", "👍🏻", "👍🏼", "👍🏽", "👍🏾", "👍🏿",
            "👎", "👎🏻", "👎🏼", "👎🏽", "👎🏾", "👎🏿", "✊", "✊🏻", "✊🏼", "✊🏽",
            "✊🏾", "✊🏿", "👊", "👊🏻", "👊🏼", "👊🏽", "👊🏾", "👊🏿", "🤛", "🤛🏻",
            "🤛🏼", "🤛🏽", "🤛🏾", "🤛🏿", "🤜", "🤜🏻", "🤜🏼", "🤜🏽", "🤜🏾", "🤜🏿",
            "👏", "👏🏻", "👏🏼", "👏🏽", "👏🏾", "👏🏿", "🙌", "🙌🏻", "🙌🏼", "🙌🏽",
            "🙌🏾", "🙌🏿", "👐", "👐🏻", "👐🏼", "👐🏽", "👐🏾", "👐🏿", "🤲", "🤲🏻",
            "🤲🏼", "🤲🏽", "🤲🏾", "🤲🏿", "🤝", "🙏", "🙏🏻", "🙏🏼", "🙏🏽", "🙏🏾",
            "🙏🏿", "✍️", "✍🏻", "✍🏼", "✍🏽", "✍🏾", "✍🏿", "💅", "💅🏻", "💅🏼", "💅🏽",
            "💅🏾", "💅🏿", "🤳", "🤳🏻", "🤳🏼", "🤳🏽", "🤳🏾", "🤳🏿", "💪", "💪🏻",
            "💪🏼", "💪🏽", "💪🏾", "💪🏿", "🦾", "🦵", "🦵🏻", "🦵🏼", "🦵🏽", "🦵🏾",
            "🦵🏿", "🦿", "🦶", "🦶🏻", "🦶🏼", "🦶🏽", "🦶🏾", "🦶🏿", "👂", "👂🏻",
            "👂🏼", "👂🏽", "👂🏾", "👂🏿", "🦻", "🦻🏻", "🦻🏼", "🦻🏽", "🦻🏾", "🦻🏿",
            "👃", "👃🏻", "👃🏼", "👃🏽", "👃🏾", "👃🏿", "🧠", "🫀", "🫁", "🦷",
            "🦴", "👀", "👁️", "👅", "👄", "🫦", "👤", "👥", "🫂", "🧍",
            "🧍🏻", "🧍🏼", "🧍🏽", "🧍🏾", "🧍🏿", "🧎", "🧎🏻", "🧎🏼", "🧎🏽", "🧎🏾",
            "🧎🏿", "🧑‍🦽", "🧑‍🦼", "🦯", "🦼", "🦽",
        )
    }

    private val symbolList by lazy {
        listOf(
            // --- HEARTS & EMOTIONS ---
            "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎", "💔",
            "❣️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "♥️",
            "❤️‍🔥", "❤️‍🩹", "💌", "💋", "💯", "💢", "💥", "💫", "💦", "💨",
            "🫀", "🫁", "🧠", "🦷", "🦴", "👁️", "👅", "👄", "🫦",

            // --- WARNING & PROHIBITION ---
            "🚫", "❌", "⭕", "🛑", "⛔", "📛", "♨️", "🚷", "🚯", "🚳",
            "🚱", "🚭", "⚠️", "☢️", "☣️", "📢", "🔞", "🚸", "🔇", "🔈",
            "🔉", "🔊", "🔕", "📣", "📯", "🔔", "🔕", "🛎️",

            // --- MUSIC & SOUND ---
            "🎼", "🎵", "🎶", "🎙️", "🎚️", "🎛️", "🎤", "🎧", "📻", "🔊",
            "🔉", "🔈", "🔇", "🎷", "🎸", "🎹", "🎺", "🎻", "🥁", "🪘",

            // --- RELIGION & SPIRITUAL ---
            "⚛️", "🕉️", "✝️", "☦️", "☪️", "🕎", "🔯", "☸️", "✡️", "☯️",
            "🛐", "🕉️", "⚛️", "🕎", "☸️", "✝️", "☦️", "☪️", "✡️", "☯️",
            "🛐", "🕋", "⛩️", "🕍", "⛪", "🕌",

            // --- ZODIAC & ASTROLOGY ---
            "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑",
            "♒", "♓", "⛎", "🔮", "🪐", "⭐", "🌟", "🌠", "☄️", "🌌",

            // --- MATH & NUMBERS ---
            "➕", "➖", "➗", "✖️", "♾️", "💲", "💱", "™️", "©️", "®️",
            "〰️", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "✔️", "☑️",
            "✅", "❎", "🔢", "#️⃣", "*️⃣", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣",
            "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "💯", "🔣", "🔠", "🔡",
            "🔤", "🆎", "🅰️", "🅱️", "🅾️", "🆑", "🆒", "🆓", "🆔", "🆕",
            "🆖", "🆗", "🆘", "🆙", "🆚", "🇦", "🇧", "🇨", "🇩", "🇪",
            "🇫", "🇬", "🇭", "🇮", "🇯", "🇰", "🇱", "🇲", "🇳", "🇴",
            "🇵", "🇶", "🇷", "🇸", "🇹", "🇺", "🇻", "🇼", "🇽", "🇾", "🇿",

            // --- GEOMETRIC SHAPES ---
            "🔘", "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⚫", "⚪", "🟤",
            "🟥", "🟧", "🟨", "🟩", "🟦", "🟪", "⬛", "⬜", "🟫", "🔲",
            "🔳", "◼️", "◻️", "◾", "◽", "▪️", "▫️", "🔸", "🔹", "🔶",
            "🔷", "🔺", "🔻", "💠", "🔘", "⭕", "❌", "❎", "✅", "✔️",

            // --- ARROWS & DIRECTIONS ---
            "⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️", "↕️", "↔️",
            "↩️", "↪️", "⤴️", "⤵️", "🔀", "🔁", "🔂", "🔄", "🔃", "🔼",
            "🔽", "⏫", "⏬", "➡️", "⬅️", "⬆️", "⬇️", "↗️", "↖️", "↘️",
            "↙️", "↕️", "↔️", "↩️", "↪️", "⤵️", "⤴️", "🔃", "🔄", "🔁",
            "🔂", "🔀", "🎦", "🔅", "🔆", "📶", "📳", "📴", "🈁", "🈂️",
            "🈚", "🈶", "🈸", "🈺", "🈷️", "🈹", "🈯", "🈴", "🈳", "🈲",

            // --- UI & TECH SYMBOLS ---
            "💠", "♿", "🛄", "🛅", "🚾", "🚰", "🛗", "🪪", "📛", "🔰",
            "🔱", "⚜️", "♻️", "💹", "💸", "💳", "🧾", "🏧", "💴", "💵",
            "💶", "💷", "💰", "💎", "⚖️", "🪙", "💉", "💊", "🩸", "🩹",
            "🩺", "🩻", "⚕️", "🧬", "🦠", "🧪", "🧫", "🧬", "🔬", "🔭",
            "📡", "💡", "🔦", "🏮", "🪔", "🧯", "🛡️", "🔑", "🗝️", "🔒",
            "🔓", "🔏", "🔐", "🔑", "🗝️", "🔨", "⚒️", "🛠️", "⛏️", "🔧",
            "🔩", "⚙️", "🧰", "🧲", "🔫", "🧨", "💣", "🔪", "🗡️", "⚔️",

            // --- PUNCTUATION & WRITING ---
            "⁉️", "❗", "❓", "❕", "❔", "‼️", "⁉️", "ℹ️", "#️⃣", "*️⃣",
            "⬅️", "➡️", "⬆️", "⬇️", "↗️", "↘️", "↙️", "↖️", "↕️", "↔️",
            "〽️", "〰️", "➰", "➿", "🔚", "🔙", "🔛", "🔝", "🔜", "🔃",

            // --- COMBINED SYMBOLS ---
            "💠", "💢", "💣", "💤", "💦", "💨", "💩", "💪", "💫", "💬",
            "💭", "🗯️", "💮", "💯", "💰", "💱", "💲", "💳", "💴", "💵",
            "💶", "💷", "💸", "💹", "💺", "💻", "💼", "💽", "💾", "💿",
            "📀", "📁", "📂", "📃", "📄", "📅", "📆", "📇", "📈", "📉",
            "📊", "📋", "📌", "📍", "📎", "📏", "📐", "📑", "📒", "📓",
            "📔", "📕", "📖", "📗", "📘", "📙", "📚", "📛", "📜", "📝",
            "📞", "📟", "📠", "📡", "📢", "📣", "📤", "📥", "📦", "📧",
            "📨", "📩", "📪", "📫", "📬", "📭", "📮", "📯", "📰", "📱",
            "📲", "📳", "📴", "📵", "📶", "📷", "📸", "📹", "📺", "📻",
            "📼", "📽️", "📿", "🔀", "🔁", "🔂", "🔃", "🔄", "🔅", "🔆",
            "🔇", "🔈", "🔉", "🔊", "🔋", "🔌", "🔍", "🔎", "🔏", "🔐",
            "🔑", "🔒", "🔓", "🔔", "🔕", "🔖", "🔗", "🔘", "🔙", "🔚",
            "🔛", "🔜", "🔝", "🔞", "🔟", "🔠", "🔡", "🔢", "🔣", "🔤",
            "🔥", "🔦", "🔧", "🔨", "🔩", "🔪", "🔫", "🔬", "🔭", "🔮",
            "🔯", "🔰", "🔱", "🔲", "🔳", "🔴", "🔵", "🔶", "🔷", "🔸",
            "🔹", "🔺", "🔻", "🔼", "🔽", "🕉️", "🕊️", "🕋", "🕌", "🕍",
            "🕎", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘",
            "🕙", "🕚", "🕛", "🕜", "🕝", "🕞", "🕟", "🕠", "🕡", "🕢",
            "🕣", "🕤", "🕥", "🕦", "🕧",
        )
    }


    private val categories by lazy {
        mutableListOf(
            "Recent" to getRecentEmojis(),
            "Smiley" to smileyList,
            "People" to peopleList,
            "Nature" to natureList,
            "Food" to foodList,
            "Places" to places,
            "Objects" to objectList,
            "Symbols" to symbolList,
        )
    }

    private fun initView(): View {
        if (!this::emojiView.isInitialized) {

            // 1. သၢင်ႈ Context ဢၼ်မီး Theme (ၸႂ်ႉ Theme.Material3 ဢမ်ႇၼၼ် Theme.AppCompat)
            val contextThemeWrapper = ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_Material3_DayNight
            )

            // 2. ၸႂ်ႉ LayoutInflater ဢၼ်လုၵ်ႉတီႈ Wrapper ၼၼ်ႉမႃး Inflate
            val themedInflater = layoutInflater.cloneInContext(contextThemeWrapper)

            // 3. Inflate Layout လူၺ်ႈၸႂ်ႉ themedInflater
            emojiView = themedInflater.inflate(R.layout.emoji_picker, null)

            val viewPager = emojiView.findViewById<ViewPager2>(R.id.emoji_viewpager)
            val tabLayout = emojiView.findViewById<TabLayout>(R.id.emoji_tabs)

            // ႁႃတၢင်းသုင် key_height လုၵ်ႉတီႈ dimens.xml
            val keyHeight = context.resources.getDimensionPixelSize(R.dimen.key_height)
            val padding = context.resources.getDimensionPixelSize(R.dimen.keyboard_padding)

            // Total Height = (5 Rows * keyHeight) + (2 * Padding)
            val totalHeight = (keyHeight * 5) + (padding * 2)

            // Force တၢင်းသုင် Layout ႁႂ်ႈမိူၼ် Keyboard တႅတ်ႈတေႃး
            emojiView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                totalHeight
            )

            // Setup ViewPager Adapter
            val emojiAdapter = object : RecyclerView.Adapter<EmojiPageViewHolder>() {
                // ၼႂ်း ViewPager Adapter:
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): EmojiPageViewHolder {
                    val recyclerView = RecyclerView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                        // ၸတ်းႁႂ်ႈမီး 8 Columns (ၸွမ်းၼင်ႇသႅၼ်း Keyboard)
                        layoutManager = GridLayoutManager(context, 8)
                    }
                    return EmojiPageViewHolder(recyclerView)
                }

                override fun onBindViewHolder(holder: EmojiPageViewHolder, position: Int) {
                    val list = categories[position].second
                    // ၸႂ်ႉ EmojiAdapter တူဝ်မႂ်ႇ ဢၼ်ပဵၼ် RecyclerView.Adapter
                    holder.recyclerView.adapter = EmojiAdapter(context, list) { emoji ->
                        onEmojiPressed(emoji)
                        saveToRecentEmojis(emoji)
                    }
                }

                override fun getItemCount(): Int = categories.size
            }
            viewPager.adapter = emojiAdapter

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // သင်ပဵၼ် Recent Tab (Index 0) ႁႂ်ႈ Refresh Data
                    if (categories[position].first == "Recent") {
                        // Update List ၼႂ်း Memory ၵွၼ်ႇ
                        categories[position] = "Recent" to getRecentEmojis()
                        // ႁွင်ႉ Adapter ႁႂ်ႈ Draw မႂ်ႇ
                        emojiAdapter.notifyItemChanged(position)
                    }

                    // --- လွင်ႈတၢင်းဢၼ်ၸဝ်ႈၵဝ်ႇၺႃးသီ Dark ၼၼ်ႉၵေႃႈ ၵႄႈလႆႈတီႈၼႆႉၸွမ်းၶႃႈ ---
                    ThemeManager.applyTheme(context.applicationContext, emojiView)
                }
            })

            // Connect TabLayout with ViewPager2
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = categories[position].first
                // ၸဝ်ႈၵဝ်ႇၸၢင်ႈသႂ်ႇ Icon တႅၼ်း Text လႆႈၶႃႈ: tab.icon = getIcon(position)
            }.attach()

            // Setup Listeners ၵွၺ်းပွၵ်ႈလဵဝ်
            emojiView.apply {
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
        ThemeManager.applyTheme(context, emojiView)
        return emojiView
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

    class EmojiPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView as RecyclerView
    }


    class EmojiAdapter(
        private val context: Context,
        private val emojis: List<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

        private val inflater = LayoutInflater.from(context)

        // ViewHolder Pattern တွၼ်ႈတႃႇလူတ်ႇယွၼ်ႇၵၢၼ် findViewById()
        class EmojiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val button: Button = view.findViewById(R.id.emoji_button)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
            val view = inflater.inflate(R.layout.item_emoji, parent, false)
            return EmojiViewHolder(view)
        }

        override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
            val emoji = emojis[position]
            holder.button.text = emoji

            // --- Apply Theme ၵမ်းလဵဝ် (RecyclerView တေ Handle Recycling ပၼ်ႁင်းၵူၺ်း) ---
            ThemeManager.applySingleViewTheme(context, holder.button)

            holder.button.setOnClickListener { onClick(emoji) }
        }

        override fun getItemCount(): Int = emojis.size
    }


}