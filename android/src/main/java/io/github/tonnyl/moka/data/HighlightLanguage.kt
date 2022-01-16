package io.github.tonnyl.moka.data

import kotlinx.serialization.Serializable

@Serializable
data class HighlightLanguage(

    val lang: String = "",

    val extensions: List<String> = emptyList(),

    val filenames: List<String>? = null

)