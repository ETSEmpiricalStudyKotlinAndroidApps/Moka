package io.tonnyl.moka.common.db.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import kotlinx.datetime.Instant
import io.tonnyl.moka.common.data.Event as SerializableEvent
import io.tonnyl.moka.common.data.EventComment as SerializableEventComment
import io.tonnyl.moka.common.data.EventDownload as SerializableEventDownload
import io.tonnyl.moka.common.data.EventGistFile as SerializableEventGistFile
import io.tonnyl.moka.common.data.EventGollumPage as SerializableEventGollumPage
import io.tonnyl.moka.common.data.EventIssue as SerializableEventIssue
import io.tonnyl.moka.common.data.EventMembership as SerializableEventMembership
import io.tonnyl.moka.common.data.EventPayload as SerializableEventPayload
import io.tonnyl.moka.common.data.EventProject as SerializableEventProject
import io.tonnyl.moka.common.data.EventProjectCard as SerializableEventProjectCard
import io.tonnyl.moka.common.data.EventProjectColumn as SerializableEventProjectColumn
import io.tonnyl.moka.common.data.EventPullRequest as SerializableEventPullRequest
import io.tonnyl.moka.common.data.EventRelease as SerializableEventRelease
import io.tonnyl.moka.common.data.EventReview as SerializableEventReview
import io.tonnyl.moka.common.data.EventTeam as SerializableEventTeam

data class EventPayload(

    @ColumnInfo(name = "action")
    var action: String? = null,

    @Embedded(prefix = "comment_")
    var comment: EventComment? = null,

    @Embedded(prefix = "commit_comment_")
    var commitComment: EventComment? = null,

    @Embedded(prefix = "issue_")
    var issue: EventIssue? = null,

    @Embedded(prefix = "pull_request_")
    var pullRequest: EventPullRequest? = null,

    @Embedded(prefix = "review_")
    var review: EventReview? = null,

    /**
     * Only for [SerializableEvent.DOWNLOAD_EVENT].
     */
    @Embedded(prefix = "download_")
    var download: EventDownload? = null,

    /**
     * Only for [SerializableEvent.FOLLOW_EVENT]
     */
    @Embedded(prefix = "target_")
    var target: EventActor? = null,

    /**
     * Only for [SerializableEvent.FORK_EVENT]
     */
    @Embedded(prefix = "forkee_")
    var forkee: EventRepository? = null,

    /**
     * Only for [SerializableEvent.GIST_EVENT]
     */
    @Embedded(prefix = "gist_")
    var gist: Gist? = null,

    /**
     * Only for [SerializableEvent.GOLLUM_EVENT]
     */
    @ColumnInfo(name = "pages")
    var pages: List<EventGollumPage>? = null,

    /**
     * Only for [SerializableEvent.MEMBER_EVENT]
     */
    @Embedded(prefix = "member_")
    var member: EventActor? = null,

    /**
     * Only for [SerializableEvent.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "team_")
    var team: EventTeam? = null,

    /**
     * Only for [SerializableEvent.TEAM_ADD_EVENT]
     */
    @Embedded(prefix = "organization_")
    var organization: EventActor? = null,

    /**
     * Only for [SerializableEvent.RELEASE_EVENT]
     */
    @Embedded(prefix = "release_")
    var release: EventRelease? = null,

    /**
     * Only for [SerializableEvent.ORG_BLOCK_EVENT]
     */
    @Embedded(prefix = "blocked_user_")
    var blockedUser: EventActor? = null,

    /**
     * Only for [SerializableEvent.PROJECT_CARD_EVENT]
     */
    @Embedded(prefix = "project_card_")
    var projectCard: EventProjectCard? = null,

    /**
     * Only for [SerializableEvent.PROJECT_COLUMN_EVENT]
     */
    @Embedded(prefix = "project_column_")
    var projectColumn: EventProjectColumn? = null,

    /**
     * Only for [SerializableEvent.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "membership_")
    var membership: EventMembership? = null,

    /**
     * Only for [SerializableEvent.ORGANIZATION_EVENT]
     */
    @Embedded(prefix = "invitation_")
    var invitation: EventActor? = null,

    /**
     * Only for [SerializableEvent.PROJECT_EVENT]
     */
    @Embedded(prefix = "project_")
    var project: EventProject? = null,

    /**
     * Only for [SerializableEvent.PUSH_EVENT]
     *
     * The number of commits in the push.
     */
    @ColumnInfo(name = "size")
    var size: Int? = null,

    @ColumnInfo(name = "ref_type")
    var refType: String? = null,

    @ColumnInfo(name = "ref")
    var ref: String? = null

)

data class EventComment(

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @Embedded(prefix = "user_")
    var user: EventActor,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant,

    @ColumnInfo(name = "author_association")
    var authorAssociation: String,

    @ColumnInfo(name = "body")
    var body: String,

    @ColumnInfo(name = "commit_id")
    var commitId: String? = null

)

data class EventPullRequest(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "diff_url")
    var diffUrl: String,

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
    var body: String? = null,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant,

    @ColumnInfo(name = "closed_at")
    var closedAt: Instant? = null,

    @ColumnInfo(name = "merged_at")
    var mergedAt: Instant? = null,

    @ColumnInfo(name = "author_association")
    var authorAssociation: String

)

data class EventReview(

    @ColumnInfo(name = "id")
    var id: String,

    @Embedded(prefix = "user_")
    var user: EventActor,

    @ColumnInfo(name = "body")
    var body: String?,

    @ColumnInfo(name = "submitted_at")
    var submittedAt: Instant,

    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "author_association")
    var authorAssociation: String

)

