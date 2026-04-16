package it.saimao.tmkkeyboardpro.logic

import android.content.Context
import android.util.Log

open class DictionaryManager(context: Context, fileName: String) {
    private val words = mutableListOf<String>()

    init {
        loadDictionary(context, fileName)
    }

    private fun loadDictionary(context: Context, fileName: String) {
        try {
            context.assets.open(fileName).bufferedReader(Charsets.UTF_8).use { reader ->
                reader.forEachLine { line ->
                    if (line.isNotBlank()) {
                        words.add(line.trim())
                    }
                }
            }
            Log.d("TAGY", "Successfully loaded ${words.size} words from $fileName")
        } catch (e: Exception) {
            Log.e("TAGY", "Error loading $fileName: ${e.message}")
        }
    }

    // Function တႃႇႁႃၶေႃႈၵႂၢမ်း ဢၼ်တႄႇလူၺ်း တူဝ်လိၵ်ႈဢၼ်တႅမ်ႈဝႆႉ
    fun getSuggestions(query: String): List<String> {
        Log.d("TAGY", "Query - $query")
        Log.d("TAGY", "Shan Words - $words")
        if (query.isEmpty()) return emptyList()
        return words.filter { it.startsWith(query) }.take(5) // ဢဝ် 5 ၶေႃႈၵွၺ်း
    }
}

class ShanDictionaryManager(context: Context) : DictionaryManager(context, "shn_words.txt")
class EnglishDictionaryManager(context: Context) : DictionaryManager(context, "eng_words.txt")
class MyanmarDictionaryManager(context: Context) : DictionaryManager(context, "mm_words.txt")
