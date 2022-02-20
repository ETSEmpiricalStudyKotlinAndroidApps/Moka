package io.tonnyl.moka.common.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(

    var id: String,

    @Contextual
    var repository: NotificationRepository,

    @Contextual
    var subject: NotificationRepositorySubject,

    var reason: NotificationReasons,

    var unread: Boolean,

    @SerialName("updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("last_read_at")
    @Contextual
    var lastReadAt: Instant? = null,

    var url: String

)

@Serializable
data class NotificationRepository(

    var id: Long,

    @SerialName("node_id")
    var nodeId: String,

    var name: String,

    @SerialName("full_name")
    var fullName: String,

    @Contextual
    var owner: NotificationRepositoryOwner,

    // note the difference of serialized name, column name and field name
    @SerialName("private")
    var isPrivate: Boolean,

    @SerialName("html_url")
    var htmlUrl: String,

    var description: String? = null,

    var fork: Boolean,

    var url: String

)

@Serializable
data class NotificationRepositoryOwner(

    var login: String,

    var id: Long,

    @SerialName("node_id")
    var nodeId: String,

    @SerialName("avatar_url")
    var avatarUrl: String,

    @SerialName("gravatar_id")
    var gravatarId: String,

    var url: String,

    @SerialName("html_url")
    var htmlUrl: String,

    @SerialName("followers_url")
    var followersUrl: String,

    @SerialName("following_url")
    var followingUrl: String,

    @SerialName("gists_url")
    var gistsUrl: String,

    @SerialName("starred_url")
    var starredUrl: String,

    @SerialName("subscriptions_url")
    var subscriptionsUrl: String,

    @SerialName("organizations_url")
    var organizationsUrl: String,

    @SerialName("repos_url")
    var reposUrl: String,

    @SerialName("events_url")
    var eventsUrl: String,

    @SerialName("received_events_url")
    var receivedEventsUrl: String,

    var type: String,

    @SerialName("site_admin")
    var siteAdmin: Boolean

)

@Serializable
data class NotificationRepositorySubject(

    var title: String,

    var url: String,

    @SerialName("latest_comment_url")
    var latestCommentUrl: String? = null,

    var type: String

)

/**
 * When retrieving responses from the Notifications API, each payload has a key titled reason.
 * These correspond to events that trigger a notification.
 */
@Serializable
enum class NotificationReasons {

    /**
     * You were assigned to the Issue.
     */
    @SerialName("assign")
    ASSIGN,

    /**
     * You created the thread.
     */
    @SerialName("author")
    AUTHOR,

    /**
     * You commented on the thread.
     */
    @SerialName("comment")
    COMMENT,

    /**
     * You accepted an invitation to contribute to the repository.
     */
    @SerialName("invitation")
    INVITATION,

    /**
     * You subscribed to the thread (via an Issue or Pull Request).
     */
    @SerialName("manual")
    MANUAL,

    /**
     * You were specifically @mentioned in the content.
     */
    @SerialName("mention")
    MENTION,

    /**
     * You, or a team you're a member of, were requested to review a pull request.
     */
    @SerialName("review_requested")
    REVIEW_REQUESTED,

    /**
     * You changed the thread state (for example, closing an Issue or merging a Pull Request).
     */
    @SerialName("state_change")
    STATE_CHANGE,

    /**
     * You're watching the repository.
     */
    @SerialName("subscribed")
    SUBSCRIBED,

    /**
     * You were on a team that was mentioned.
     */
    @SerialName("team_mention")
    TEAM_MENTION,

    /**
     * LOCAL FALLBACK ONLY. You will never get an OTHER from server.
     */
    @SerialName("other")
    OTHER,

}

@Serializable
enum class SubjectType {

    PullRequest,

    Issue,

    Release

}