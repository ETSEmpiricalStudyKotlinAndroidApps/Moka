package io.tonnyl.moka.common.data.extension

import io.tonnyl.moka.graphql.fragment.IssuePullRequestTimelineItemAssigneeFragment
import io.tonnyl.moka.graphql.fragment.PageInfo
import io.tonnyl.moka.graphql.fragment.ReviewRequestRemovedEventFragment
import io.tonnyl.moka.graphql.fragment.ReviewRequestedEventFragment

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

val IssuePullRequestTimelineItemAssigneeFragment.assigneeLogin: String?
    get() = userListItemFragment?.login
        ?: organizationListItemFragment?.login
        ?: issuePullRequestTimelineItemMannequinFragment?.login
        ?: issuePullRequestTimelineItemBotFragment?.login

val ReviewRequestedEventFragment.RequestedReviewer.requestedReviewerLogin: String?
    get() = userListItemFragment?.login
        ?: issuePullRequestTimelineItemTeamFragment?.combinedSlug
        ?: issuePullRequestTimelineItemMannequinFragment?.login

val ReviewRequestRemovedEventFragment.RequestedReviewer.requestedReviewerLogin: String?
    get() = userListItemFragment?.login
        ?: issuePullRequestTimelineItemTeamFragment?.combinedSlug
        ?: issuePullRequestTimelineItemMannequinFragment?.login