package it.saimao.tmkkeyboardpro.model

enum class Language {
    EN("English"), SHN("တႆး"), MY("မြန်မာ"), TDD("ᥖᥭᥰ"), TH("ไทย"), LO("ລາວ"), AHM("\uD805\uDF04\uD805\uDF29\uD805\uDF12\uD805\uDF21\uD805\uDF11\uD805\uDF2A\uD805\uDF24");

    var fullname: String

    constructor(fullname: String) {
        this.fullname = fullname
    }
}

data class KeyboardLanguage(
    val language: Language,     // English, Shan (တႆး), Thai (ไทย)
    var isEnabled: Boolean = false,
    val isDefault: Boolean = false // English တေပဵၼ် Default ဝႆႉ
)