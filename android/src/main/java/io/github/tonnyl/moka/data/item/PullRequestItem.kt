package io.github.tonnyl.moka.data.item

import io.github.tonnyl.moka.data.toNonNullActor
import io.tonnyl.moka.common.data.Actor
import io.tonnyl.moka.graphql.PullRequestsQuery
import kotlinx.datetime.Instant

data class PullRequestItem(

    /**
     * The actor who authored the comment.
     */
    val actor: Actor?,

    /**
     * `true` if the pull request is closed
     */
    val closed: Boolean,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    val id: String,

    /**
     * Whether or not the pull request was merged.
     */
    val merged: Boolean,

    /**
     * Identifies the pull request number.
     */
    val number: Int,

    /**
     * Identifies the pull request title.
     */
    val title: String

)

fun PullRequestsQuery.Node.toNonNullPullRequestItem(): PullRequestItem {
    return PullRequestItem(
        author?.actor?.toNonNullActor(),
        closed,
        createdAt,
        id,
        merged,
        number,
        title
    )
}