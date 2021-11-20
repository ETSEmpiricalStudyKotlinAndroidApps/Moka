package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PullRequestListItem(

    val user: Actor?,

    val id: String,

    val number: Int,

    @SerialName("created_at")
    val createdAt: Instant,

    val title: String,

    val state: IssuePrState,

    @SerialName("merged_at")
    val mergedAt: Instant?,

    val draft: Boolean

)