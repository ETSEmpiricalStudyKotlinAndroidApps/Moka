package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GitHubStatusStatusIndicator {

    @SerialName("none")
    None,

    @SerialName("minor")
    Minor,

    @SerialName("major")
    Major,

    @SerialName("critical")
    Critical,

    @SerialName("maintenance")
    Maintenance

}