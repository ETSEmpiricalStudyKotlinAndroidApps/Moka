package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubIncident(

    val id: String,

    val name: String,

    val status: GitHubIncidentStatus,

    @SerialName("created_at")
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant,

    @SerialName("monitoring_at")
    val monitoringAt: Instant?,

    @SerialName("resolved_at")
    val resolvedAt: Instant?,

    val impact: GitHubStatusStatusIndicator,

    val shortlink: String?,

    @SerialName("started_at")
    val startedAt: Instant,

    @SerialName("page_id")
    val pageId: String,

    @SerialName("incident_updates")
    val incidentUpdates: List<GitHubIncidentUpdate>,

    val components: List<GitHubStatusComponent>

)
