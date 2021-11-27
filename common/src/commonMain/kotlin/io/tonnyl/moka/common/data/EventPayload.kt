package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventPayload(

    var action: String? = null,

    var comment: EventComment? = null,

    @SerialName("commit_comment")
    var commitComment: EventComment? = null,

    var issue: EventIssue? = null,

    @SerialName("pull_request")
    var pullRequest: EventPullRequest? = null,

    var review: EventReview? = null,

    /**
     * Only for [Event.DOWNLOAD_EVENT].
     */
    var download: EventDownload? = null,

    /**
     * Only for [Event.FOLLOW_EVENT]
     */
    @Contextual
    var target: EventActor? = null,

    /**
     * Only for [Event.FORK_EVENT]
     */
    @Contextual
    var forkee: EventRepository? = null,

    /**
     * Only for [Event.GIST_EVENT]
     */
    var gist: Gist? = null,

    /**
     * Only for [Event.GOLLUM_EVENT]
     */
    var pages: List<EventGollumPage>? = null,

    /**
     * Only for [Event.MEMBER_EVENT]
     */
    @Contextual
    var member: EventActor? = null,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    var team: EventTeam? = null,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @Contextual
    var organization: EventActor? = null,

    /**
     * Only for [Event.RELEASE_EVENT]
     */
    var release: EventRelease? = null,

    /**
     * Only for [Event.ORG_BLOCK_EVENT]
     */
    @SerialName("blocked_user")
    @Contextual
    var blockedUser: EventActor? = null,

    /**
     * Only for [Event.PROJECT_CARD_EVENT]
     */
    @SerialName("project_card")
    var projectCard: EventProjectCard? = null,

    /**
     * Only for [Event.PROJECT_COLUMN_EVENT]
     */
    @SerialName("project_column")
    var projectColumn: EventProjectColumn? = null,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    var membership: EventMembership? = null,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @Contextual
    var invitation: EventActor? = null,

    /**
     * Only for [Event.PROJECT_EVENT]
     */
    var project: EventProject? = null,

    /**
     * Only for [Event.PUSH_EVENT]
     *
     * The number of commits in the push.
     */
    var size: Int? = null,

    @SerialName("ref_type")
    var refType: String? = null,

    var ref: String? = null

)

@Serializable
data class EventComment(

    @SerialName("html_url")
    var htmlUrl: String,

    var id: Long,

    @Contextual
    var user: EventActor,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("author_association")
    var authorAssociation: String,

    var body: String,

    @SerialName("commit_id")
    var commitId: String? = null

)

@Serializable
data class EventPullRequest(

    var id: String,

    @SerialName("html_url")
    var htmlUrl: String,

    @SerialName("diff_url")
    var diffUrl: String,

    @SerialName("patch_url")
    var patchUrl: String,

    var number: Long,

    var state: String,

    var locked: Boolean,

    var title: String,

    @Contextual
    var user: EventActor,

    var body: String? = null,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("closed_at")
    @Contextual
    var closedAt: Instant? = null,

    @SerialName("merged_at")
    @Contextual
    var mergedAt: Instant? = null,

    @SerialName("author_association")
    var authorAssociation: String

)

@Serializable
data class EventReview(

    var id: String,

    @Contextual
    var user: EventActor,

    var body: String?,

    @SerialName("submitted_at")
    @Contextual
    var submittedAt: Instant,

    var state: String,

    @SerialName("html_url")
    var htmlUrl: String,

    @SerialName("author_association")
    var authorAssociation: String

)

@Serializable
data class EventDownload(

    var id: Int,

    var name: String,

    var description: String,

    var size: Long,

    @SerialName("download_count")
    var downloadCount: Long,

    @SerialName("content_type")
    var contentType: String

)

@Serializable
data class EventGollumPage(

    /**
     * The name of the page.
     */
    @SerialName("page_name")
    var pageName: String,

    /**
     * The current page title.
     */
    var title: String,

    var summary: String? = null,

    /**
     * The action that was performed on the page. Can be created or edited.
     */
    var action: String,

    /**
     * The latest commit SHA of the page.
     */
    var sha: String,

    /**
     * Points to the HTML wiki page.
     */
    @SerialName("html_url")
    var htmlUrl: String

)

@Serializable
data class EventTeam(

    var name: String,

    var id: Long,

    var slug: String,

    var description: String,

    var privacy: Boolean,

    var permission: String

)

@Serializable
data class EventRelease(

    @SerialName("html_url")
    var htmlUrl: String,

    var id: Long,

    @SerialName("tag_name")
    var tagName: String,

    @SerialName("target_commitish")
    var targetCommitish: String,

    var name: String? = null,

    var draft: Boolean,

    @Contextual
    var author: EventActor,

    var prerelease: Boolean,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("published_at")
    @Contextual
    var publishedAt: Instant,

    var body: String?

)

@Serializable
data class EventProjectCard(

    @SerialName("column_id")
    var columnId: Long,

    var id: Long,

    var note: String,

    @Contextual
    var creator: EventActor,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventProjectColumn(

    var id: Long,

    var name: String,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventProject(

    @SerialName("html_url")
    var htmlUrl: String,

    var id: Long,

    var name: String,

    var body: String,

    var number: Int,

    var state: String,

    @Contextual
    var creator: EventActor,

    @SerialName("created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventGistFile(

    var filename: String,

    var type: String,

    var language: String,

    @SerialName("raw_url")
    var rawUrl: String,

    var size: Long

)

@Serializable
data class EventIssue(

    @SerialName("repository_url")
    var repositoryUrl: String,

    @SerialName("html_url")
    var htmlUrl: String,

    var id: Long,

    var number: Int,

    var title: String,

    @Contextual
    var user: EventActor

)

@Serializable
data class EventMembership(

    var state: String,

    var role: String,

    @Contextual
    var user: EventActor

)