package it.saimao.tmkkeyboardpro.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.model.KeyboardLanguage
import it.saimao.tmkkeyboardpro.utils.saveKeyboardLanguageState

class LanguageAdapter(private val languages: List<KeyboardLanguage>) :
    RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_lang_name)
        val switch: SwitchMaterial = view.findViewById(R.id.switch_lang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflation ဢဝ် item_keyboard_language.xml ဢၼ်ႁဝ်းမႄးဝႆႉၼၼ်ႉၶႃႈ
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_keyboard_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lang = languages[position]
        holder.tvName.text = lang.language.fullname
        holder.switch.isChecked = lang.isEnabled

        // သင်ပဵၼ် English (Default), ဢမ်ႇပၼ် User ၼဵၵ်းပိၵ်ႉလႆႈ
        if (lang.isDefault) {
            holder.switch.isEnabled = false
            holder.switch.isChecked = true
        } else {
            holder.switch.isEnabled = true
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            lang.isEnabled = isChecked
            // Save Status ၶဝ်ႈ SharedPreferences ၵမ်းလဵဝ်
            saveKeyboardLanguageState(holder.itemView.context, lang.language.name, isChecked)
        }
    }

    override fun getItemCount(): Int = languages.size
}