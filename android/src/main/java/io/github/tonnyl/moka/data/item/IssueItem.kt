package io.github.tonnyl.moka.data.item

import io.github.tonnyl.moka.data.Actor
import io.github.tonnyl.moka.data.toNonNullActor
import io.tonnyl.moka.graphql.IssuesQuery
import kotlinx.datetime.Instant

data class IssueItem(

    /**
     * The actor who authored the comment.
     */
    val actor: Actor?,

    val id: String,

    /**
     * Identifies the issue number.
     */
    val number: Int,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the issue title.
     */
    val title: String,

    /**
     * Identifies the date and time when the object was closed.
     */
    val closed: Boolean

)

fun IssuesQuery.Node.toNonNullIssueItem(): IssueItem {
    return IssueItem(
        author?.actor?.toNonNullActor(),
        id,
        number,
        createdAt,
        title,
        closed
    )
}