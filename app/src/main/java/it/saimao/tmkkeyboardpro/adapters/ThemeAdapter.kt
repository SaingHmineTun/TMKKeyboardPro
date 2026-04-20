import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.logic.ThemeManager

class ThemeAdapter(
    private val themeNames: List<String>,
    private val onThemeSelected: (String) -> Unit
) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

    // ၵဵပ်းၵႃႈ Theme ဢၼ်တိုၵ်ႉၸႂ်ႉယူႇယၢမ်းလဵဝ်
    private var currentSelectedTheme: String = ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: com.google.android.material.card.MaterialCardView =
            view.findViewById(R.id.card_theme_preview)
        val tvName: TextView = view.findViewById(R.id.tv_theme_name)
        val colorIndicator: View = view.findViewById(R.id.view_color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_theme, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val themeName = themeNames[position]
        val theme = ThemeManager.themes[themeName]!!

        // 1. Read Current Theme လုၵ်ႉတီႈ Manager
        if (currentSelectedTheme.isEmpty()) {
            currentSelectedTheme = ThemeManager.getTheme(context)
        }

        holder.tvName.text = themeName
        holder.colorIndicator.background.setTint(theme.key.toColorInt())
        holder.cardView.setCardBackgroundColor(theme.txt.toColorInt())
        holder.tvName.setTextColor(theme.key.toColorInt())

        if (themeName == currentSelectedTheme) {
            holder.cardView.strokeWidth = 8 // 8px Border
            holder.cardView.strokeColor = "#D4AF37".toColorInt() // Gold Stroke
            holder.cardView.alpha = 1.0f
        } else {
            // သင်ဢမ်ႇလႆႈလိူၵ်ႈ: သႂ်ႇ Border မၢင်မၢင် သီထဝ်ႇ
            holder.cardView.strokeWidth = 2
            holder.cardView.strokeColor = "#444444".toColorInt()
            holder.cardView.alpha =
                0.8f // ႁဵတ်းႁႂ်ႈမၼ်း "Dim" ဢိတ်းၼိုင်ႈ တွၼ်ႈတႃႇၼႄဝႃႈဢမ်ႇလႆႈလိူၵ်ႈ
        }

        holder.itemView.setOnClickListener {
            currentSelectedTheme = themeName
            onThemeSelected(themeName)
            notifyDataSetChanged() // Update တင်း List ႁႂ်ႈ Border လႅၵ်ႈၸွမ်း
        }
    }

    override fun getItemCount(): Int = themeNames.size
}