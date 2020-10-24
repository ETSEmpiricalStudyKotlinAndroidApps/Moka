package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.fragment.ReactionGroup as RawReactionGroup

/**
 * A group of emoji reactions to a particular piece of content.
 */
data class ReactionGroup(

    /**
     * Identifies the emoji reaction.
     */
    val content: ReactionContent,

    /**
     * Whether or not the authenticated user has left a reaction on the subject.
     */
    var viewerHasReacted: Boolean,

    /**
     * Total count of users who have reacted to the reaction subject with the emotion represented by this reaction group.
     */
    var usersTotalCount: Int

)

fun RawReactionGroup.toNonNullReactionGroup(): ReactionGroup {
    return ReactionGroup(content, viewerHasReacted, users.totalCount)
}