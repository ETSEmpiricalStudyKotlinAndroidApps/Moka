package io.github.tonnyl.moka.data.item

import android.os.Parcelable
import io.github.tonnyl.moka.data.Actor
import io.github.tonnyl.moka.data.toNonNullActor
import io.github.tonnyl.moka.queries.IssuesQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
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
    val createdAt: Date,

    /**
     * Identifies the issue title.
     */
    val title: String,

    /**
     * Identifies the date and time when the object was closed.
     */
    val closed: Boolean

) : Parcelable

fun IssuesQuery.Node.toNonNullIssueItem(): IssueItem {
    return IssueItem(
        author?.fragments?.actor?.toNonNullActor(),
        id,
        number,
        createdAt,
        title,
        closed
    )
}