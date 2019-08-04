package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@SuppressLint("ParcelCreator")
data class EventPayload(

    @SerializedName("action")
    @ColumnInfo(name = "action")
    var action: String?,

    @SerializedName("comment")
    @Embedded(prefix = "comment_")
    var comment: EventComment?,

    @SerializedName("commit_comment")
    @Embedded(prefix = "commit_comment_")
    var commitComment: EventComment?,

    @SerializedName("issue")
    @Embedded(prefix = "issue_")
    var issue: EventIssue?,

    @SerializedName("pull_request")
    @Embedded(prefix = "pull_request_")
    var pullRequest: EventPullRequest?,

    @SerializedName("review")
    @Embedded(prefix = "review_")
    var review: EventReview?,

    /**
     * Only for [Event.DOWNLOAD_EVENT].
     */
    @SerializedName("download")
    @Embedded(prefix = "download_")
    var download: EventDownload?,

    /**
     * Only for [Event.FOLLOW_EVENT]
     */
    @SerializedName("target")
    @Embedded(prefix = "target_")
    var target: EventActor?,

    /**
     * Only for [Event.FORK_EVENT]
     */
    @SerializedName("forkee")
    @Embedded(prefix = "forkee_")
    var forkee: EventRepository?,

    /**
     * Only for [Event.GIST_EVENT]
     */
    @SerializedName("gist")
    @Embedded(prefix = "gist_")
    var gist: Gist?,

    /**
     * Only for [Event.GOLLUM_EVENT]
     */
    @SerializedName("pages")
    @ColumnInfo(name = "pages")
    var pages: List<EventGollumPage>?,

    /**
     * Only for [Event.MEMBER_EVENT]
     */
    @SerializedName("member")
    @Embedded(prefix = "member_")
    var member: EventActor?,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @SerializedName("team")
    @Embedded(prefix = "team_")
    var team: EventTeam?,

    /**
     * Only for [Event.TEAM_ADD_EVENT]
     */
    @SerializedName("organization")
    @Embedded(prefix = "organization_")
    var organization: EventActor?,

    /**
     * Only for [Event.RELEASE_EVENT]
     */
    @SerializedName("release")
    @Embedded(prefix = "release_")
    var release: EventRelease?,

    /**
     * Only for [Event.ORG_BLOCK_EVENT]
     */
    @SerializedName("blocked_user")
    @Embedded(prefix = "blocked_user_")
    var blockedUser: EventActor?,

    /**
     * Only for [Event.PROJECT_CARD_EVENT]
     */
    @SerializedName("project_card")
    @Embedded(prefix = "project_card_")
    var projectCard: EventProjectCard?,

    /**
     * Only for [Event.PROJECT_COLUMN_EVENT]
     */
    @SerializedName("project_column")
    @Embedded(prefix = "project_column_")
    var projectColumn: EventProjectColumn?,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @SerializedName("membership")
    @Embedded(prefix = "membership_")
    var membership: EventMembership?,

    /**
     * Only for [Event.ORGANIZATION_EVENT]
     */
    @SerializedName("invitation")
    @Embedded(prefix = "invitation_")
    var invitation: EventActor?,

    /**
     * Only for [Event.PROJECT_EVENT]
     */
    @SerializedName("project")
    @Embedded(prefix = "project_")
    var project: EventProject?,

    /**
     * Only for [Event.PUSH_EVENT]
     *
     * The number of commits in the push.
     */
    @SerializedName("size")
    @ColumnInfo(name = "size")
    var size: Int?,

    @SerializedName("ref_type")
    @ColumnInfo(name = "ref_type")
    var refType: String?,

    @SerializedName("ref")
    @ColumnInfo(name = "ref")
    var ref: String?

) : Parcelable

@Parcelize
data class EventComment(

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("user")
    @Embedded(prefix = "user_")
    var user: EventActor,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @SerializedName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String,

    @SerializedName("body")
    @ColumnInfo(name = "body")
    var body: String,

    @SerializedName("commit_id")
    @ColumnInfo(name = "commit_id")
    var commitId: String?

) : Parcelable

@Parcelize
data class EventPullRequest(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("diff_url")
    @ColumnInfo(name = "diff_url")
    var diffUrl: String,

    @SerializedName("patch_url")
    @ColumnInfo(name = "patch_url")
    var patchUrl: String,

    @SerializedName("number")
    @ColumnInfo(name = "number")
    var number: Long,

    @SerializedName("state")
    @ColumnInfo(name = "state")
    var state: String,

    @SerializedName("locked")
    @ColumnInfo(name = "locked")
    var locked: Boolean,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title: String,

    @SerializedName("user")
    @Embedded(prefix = "user_")
    var user: EventActor,

    @SerializedName("body")
    @ColumnInfo(name = "body")
    var body: String?,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @SerializedName("closed_at")
    @ColumnInfo(name = "closed_at")
    var closedAt: Date?,

    @SerializedName("merged_at")
    @ColumnInfo(name = "merged_at")
    var mergedAt: Date?,

    @SerializedName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

) : Parcelable

