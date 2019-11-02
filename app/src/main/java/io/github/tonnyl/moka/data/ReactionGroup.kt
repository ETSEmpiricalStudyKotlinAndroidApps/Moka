package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.type.ReactionContent
import kotlinx.android.parcel.Parcelize
import java.util.*
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
     * Identifies when the reaction was created.
     */
    val createdAt: Date?,

    /**
     * The subject that was reacted to.
     */
    val subject: Reactable?,

    /**
     * Whether or not the authenticated user has left a reaction on the subject.
     */
    val viewerHasReacted: Boolean

) : Parcelable

fun RawReactionGroup.toNonNullReactionGroup(): ReactionGroup {
    return ReactionGroup(
        content,
        createdAt,
        subject.fragments.reactable.toNonNullReactable(),
        viewerHasReacted
    )
}