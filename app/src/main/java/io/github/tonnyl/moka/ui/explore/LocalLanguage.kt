package io.github.tonnyl.moka.ui.explore

import kotlinx.serialization.Serializable

@Serializable
data class LocalLanguage(

    val urlParam: String?,

    val name: String,

    val color: String

)