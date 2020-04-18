package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.type.ReactionContent
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.ReactionGroup as RawReactionGroup

/**
 * A group of emoji reactions to a particular piece of content.
 */
@Parcelize
data class ReactionGroup(

    /**
     * Identifies the emoji reaction.
     */
    val content: ReactionContent,

    /**
     * Whether or not the authenticated user has left a reaction on the subject.
     */
    val viewerHasReacted: Boolean,

    /**
     * Total count of users who have reacted to the reaction subject with the emotion represented by this reaction group.
     */
    val usersTotalCount: Int

) : Parcelable

fun RawReactionGroup.toNonNullReactionGroup(): ReactionGroup {
    return ReactionGroup(content, viewerHasReacted, users.totalCount)
}