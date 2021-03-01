package io.github.tonnyl.moka.ui.explore

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocalLanguage(

    val urlParam: String?,

    val name: String,

    val color: String

)