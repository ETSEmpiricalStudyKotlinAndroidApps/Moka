package io.github.tonnyl.moka.data

import io.tonnyl.moka.common.data.Actor
import io.tonnyl.moka.graphql.type.MilestoneState
import kotlinx.datetime.Instant
import io.tonnyl.moka.graphql.fragment.Milestone as RawMilestone

/**
 * Represents a Milestone object on a given repository.
 */
data class Milestone(

    /**
     * true if the object is closed (definition of closed may depend on type).
     */
    val closed: Boolean,

    /**
     * Identifies the date and time when the object was closed.
     */
    val closedAt: Instant?,

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * Identifies the actor who created the milestone.
     */
    val creator: Actor?,

    /**
     * Identifies the description of the milestone.
     */
    val description: String?,

    /**
     * Identifies the due date of the milestone.
     */
    val dueOn: Instant?,

    val id: String,

    /**
     * Identifies the number of the milestone.
     */
    val number: Int,

    /**
     * The HTTP path for this milestone.
     */
    val resourcePath: String,

    /**
     * Identifies the state of the milestone.
     */
    val state: MilestoneState,

    /**
     * Identifies the title of the milestone.
     */
    val title: String,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Instant,

    /**
     * The HTTP URL for this milestone
     */
    val url: String

)

fun RawMilestone.toNonNullMilestone(): Milestone {
    return Milestone(
        closed,
        closedAt,
        createdAt,
        creator?.actor?.toNonNullActor(),
        description,
        dueOn,
        id,
        number,
        resourcePath,
        state,
        title,
        updatedAt,
        url
    )
}