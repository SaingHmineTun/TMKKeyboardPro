import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.logic.ThemeManager
import androidx.core.graphics.toColorInt

class ThemeAdapter(
    private val themeNames: List<String>,
    private val onThemeSelected: (String) -> Unit
) : RecyclerView.Adapter<ThemeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.card_theme_preview)
        val tvName: TextView = view.findViewById(R.id.tv_theme_name)
        val colorIndicator: View = view.findViewById(R.id.view_color_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_theme, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val themeName = themeNames[position]
        val theme = ThemeManager.themes[themeName]!!

        holder.tvName.text = themeName

        // ၼႄသီ Primary ၶွင် Theme ၼၼ်ႉၼႂ်းဝူင်းမူၼ်း
        holder.colorIndicator.background.setTint(theme.key.toColorInt())
        holder.cardView.setCardBackgroundColor(theme.bg.toColorInt())
        holder.tvName.setTextColor(theme.txt.toColorInt())

        holder.itemView.setOnClickListener {
            onThemeSelected(themeName)
        }
    }

    override fun getItemCount(): Int = themeNames.size
}