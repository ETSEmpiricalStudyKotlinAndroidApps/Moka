package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.IssueQuery
import io.github.tonnyl.moka.type.ReactionContent.*
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * A group of emoji reactions to a particular piece of content.
 */
@Parcelize
data class ReactionGroupGraphQL(
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
        val subject: ReactableGraphQL,

        /**
         * Whether or not the authenticated user has left a reaction on the subject.
         */
        val viewerHasReacted: Boolean
) : Parcelable {

    companion object {

        fun createFromIssueReactionGroup(data: IssueQuery.ReactionGroup): ReactionGroupGraphQL = ReactionGroupGraphQL(
                when (data.content()) {
                    THUMBS_UP -> ReactionContent.THUMBS_UP
                    THUMBS_DOWN -> ReactionContent.THUMBS_DOWN
                    LAUGH -> ReactionContent.LAUGH
                    HOORAY -> ReactionContent.HOORAY
                    CONFUSED -> ReactionContent.CONFUSED
                    HEART -> ReactionContent.HEART
                    `$UNKNOWN` -> ReactionContent.THUMBS_UP
                },
                data.createdAt(),
                ReactableGraphQL.createFromIssueSubject(data.subject()),
                data.viewerHasReacted()
        )

    }

}