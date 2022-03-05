package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubStatusComponent(

    val id: String,

    val name: String,

    val status: GitHubStatusComponentStatus,

    @SerialName("created_at")
    @Contextual
    val createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    val updatedAt: Instant,

    val position: Int,

    val description: String?,

    val showcase: Boolean,

    @SerialName("only_show_if_degraded")
    val onlyShowIfDegraded: Boolean = false

)