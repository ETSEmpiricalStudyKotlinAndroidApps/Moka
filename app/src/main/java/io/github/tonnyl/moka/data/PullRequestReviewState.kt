package io.github.tonnyl.moka.data

enum class PullRequestReviewState {

    /**
     * A review that has not yet been submitted.
     */
    PENDING,

    /**
     * An informational review.
     */
    COMMENTED,

    /**
     * A review allowing the pull request to merge.
     */
    APPROVED,

    /**
     * A review blocking the pull request from merging.
     */
    CHANGES_REQUESTED,

    /**
     * A review that has been dismissed.
     */
    DISMISSED,

}