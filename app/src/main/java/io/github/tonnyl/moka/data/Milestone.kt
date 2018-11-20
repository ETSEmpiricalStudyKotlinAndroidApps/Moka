package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.IssueQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Represents a Milestone object on a given repository.
 */
@Parcelize
data class Milestone(
        /**
         * true if the object is closed (definition of closed may depend on type).
         */
        val closed: Boolean,
        /**
         * Identifies the date and time when the object was closed.
         */
        val closedAt: Date?,
        /**
         * Identifies the date and time when the object was created.
         */
        val createdAt: Date,
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
        val dueOn: Date?,
        val id: String,
        /**
         * Identifies the number of the milestone.
         */
        val number: Int,
        /**
         * The HTTP path for this milestone.
         */
        val resourcePath: Uri,
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
        val updatedAt: Date,
        /**
         * The HTTP URL for this milestone
         */
        val url: Uri
) : Parcelable {

    companion object {

        fun createFromIssueMilestone(data: IssueQuery.Milestone?): Milestone? = if (data == null) null else Milestone(
                data.closed(),
                data.closedAt(),
                data.createdAt(),
                Actor.createFromMilestoneCreator(data.creator()),
                data.description(),
                data.dueOn(),
                data.id(),
                data.number(),
                data.resourcePath(),
                when (data.state()) {
                    io.github.tonnyl.moka.type.MilestoneState.OPEN -> MilestoneState.OPEN
                    // including [io.github.tonnyl.moka.type.MilestoneState.`$UNKNOWN`], [io.github.tonnyl.moka.type.MilestoneState.CLOSED]
                    else -> MilestoneState.CLOSED
                },
                data.title(),
                data.updatedAt(),
                data.url()
        )

    }

}