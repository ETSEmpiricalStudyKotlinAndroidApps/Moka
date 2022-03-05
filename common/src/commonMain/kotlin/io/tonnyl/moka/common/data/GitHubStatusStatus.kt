package io.tonnyl.moka.common.data

import kotlinx.serialization.Serializable

@Serializable
data class GitHubStatusStatus(

    val indicator: GitHubStatusStatusIndicator,

    val description: String

)