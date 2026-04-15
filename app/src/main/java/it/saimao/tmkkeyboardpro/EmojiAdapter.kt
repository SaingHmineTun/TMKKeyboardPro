package it.saimao.tmkkeyboardpro

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class EmojiAdapter(context: Context, private val emojis: List<String>, val onClick: (String) -> Unit) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = emojis.size
    override fun getItem(position: Int) = emojis[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = emojis[position]
        textView.textSize = 24f
        textView.gravity = Gravity.CENTER
        textView.setOnClickListener { onClick(emojis[position]) }
        return view
    }
}