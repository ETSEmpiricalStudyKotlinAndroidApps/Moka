package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubStatusPage(

    val id: String,

    val name: String,

    val url: String,

    @SerialName("time_zone")
    val timeZone: String,

    @Contextual
    @SerialName("updated_at")
    val updatedAt: Instant

)
