package it.saimao.tmkkeyboardpro.fragments

import ThemeAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.saimao.tmkkeyboardpro.R
import it.saimao.tmkkeyboardpro.activities_services.ChooseThemeActivity
import it.saimao.tmkkeyboardpro.databinding.FragmentPredefinedThemeBinding
import it.saimao.tmkkeyboardpro.logic.ThemeManager

class PredefinedThemeFragment : Fragment() {

    private lateinit var binding: FragmentPredefinedThemeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPredefinedThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val themeNames = ThemeManager.themes.keys.toList()

        binding.rvThemes.layoutManager =
            GridLayoutManager(requireContext(), 2) // ၼႄ 2 ၶွၼ် (Columns)
        binding.rvThemes.adapter = ThemeAdapter(themeNames) { selectedTheme ->
            // မိူဝ်ႈ User တိၵ်းလိူၵ်ႈသီ
            ThemeManager.saveTheme(requireContext(), selectedTheme)
            // ႁွင်ႉ Method updatePreview() ဢၼ်ယူႇၼႂ်း Activity
            (activity as? ChooseThemeActivity)?.updatePreview()
        }
    }

}