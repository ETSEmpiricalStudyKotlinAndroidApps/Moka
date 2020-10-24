package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.Language as RawLanguage

/**
 * Represents a given language found in repositories.
 */
data class Language(

    /**
     * The color defined for the current language.
     */
    val color: String?,

    val id: String,

    /**
     * The name of the current language.
     */
    val name: String

)

fun RawLanguage.toNonNullLanguage(): Language {
    return Language(color, id, name)
}