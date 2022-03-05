package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubIncidentUpdate(

    val id: String,

    val status: GitHubIncidentStatus,

    val body: String,

    @SerialName("incident_id")
    val incidentId: String,

    @SerialName("created_at")
    val createdAt: Instant,

    @SerialName("updated_at")
    val updatedAt: Instant,

    @SerialName("display_at")
    val displayAt: Instant,

    @SerialName("affected_components")
    val affectedComponents: List<GitHubIncidentAffectedComponent>?,

    @SerialName("deliver_notifications")
    val deliverNotifications: Boolean,

    @SerialName("custom_tweet")
    val customTweet: String?,

    @SerialName("tweet_id")
    val tweetId: String?

)