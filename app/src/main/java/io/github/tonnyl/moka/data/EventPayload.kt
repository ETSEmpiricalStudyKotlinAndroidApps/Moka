package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@SuppressLint("ParcelCreator")
data class EventPayload(
        @SerializedName("action")
        val action: String,

        @SerializedName("comment")
        val comment: EventComment?,

        @SerializedName("commit_comment")
        val commitComment: EventComment?,

        @SerializedName("issue")
        val issue: EventIssue?,

        @SerializedName("pull_request")
        val pullRequest: EventPullRequest?,

        @SerializedName("review")
        val review: EventReview?,

        /**
         * Only for [Event.DOWNLOAD_EVENT].
         */
        @SerializedName("download")
        val download: EventDownload?,

        /**
         * Only for [Event.FOLLOW_EVENT]
         */
        @SerializedName("target")
        val target: EventActor?,

        /**
         * Only for [Event.FORK_EVENT]
         */
        @SerializedName("forkee")
        val forkee: EventRepository?,

        /**
         * Only for [Event.GIST_EVENT]
         */
        @SerializedName("gist")
        val gist: Gist?,

        /**
         * Only for [Event.GOLLUM_EVENT]
         */
        @SerializedName("pages")
        val pages: List<EventGollumPage>?,

        /**
         * Only for [Event.MEMBER_EVENT]
         */
        @SerializedName("member")
        val member: EventActor?,

        /**
         * Only for [Event.TEAM_ADD_EVENT]
         */
        @SerializedName("team")
        val team: EventTeam?,

        /**
         * Only for [Event.TEAM_ADD_EVENT]
         */
        @SerializedName("organization")
        val organization: EventActor?,

        /**
         * Only for [Event.RELEASE_EVENT]
         */
        @SerializedName("release")
        val release: EventRelease?,

        /**
         * Only for [Event.ORG_BLOCK_EVENT]
         */
        @SerializedName("blocked_user")
        val blockedUser: EventActor?,

        /**
         * Only for [Event.PROJECT_CARD_EVENT]
         */
        @SerializedName("project_card")
        val projectCard: EventProjectCard?,

        /**
         * Only for [Event.PROJECT_COLUMN_EVENT]
         */
        @SerializedName("project_column")
        val projectColumn: EventProjectColumn?,

        /**
         * Only for [Event.ORGANIZATION_EVENT]
         */
        @SerializedName("membership")
        val membership: EventMembership?,

        /**
         * Only for [Event.ORGANIZATION_EVENT]
         */
        @SerializedName("invitation")
        val invitation: EventActor?,

        /**
         * Only for [Event.PROJECT_EVENT]
         */
        @SerializedName("project")
        val project: EventProject?,

        /**
         * Only for [Event.PUSH_EVENT]
         *
         * The number of commits in the push.
         */
        @SerializedName("size")
        val size: Int?,

        @SerializedName("ref_type")
        val refType: String?,

        @SerializedName("ref")
        val ref: String?
) : Parcelable

@Parcelize
data class EventComment(
        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("id")
        val id: Long,

        @SerializedName("user")
        val user: EventActor,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date,

        @SerializedName("author_association")
        val authorAssociation: String,

        @SerializedName("body")
        val body: String,

        @SerializedName("commit_id")
        val commitId: String?
) : Parcelable

@Parcelize
data class EventPullRequest(
        @SerializedName("id")
        val id: String,

        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("diff_url")
        val diffUrl: String,

        @SerializedName("patch_url")
        val patchUrl: String,

        @SerializedName("number")
        val number: Long,

        @SerializedName("state")
        val state: String,

        @SerializedName("locked")
        val locked: Boolean,

        @SerializedName("title")
        val title: String,

        @SerializedName("user")
        val user: EventActor,

        @SerializedName("body")
        val body: String?,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date,

        @SerializedName("closed_at")
        val closedAt: Date?,

        @SerializedName("merged_at")
        val mergedAt: Date?,

        @SerializedName("author_association")
        val authorAssociation: String
) : Parcelable

