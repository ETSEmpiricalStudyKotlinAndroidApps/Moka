package io.tonnyl.moka.common.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingDeveloper(

    val username: String,

    val name: String? = null,

    /**
     * could be organization or user.
     */
    val type: String? = null,

    val url: String,

    val avatar: String,

    @SerialName("repo")
    @Contextual
    val repository: TrendingDeveloperRepository? = null

)

@Serializable
data class TrendingDeveloperRepository(

    val name: String,

    val description: String? = null,

    val url: String

)