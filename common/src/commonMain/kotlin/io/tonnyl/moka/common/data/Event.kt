package io.tonnyl.moka.common.data

import io.tonnyl.moka.common.data.Event.Companion.CREATE_EVENT
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(

    var id: String,

    var type: String,

    // note the difference of serialized name, column name and field name
    @SerialName("public")
    var isPublic: Boolean,

    @Contextual
    var actor: EventActor,

    var repo: EventRepository? = null,

    @Contextual
    var org: EventOrg? = null,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    var payload: EventPayload? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The git ref (or null if only a repository was created).
     */
    var ref: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The object that was created. Can be one of repository, branch, or tag
     */
    @SerialName("ref_type")
    var refType: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The name of the repository's default branch (usually master).
     */
    @SerialName("master_branch")
    var masterBranch: String? = null,

    /**
     * Only for [CREATE_EVENT].
     *
     * The repository's current description.
     */
    var description: String? = null,

    @SerialName("pusher_type")
    var pusherType: String? = null,

    var head: String? = null,

    var before: String? = null

) {

    companion object {

        /**
         * The WatchEvent is related to starring a repository, not watching.
         * The event’s actor is the user who starred a repository, and the event’s repository is the repository that was starred.
         */
        const val WATCH_EVENT = "WatchEvent"

        /**
         * Represents a created repository, branch, or tag.
         */
        const val CREATE_EVENT = "CreateEvent"

        /**
         * Triggered when a commit comment is created.
         */
        const val COMMIT_COMMENT_EVENT = "CommitCommentEvent"

        /**
         * Triggered when a new download is created.
         * Events of this type are no longer delivered, but it's possible that they exist in timelines of some users.
         */
        const val DOWNLOAD_EVENT = "DownloadEvent"

        /**
         * Triggered when a user follows another user.
         * Events of this type are no longer delivered, but it's possible that they exist in timelines of some users.
         */
        const val FOLLOW_EVENT = "FollowEvent"

        /**
         * Triggered when a user forks a repository.
         */
        const val FORK_EVENT = "ForkEvent"

        /**
         * Triggered when a Gist is created or updated.
         * Events of this type are no longer delivered, but it's possible that they exist in timelines of some users.
         */
        const val GIST_EVENT = "GistEvent"

        /**
         * Triggered when a Wiki page is created or updated.
         */
        const val GOLLUM_EVENT = "GollumEvent"

        /**
         * Triggered when an issue comment is created, edited, or deleted.
         */
        const val ISSUE_COMMENT_EVENT = "IssueCommentEvent"

        /**
         * Triggered when an issue is opened, edited, deleted, transferred, pinned, unpinned, closed,
         * reopened, assigned, unassigned, labeled, unlabeled, milestoned, or demilestoned.
         */
        const val ISSUES_EVENT = "IssuesEvent"

        /**
         * Triggered when a user accepts an invitation or is removed as a collaborator to a repository,
         * or has their permissions changed.
         */
        const val MEMBER_EVENT = "MemberEvent"

        /**
         * Triggered when a private repository is open sourced.
         */
        const val PUBLIC_EVENT = "PublicEvent"

        /**
         * Triggered when a pull request is assigned, unassigned, labeled, unlabeled, opened, edited,
         * closed, reopened, synchronized, a pull request review is requested, or a review request is removed.
         */
        const val PULL_REQUEST_EVENT = "PullRequestEvent"

        /**
         * Triggered when a comment on a pull request's unified diff is created, edited, or deleted.
         */
        const val PULL_REQUEST_REVIEW_COMMENT_EVENT = "PullRequestReviewCommentEvent"

        /**
         * Triggered when a pull request review is submitted into a non-pending state, the body is edited, or the review is dismissed.
         */
        const val PULL_REQUEST_REVIEW_EVENT = "PullRequestReviewEvent"

        /**
         * Triggered when a repository is created, archived, unarchived, made public, or made private.
         * Organization hooks are also triggered when a repository is deleted.
         */
        const val REPOSITORY_EVENT = "RepositoryEvent"

        /**
         * Triggered on a push to a repository branch. Branch pushes and repository tag pushes also trigger webhook push events.
         */
        const val PUSH_EVENT = "PushEvent"

        /**
         * Triggered when a repository is added to a team.
         * Events of this type are not visible in timelines. These events are only used to trigger hooks.
         */
        const val TEAM_ADD_EVENT = "TeamAddEvent"

        /**
         * Represents a deleted branch or tag.
         * Note: webhooks will not receive this event for tags if more than three tags are deleted at once.
         */
        const val DELETE_EVENT = "DeleteEvent"

        /**
         * Triggered when a release is published.
         */
        const val RELEASE_EVENT = "ReleaseEvent"

        /**
         * Triggered when a patch is applied in the fork queue.
         * Events of this type are no longer delivered, but it's possible that they exist in timelines of some users.
         */
        const val FORK_APPLY_EVENT = "ForkApplyEvent"

        /**
         * Triggered when an organization blocks or unblocks a user. These events are only used to trigger organization hooks.
         */
        const val ORG_BLOCK_EVENT = "OrgBlockEvent"

        /**
         * Triggered when a project card is created, updated, moved, converted to an issue, or deleted.
         */
        const val PROJECT_CARD_EVENT = "ProjectCardEvent"

        /**
         * Triggered when a project column is created, updated, moved, or deleted.
         */
        const val PROJECT_COLUMN_EVENT = "ProjectColumnEvent"

        /**
         * Triggered when a user is added, removed, or invited to an organization.
         * Events of this type are not visible in GitHub timelines. These events are only used to trigger organization hooks.
         */
        const val ORGANIZATION_EVENT = "OrganizationEvent"

        /**
         * Triggered when a project is created, updated, closed, reopened, or deleted.
         */
        const val PROJECT_EVENT = "ProjectEvent"

    }

}