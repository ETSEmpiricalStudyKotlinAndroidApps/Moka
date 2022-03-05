package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubIncidentAffectedComponent(

    val code: String,

    val name: String,

    @SerialName("old_status")
    val oldStatus: GitHubStatusComponentStatus,

    @SerialName("new_status")
    val newStatus: GitHubStatusComponentStatus

)