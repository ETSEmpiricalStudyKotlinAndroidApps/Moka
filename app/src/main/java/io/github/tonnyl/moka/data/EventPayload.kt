package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventPayload(

    @ColumnInfo(name = "action")
    var action: String? = null,

    @Embedded(prefix = "comment_")
    var comment: EventComment? = null,

    @SerialName("commit_comment")
    @Embedded(prefix = "commit_comment_")
    var commitComment: EventComment? = null,

    @Embedded(prefix = "issue_")
    var issue: EventIssue? = null,

    @SerialName("pull_request")
    @Embedded(prefix = "pull_request_")
    var pullRequest: EventPullRequest? = null,

    @Embedded(prefix = "review_")
    var review: EventReview? = null,

    /**
     * Only for [Event.DOWNLOAD_EVENT].
     */
    @Embedded(prefix = "download_")
    var download: EventDownload? = null,

    /**
     * Only for [Event.FOLLOW_EVENT]
     */
    @Embedded(prefix = "target_")
    @Contextual
    var target: EventActor? = null,

    /**
     * Only for [Event.FORK_EVENT]
     */
    @Embedded(prefix = "forkee_")
    @Contextual
    var forkee: EventRepository? = null,

    /**
     * Only for [Event.GIST_EVENT]
     */
    @Embedded(prefix = "gist_")
    var gist: Gist? = null,

    /**
     * Only for [Event.GOLLUM_EVENT]
     */
    @ColumnInfo(name = "pages")
    var pages: List<EventGollumPage>? = null,

    /**
     * Only for [Event.MEMBER_EVENT]
     */
    @Embedded(prefix = "member_")
    @Contextual
    var member: EventActor? = null,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "team_")
    var team: EventTeam? = null,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "organization_")
    @Contextual
    var organization: EventActor? = null,

    /**
     * Only for [Event.RELEASE_EVENT]
     */
    @Embedded(prefix = "release_")
    var release: EventRelease? = null,

    /**
     * Only for [Event.ORG_BLOCK_EVENT]
     */
    @SerialName("blocked_user")
    @Embedded(prefix = "blocked_user_")
    @Contextual
    var blockedUser: EventActor? = null,

    /**
     * Only for [Event.PROJECT_CARD_EVENT]
     */
    @SerialName("project_card")
    @Embedded(prefix = "project_card_")
    var projectCard: EventProjectCard? = null,

    /**
     * Only for [Event.PROJECT_COLUMN_EVENT]
     */
    @SerialName("project_column")
    @Embedded(prefix = "project_column_")
    var projectColumn: EventProjectColumn? = null,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "membership_")
    var membership: EventMembership? = null,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "invitation_")
    @Contextual
    var invitation: EventActor? = null,

    /**
     * Only for [Event.PROJECT_EVENT]
     */
    @Embedded(prefix = "project_")
    var project: EventProject? = null,

    /**
     * Only for [Event.PUSH_EVENT]
     *
     * The number of commits in the push.
     */
    @ColumnInfo(name = "size")
    var size: Int? = null,

    @SerialName("ref_type")
    @ColumnInfo(name = "ref_type")
    var refType: String? = null,

    @ColumnInfo(name = "ref")
    var ref: String? = null

)

@Serializable
data class EventComment(

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @Embedded(prefix = "user_")
    @Contextual
    var user: EventActor,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String,

    @ColumnInfo(name = "body")
    var body: String,

    @SerialName("commit_id")
    @ColumnInfo(name = "commit_id")
    var commitId: String? = null

)

@Serializable
data class EventPullRequest(

    @ColumnInfo(name = "id")
    var id: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerialName("diff_url")
    @ColumnInfo(name = "diff_url")
    var diffUrl: String,

    @SerialName("patch_url")
    @ColumnInfo(name = "patch_url")
    var patchUrl: String,

    @ColumnInfo(name = "number")
    var number: Long,

    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "locked")
    var locked: Boolean,

    @ColumnInfo(name = "title")
    var title: String,

    @Embedded(prefix = "user_")
    @Contextual
    var user: EventActor,

    @ColumnInfo(name = "body")
    var body: String? = null,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("closed_at")
    @ColumnInfo(name = "closed_at")
    @Contextual
    var closedAt: Instant? = null,

    @SerialName("merged_at")
    @ColumnInfo(name = "merged_at")
    @Contextual
    var mergedAt: Instant? = null,

    @SerialName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

)

@Serializable
data class EventReview(

    @ColumnInfo(name = "id")
    var id: String,

    @Embedded(prefix = "user_")
    @Contextual
    var user: EventActor,

    @ColumnInfo(name = "body")
    var body: String?,

    @SerialName("submitted_at")
    @ColumnInfo(name = "submitted_at")
    @Contextual
    var submittedAt: Instant,

    @ColumnInfo(name = "state")
    var state: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerialName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

)

@Serializable
data class EventDownload(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "size")
    var size: Long,

    @SerialName("download_count")
    @ColumnInfo(name = "download_count")
    var downloadCount: Long,

    @SerialName("content_type")
    @ColumnInfo(name = "content_type")
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

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "slug")
    var slug: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "privacy")
    var privacy: Boolean,

    @ColumnInfo(name = "permission")
    var permission: String

)

@Serializable
data class EventRelease(

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @SerialName("tag_name")
    @ColumnInfo(name = "tag_name")
    var tagName: String,

    @SerialName("target_commitish")
    @ColumnInfo(name = "target_commitish")
    var targetCommitish: String,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "draft")
    var draft: Boolean,

    @Embedded(prefix = "author_")
    @Contextual
    var author: EventActor,

    @ColumnInfo(name = "prerelease")
    var prerelease: Boolean,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("published_at")
    @ColumnInfo(name = "published_at")
    @Contextual
    var publishedAt: Instant,

    @ColumnInfo(name = "body")
    var body: String

)

@Serializable
data class EventProjectCard(

    @SerialName("column_id")
    @ColumnInfo(name = "column_id")
    var columnId: Long,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "note")
    var note: String,

    @Embedded(prefix = "creator_")
    @Contextual
    var creator: EventActor,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventProjectColumn(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventProject(

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "state")
    var state: String,

    @Embedded(prefix = "creator_")
    @Contextual
    var creator: EventActor,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant

)

@Serializable
data class EventGistFile(

    @ColumnInfo(name = "filename")
    var filename: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "language")
    var language: String,

    @SerialName("raw_url")
    @ColumnInfo(name = "raw_url")
    var rawUrl: String,

    @ColumnInfo(name = "size")
    var size: Long

)

@Serializable
data class EventIssue(

    @SerialName("repository_url")
    @ColumnInfo(name = "repository_url")
    var repositoryUrl: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @Embedded(prefix = "user_")
    @Contextual
    var user: EventActor

)

@Serializable
data class EventMembership(

    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "role")
    var role: String,

    @Embedded(prefix = "user_")
    @Contextual
    var user: EventActor

)