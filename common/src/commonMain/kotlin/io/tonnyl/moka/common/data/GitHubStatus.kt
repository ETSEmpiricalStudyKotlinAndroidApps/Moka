package io.tonnyl.moka.common.data

import kotlinx.serialization.Serializable

@Serializable
data class GitHubStatus(

    val page: GitHubStatusPage,

    val components: List<GitHubStatusComponent>,

    val status: GitHubStatusStatus,

    val incidents: List<GitHubIncident>

)