@Parcelize
data class EventReview(
        @SerializedName("id")
        val id: String,

        @SerializedName("user")
        val user: EventActor,

        @SerializedName("body")
        val body: String?,

        @SerializedName("submitted_at")
        val submittedAt: Date,

        @SerializedName("state")
        val state: String,

        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("author_association")
        val authorAssociation: String
) : Parcelable

@Parcelize
data class EventDownload(
        @SerializedName("id")
        val id: Int,

        @SerializedName("name")
        val name: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("size")
        val size: Long,

        @SerializedName("download_count")
        val downloadCount: Long,

        @SerializedName("content_type")
        val contentType: String
) : Parcelable

@Parcelize
data class EventGollumPage(
        /**
         * The name of the page.
         */
        @SerializedName("page_name")
        val pageName: String,

        /**
         * The current page title.
         */
        @SerializedName("title")
        val title: String,

        @SerializedName("summary")
        val summary: String?,

        /**
         * The action that was performed on the page. Can be created or edited.
         */
        @SerializedName("action")
        val action: String,

        /**
         * The latest commit SHA of the page.
         */
        @SerializedName("sha")
        val sha: String,

        /**
         * Points to the HTML wiki page.
         */
        @SerializedName("html_url")
        val htmlUrl: String
) : Parcelable

@Parcelize
data class EventTeam(
        @SerializedName("name")
        val name: String,

        @SerializedName("id")
        val id: Long,

        @SerializedName("slug")
        val slug: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("privacy")
        val privacy: Boolean,

        @SerializedName("permission")
        val permission: String
) : Parcelable

@Parcelize
data class EventRelease(
        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("id")
        val id: Long,

        @SerializedName("tag_name")
        val tagName: String,

        @SerializedName("target_commitish")
        val targetCommitish: String,

        @SerializedName("name")
        val name: String?,

        @SerializedName("draft")
        val draft: Boolean,

        @SerializedName("author")
        val author: EventActor,

        @SerializedName("prerelease")
        val prerelease: Boolean,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("published_at")
        val publishedAt: Date,

        @SerializedName("body")
        val body: String
) : Parcelable

@Parcelize
data class EventProjectCard(
        @SerializedName("column_id")
        val columnId: Long,

        @SerializedName("id")
        val id: Long,

        @SerializedName("note")
        val note: String,

        @SerializedName("creator")
        val creator: EventActor,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date
) : Parcelable

@Parcelize
data class EventProjectColumn(
        @SerializedName("id")
        val id: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date
) : Parcelable

@Parcelize
data class EventProject(
        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("id")
        val id: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("body")
        val body: String,

        @SerializedName("number")
        val number: Int,

        @SerializedName("state")
        val state: String,

        @SerializedName("creator")
        val creator: EventActor,

        @SerializedName("created_at")
        val createdAt: Date,

        @SerializedName("updated_at")
        val updatedAt: Date
) : Parcelable

@Parcelize
data class EventGistFile(
        @SerializedName("filename")
        val filename: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("language")
        val language: String,

        @SerializedName("raw_url")
        val rawUrl: String,

        @SerializedName("size")
        val size: Long
) : Parcelable

@Parcelize
data class EventIssue(
        @SerializedName("repository_url")
        val repositoryUrl: String,

        @SerializedName("html_url")
        val htmlUrl: String,

        @SerializedName("id")
        val id: Long,

        @SerializedName("number")
        val number: Int,

        @SerializedName("title")
        val title: String,

        @SerializedName("user")
        val user: EventActor
) : Parcelable

@Parcelize
data class EventMembership(
        @SerializedName("state")
        val state: String,

        @SerializedName("role")
        val role: String,

        @SerializedName("user")
        val user: EventActor
) : Parcelable