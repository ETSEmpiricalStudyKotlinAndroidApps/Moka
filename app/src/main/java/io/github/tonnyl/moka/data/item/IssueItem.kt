package io.github.tonnyl.moka.data.item

import io.github.tonnyl.moka.data.Actor
import io.github.tonnyl.moka.data.toNonNullActor
import io.github.tonnyl.moka.queries.IssuesQuery
import io.github.tonnyl.moka.queries.IssuesQuery.Data.Repository.Issues.Node.Author.Companion.actor
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

fun IssuesQuery.Data.Repository.Issues.Node.toNonNullIssueItem(): IssueItem {
    return IssueItem(
        author?.actor()?.toNonNullActor(),
        id,
        number,
        createdAt,
        title,
        closed
    )
}