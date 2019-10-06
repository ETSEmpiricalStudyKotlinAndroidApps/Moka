package io.github.tonnyl.moka.data.item

import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestsQuery
import io.github.tonnyl.moka.data.Actor
import io.github.tonnyl.moka.data.toNonNullActor
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
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
    val createdAt: Date,

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

) : Parcelable

fun PullRequestsQuery.Node.toNonNullPullRequestItem(): PullRequestItem {
    return PullRequestItem(
        author()?.fragments()?.actor()?.toNonNullActor(),
        closed(),
        createdAt(),
        id(),
        merged(),
        number(),
        title()
    )
}