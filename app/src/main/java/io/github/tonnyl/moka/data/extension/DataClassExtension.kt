package io.github.tonnyl.moka.data.extension

import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.TimelineItem
import io.github.tonnyl.moka.fragment.PageInfo
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.UserEvent.React
import io.github.tonnyl.moka.ui.reaction.ReactionChange
import io.github.tonnyl.moka.ui.reaction.ReactionChangePayload

val PageInfo?.checkedStartCursor: String?
    get() {
        return if (this?.hasPreviousPage == true) {
            startCursor
        } else {
            null
        }
    }

val PageInfo?.checkedEndCursor: String?
    get() {
        return if (this?.hasNextPage == true) {
            endCursor
        } else {
            null
        }
    }

fun Issue.updateByReactionEventIfNeeded(event: React): Boolean {
    if (event.resource.status != Status.SUCCESS) {
        return false
    }

    if (this.reactionGroups == null) {
        reactionGroups = mutableListOf()
    }

    reactionGroups?.updateByReactionEventIfNeeded(event)

    return true
}

fun PullRequest.updateByReactionEventIfNeeded(event: React): Boolean {
    if (event.resource.status != Status.SUCCESS) {
        return false
    }

    if (this.reactionGroups == null) {
        reactionGroups = mutableListOf()
    }

    reactionGroups?.updateByReactionEventIfNeeded(event)

    return true
}

fun List<TimelineItem>.updateByReactionEventIfNeeded(event: React): ReactionChangePayload {
    if (event.resource.status != Status.SUCCESS) {
        return ReactionChangePayload(-1, null)
    }

    val commentIndex = indexOfFirst {
        it is IssueComment
                && it.id == event.reactableId
    }

    if (commentIndex < 0) {
        return ReactionChangePayload(-1, null)
    }

    val comment = this[commentIndex] as? IssueComment
        ?: return ReactionChangePayload(
            -1,
            null
        )

    val reactionGroups = comment.reactionGroups ?: mutableListOf()
    val change = reactionGroups.updateByReactionEventIfNeeded(event)

    return ReactionChangePayload(
        commentIndex,
        change
    )
}

private fun MutableList<ReactionGroup>.updateByReactionEventIfNeeded(event: React): ReactionChange? {
    val index = indexOfFirst {
        it.content == event.content
    }
    val reactionGroup = if (index < 0) {
        null
    } else {
        this[index]
    }

    return when {
        event.isSelected -> {
            if (reactionGroup == null) {
                add(ReactionGroup(event.content, true, 1))

                ReactionChange.ReactionInsert(this.size - 1)
            } else {
                reactionGroup.viewerHasReacted = true
                reactionGroup.usersTotalCount += 1

                ReactionChange.ReactionUpdate(index)
            }
        }
        reactionGroup != null -> {
            if (reactionGroup.usersTotalCount == 1) {
                remove(reactionGroup)

                ReactionChange.ReactionRemove(index)
            } else {
                reactionGroup.viewerHasReacted = false
                reactionGroup.usersTotalCount -= 1

                ReactionChange.ReactionUpdate(index)
            }
        }
        else -> {
            null
        }
    }
}