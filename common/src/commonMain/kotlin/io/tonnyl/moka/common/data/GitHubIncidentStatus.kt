package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GitHubIncidentStatus {

    @SerialName("investigating")
    Investigating,

    @SerialName("identified")
    Identified,

    @SerialName("monitoring")
    Monitoring,

    @SerialName("resolved")
    Resolved,

    @SerialName("postmortem")
    Postmortem

}