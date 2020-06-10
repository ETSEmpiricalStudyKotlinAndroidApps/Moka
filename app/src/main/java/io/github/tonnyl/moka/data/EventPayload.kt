package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@SuppressLint("ParcelCreator")
@JsonClass(generateAdapter = true)
data class EventPayload(

    @ColumnInfo(name = "action")
    var action: String?,

    @Embedded(prefix = "comment_")
    var comment: EventComment?,

    @Json(name = "commit_comment")
    @Embedded(prefix = "commit_comment_")
    var commitComment: EventComment?,

    @Embedded(prefix = "issue_")
    var issue: EventIssue?,

    @Json(name = "pull_request")
    @Embedded(prefix = "pull_request_")
    var pullRequest: EventPullRequest?,

    @Embedded(prefix = "review_")
    var review: EventReview?,

    /**
     * Only for [Event.DOWNLOAD_EVENT].
     */
    @Embedded(prefix = "download_")
    var download: EventDownload?,

    /**
     * Only for [Event.FOLLOW_EVENT]
     */
    @Embedded(prefix = "target_")
    var target: EventActor?,

    /**
     * Only for [Event.FORK_EVENT]
     */
    @Embedded(prefix = "forkee_")
    var forkee: EventRepository?,

    /**
     * Only for [Event.GIST_EVENT]
     */
    @Embedded(prefix = "gist_")
    var gist: Gist?,

    /**
     * Only for [Event.GOLLUM_EVENT]
     */
    @ColumnInfo(name = "pages")
    var pages: List<EventGollumPage>?,

    /**
     * Only for [Event.MEMBER_EVENT]
     */
    @Embedded(prefix = "member_")
    var member: EventActor?,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "team_")
    var team: EventTeam?,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "organization_")
    var organization: EventActor?,

    /**
     * Only for [Event.RELEASE_EVENT]
     */
    @Embedded(prefix = "release_")
    var release: EventRelease?,

    /**
     * Only for [Event.ORG_BLOCK_EVENT]
     */
    @Json(name = "blocked_user")
    @Embedded(prefix = "blocked_user_")
    var blockedUser: EventActor?,

    /**
     * Only for [Event.PROJECT_CARD_EVENT]
     */
    @Json(name = "project_card")
    @Embedded(prefix = "project_card_")
    var projectCard: EventProjectCard?,

    /**
     * Only for [Event.PROJECT_COLUMN_EVENT]
     */
    @Json(name = "project_column")
    @Embedded(prefix = "project_column_")
    var projectColumn: EventProjectColumn?,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "membership_")
    var membership: EventMembership?,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "invitation_")
    var invitation: EventActor?,

    /**
     * Only for [Event.PROJECT_EVENT]
     */
    @Embedded(prefix = "project_")
    var project: EventProject?,

    /**
     * Only for [Event.PUSH_EVENT]
     *
     * The number of commits in the push.
     */
    @ColumnInfo(name = "size")
    var size: Int?,

    @Json(name = "ref_type")
    @ColumnInfo(name = "ref_type")
    var refType: String?,

    @ColumnInfo(name = "ref")
    var ref: String?

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventComment(

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @Embedded(prefix = "user_")
    var user: EventActor,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @Json(name = "author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String,

    @ColumnInfo(name = "body")
    var body: String,

    @Json(name = "commit_id")
    @ColumnInfo(name = "commit_id")
    var commitId: String?

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventPullRequest(

    @ColumnInfo(name = "id")
    var id: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @Json(name = "diff_url")
    @ColumnInfo(name = "diff_url")
    var diffUrl: String,

    @Json(name = "patch_url")
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
    var user: EventActor,

    @ColumnInfo(name = "body")
    var body: String?,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @Json(name = "closed_at")
    @ColumnInfo(name = "closed_at")
    var closedAt: Date?,

    @Json(name = "merged_at")
    @ColumnInfo(name = "merged_at")
    var mergedAt: Date?,

    @Json(name = "author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventReview(

    @ColumnInfo(name = "id")
    var id: String,

    @Embedded(prefix = "user_")
    var user: EventActor,

    @ColumnInfo(name = "body")
    var body: String?,

    @Json(name = "submitted_at")
    @ColumnInfo(name = "submitted_at")
    var submittedAt: Date,

    @ColumnInfo(name = "state")
    var state: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @Json(name = "author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventDownload(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "size")
    var size: Long,

    @Json(name = "download_count")
    @ColumnInfo(name = "download_count")
    var downloadCount: Long,

    @Json(name = "content_type")
    @ColumnInfo(name = "content_type")
    var contentType: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventGollumPage(

    /**
     * The name of the page.
     */
    @Json(name = "page_name")
    var pageName: String,

    /**
     * The current page title.
     */
    var title: String,

    var summary: String?,

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
    @Json(name = "html_url")
    var htmlUrl: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
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

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventRelease(

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @Json(name = "tag_name")
    @ColumnInfo(name = "tag_name")
    var tagName: String,

    @Json(name = "target_commitish")
    @ColumnInfo(name = "target_commitish")
    var targetCommitish: String,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "draft")
    var draft: Boolean,

    @Embedded(prefix = "author_")
    var author: EventActor,

    @ColumnInfo(name = "prerelease")
    var prerelease: Boolean,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "published_at")
    @ColumnInfo(name = "published_at")
    var publishedAt: Date,

    @ColumnInfo(name = "body")
    var body: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventProjectCard(

    @Json(name = "column_id")
    @ColumnInfo(name = "column_id")
    var columnId: Long,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "note")
    var note: String,

    @Embedded(prefix = "creator_")
    var creator: EventActor,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventProjectColumn(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventProject(

    @Json(name = "html_url")
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
    var creator: EventActor,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventGistFile(

    @ColumnInfo(name = "filename")
    var filename: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "language")
    var language: String,

    @Json(name = "raw_url")
    @ColumnInfo(name = "raw_url")
    var rawUrl: String,

    @ColumnInfo(name = "size")
    var size: Long

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventIssue(

    @Json(name = "repository_url")
    @ColumnInfo(name = "repository_url")
    var repositoryUrl: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "number")
    var number: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @Embedded(prefix = "user_")
    var user: EventActor

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class EventMembership(

    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "role")
    var role: String,

    @Embedded(prefix = "user_")
    var user: EventActor

) : Parcelable