@Parcelize
data class EventReview(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: String,

    @SerializedName("user")
    @Embedded(prefix = "user_")
    var user: EventActor,

    @SerializedName("body")
    @ColumnInfo(name = "body")
    var body: String?,

    @SerializedName("submitted_at")
    @ColumnInfo(name = "submitted_at")
    var submittedAt: Date,

    @SerializedName("state")
    @ColumnInfo(name = "state")
    var state: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("author_association")
    @ColumnInfo(name = "author_association")
    var authorAssociation: String

) : Parcelable

@Parcelize
data class EventDownload(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String,

    @SerializedName("size")
    @ColumnInfo(name = "size")
    var size: Long,

    @SerializedName("download_count")
    @ColumnInfo(name = "download_count")
    var downloadCount: Long,

    @SerializedName("content_type")
    @ColumnInfo(name = "content_type")
    var contentType: String

) : Parcelable

@Parcelize
data class EventGollumPage(

    /**
     * The name of the page.
     */
    @SerializedName("page_name")
    var pageName: String,

    /**
     * The current page title.
     */
    @SerializedName("title")
    var title: String,

    @SerializedName("summary")
    var summary: String?,

    /**
     * The action that was performed on the page. Can be created or edited.
     */
    @SerializedName("action")
    var action: String,

    /**
     * The latest commit SHA of the page.
     */
    @SerializedName("sha")
    var sha: String,

    /**
     * Points to the HTML wiki page.
     */
    @SerializedName("html_url")
    var htmlUrl: String

) : Parcelable

@Parcelize
data class EventTeam(

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("slug")
    @ColumnInfo(name = "slug")
    var slug: String,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String,

    @SerializedName("privacy")
    @ColumnInfo(name = "privacy")
    var privacy: Boolean,

    @SerializedName("permission")
    @ColumnInfo(name = "permission")
    var permission: String

) : Parcelable

@Parcelize
data class EventRelease(

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("tag_name")
    @ColumnInfo(name = "tag_name")
    var tagName: String,

    @SerializedName("target_commitish")
    @ColumnInfo(name = "target_commitish")
    var targetCommitish: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String?,

    @SerializedName("draft")
    @ColumnInfo(name = "draft")
    var draft: Boolean,

    @SerializedName("author")
    @Embedded(prefix = "author_")
    var author: EventActor,

    @SerializedName("prerelease")
    @ColumnInfo(name = "prerelease")
    var prerelease: Boolean,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("published_at")
    @ColumnInfo(name = "published_at")
    var publishedAt: Date,

    @SerializedName("body")
    @ColumnInfo(name = "body")
    var body: String

) : Parcelable

@Parcelize
data class EventProjectCard(

    @SerializedName("column_id")
    @ColumnInfo(name = "column_id")
    var columnId: Long,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("note")
    @ColumnInfo(name = "note")
    var note: String,

    @SerializedName("creator")
    @Embedded(prefix = "creator_")
    var creator: EventActor,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
data class EventProjectColumn(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
data class EventProject(

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("body")
    @ColumnInfo(name = "body")
    var body: String,

    @SerializedName("number")
    @ColumnInfo(name = "number")
    var number: Int,

    @SerializedName("state")
    @ColumnInfo(name = "state")
    var state: String,

    @SerializedName("creator")
    @Embedded(prefix = "creator_")
    var creator: EventActor,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date

) : Parcelable

@Parcelize
data class EventGistFile(

    @SerializedName("filename")
    @ColumnInfo(name = "filename")
    var filename: String,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: String,

    @SerializedName("language")
    @ColumnInfo(name = "language")
    var language: String,

    @SerializedName("raw_url")
    @ColumnInfo(name = "raw_url")
    var rawUrl: String,

    @SerializedName("size")
    @ColumnInfo(name = "size")
    var size: Long

) : Parcelable

@Parcelize
data class EventIssue(

    @SerializedName("repository_url")
    @ColumnInfo(name = "repository_url")
    var repositoryUrl: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("number")
    @ColumnInfo(name = "number")
    var number: Int,

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title: String,

    @SerializedName("user")
    @Embedded(prefix = "user_")
    var user: EventActor

) : Parcelable

@Parcelize
data class EventMembership(

    @SerializedName("state")
    @ColumnInfo(name = "state")
    var state: String,

    @SerializedName("role")
    @ColumnInfo(name = "role")
    var role: String,

    @SerializedName("user")
    @Embedded(prefix = "user_")
    var user: EventActor

) : Parcelable