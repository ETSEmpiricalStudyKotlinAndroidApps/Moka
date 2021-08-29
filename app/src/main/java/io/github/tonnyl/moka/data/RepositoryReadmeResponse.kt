package io.github.tonnyl.moka.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepositoryReadmeResponse(

    val type: String,

    val encoding: String,

    val size: Long,

    val name: String,

    val path: String,

    val content: String, // encoded content

    @SerialName("html_url")
    val htmlUrl: String

)