data class EventDownload(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "size")
    var size: Long,

    @ColumnInfo(name = "download_count")
    var downloadCount: Long,

    @ColumnInfo(name = "content_type")
    var contentType: String

)

data class EventGollumPage(

    /**
     * The name of the page.
     */
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
    var htmlUrl: String

)

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

data class EventRelease(

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "tag_name")
    var tagName: String,

    @ColumnInfo(name = "target_commitish")
    var targetCommitish: String,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "draft")
    var draft: Boolean,

    @Embedded(prefix = "author_")
    var author: EventActor,

    @ColumnInfo(name = "prerelease")
    var prerelease: Boolean,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "published_at")
    var publishedAt: Instant,

    @ColumnInfo(name = "body")
    var body: String?

)

data class EventProjectCard(

    @ColumnInfo(name = "column_id")
    var columnId: Long,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "note")
    var note: String,

    @Embedded(prefix = "creator_")
    var creator: EventActor,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant

)

data class EventProjectColumn(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant

)

data class EventProject(

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

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant

)

data class EventGistFile(

    @ColumnInfo(name = "filename")
    var filename: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "language")
    var language: String,

    @ColumnInfo(name = "raw_url")
    var rawUrl: String,

    @ColumnInfo(name = "size")
    var size: Long

)

data class EventIssue(

    @ColumnInfo(name = "repository_url")
    var repositoryUrl: String,

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

)

data class EventMembership(

    @ColumnInfo(name = "state")
    var state: String,

    @ColumnInfo(name = "role")
    var role: String,

    @Embedded(prefix = "user_")
    var user: EventActor

)

val SerializableEventPayload.dbModel: EventPayload
    get() = EventPayload(
        action = action,
        comment = comment?.dbModel,
        commitComment = commitComment?.dbModel,
        issue = issue?.dbModel,
        pullRequest = pullRequest?.dbModel,
        review = review?.dbModel,
        download = download?.dbModel,
        target = target?.dbModel,
        forkee = forkee?.dbModel,
        gist = gist?.toDbModel,
        pages = pages?.map { it.dbModel },
        member = member?.dbModel,
        team = team?.dbModel,
        organization = organization?.dbModel,
        release = release?.dbModel,
        blockedUser = blockedUser?.dbModel,
        projectCard = projectCard?.dbModel,
        projectColumn = projectColumn?.dbModel,
        membership = membership?.dbModel,
        invitation = invitation?.dbModel,
        project = project?.dbModel,
        size = size,
        refType = refType,
        ref = ref
    )

val SerializableEventComment.dbModel: EventComment
    get() = EventComment(
        htmlUrl = htmlUrl,
        id = id,
        user = user.dbModel,
        createdAt = createdAt,
        updatedAt = updatedAt,
        authorAssociation = authorAssociation,
        body = body,
        commitId = commitId
    )

val SerializableEventPullRequest.dbModel: EventPullRequest
    get() = EventPullRequest(
        id = id,
        htmlUrl = htmlUrl,
        diffUrl = diffUrl,
        patchUrl = patchUrl,
        number = number,
        state = state,
        locked = locked,
        title = title,
        user = user.dbModel,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt,
        closedAt = closedAt,
        mergedAt = mergedAt,
        authorAssociation = authorAssociation
    )

val SerializableEventIssue.dbModel: EventIssue
    get() = EventIssue(
        repositoryUrl = repositoryUrl,
        htmlUrl = htmlUrl,
        id = id,
        number = number,
        title = title,
        user = user.dbModel
    )

val SerializableEventReview.dbModel: EventReview
    get() = EventReview(
        id = id,
        user = user.dbModel,
        body = body,
        submittedAt = submittedAt,
        state = state,
        htmlUrl = htmlUrl,
        authorAssociation = authorAssociation
    )

val SerializableEventDownload.dbModel: EventDownload
    get() = EventDownload(
        id = id,
        name = name,
        description = description,
        size = size,
        downloadCount = downloadCount,
        contentType = contentType
    )

val SerializableEventGistFile.dbModel: EventGistFile
    get() = EventGistFile(
        filename = filename,
        type = type,
        language = language,
        rawUrl = rawUrl,
        size = size
    )

val SerializableEventGollumPage.dbModel: EventGollumPage
    get() = EventGollumPage(
        pageName = pageName,
        title = title,
        summary = summary,
        action = action,
        sha = sha,
        htmlUrl = htmlUrl
    )

val SerializableEventTeam.dbModel: EventTeam
    get() = EventTeam(
        name = name,
        id = id,
        slug = slug,
        description = description,
        privacy = privacy,
        permission = permission
    )

val SerializableEventRelease.dbModel: EventRelease
    get() = EventRelease(
        htmlUrl = htmlUrl,
        id = id,
        tagName = tagName,
        targetCommitish = targetCommitish,
        name = name,
        draft = draft,
        author = author.dbModel,
        prerelease = prerelease,
        createdAt = createdAt,
        publishedAt = publishedAt,
        body = body
    )

val SerializableEventProjectCard.dbModel: EventProjectCard
    get() = EventProjectCard(
        columnId = columnId,
        id = id,
        note = note,
        creator = creator.dbModel,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

val SerializableEventProjectColumn.dbModel: EventProjectColumn
    get() = EventProjectColumn(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

val SerializableEventMembership.dbModel: EventMembership
    get() = EventMembership(
        state = state,
        role = role,
        user = user.dbModel
    )

val SerializableEventProject.dbModel: EventProject
    get() = EventProject(
        htmlUrl = htmlUrl,
        id = id,
        name = name,
        body = body,
        number = number,
        state = state,
        creator = creator.dbModel,
        createdAt = createdAt,
        updatedAt = updatedAt
    )