package io.github.tonnyl.moka.data

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.profile.ProfileType
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "notification")
@Serializable
data class Notification(

    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String,

    @Embedded(prefix = "repository_")
    @Contextual
    var repository: NotificationRepository,

    @Embedded(prefix = "subject_")
    @Contextual
    var subject: NotificationRepositorySubject,

    @ColumnInfo(name = "reason")
    var reason: NotificationReasons,

    @ColumnInfo(name = "unread")
    var unread: Boolean,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant,

    @SerialName("last_read_at")
    @ColumnInfo(name = "last_read_at")
    @Contextual
    var lastReadAt: Instant? = null,

    @ColumnInfo(name = "url")
    var url: String,

    // local only
    // indicates if the notification has been displayed to user.
    @ColumnInfo(name = "has_displayed")
    @Transient
    var hasDisplayed: Boolean = false

)

@Serializable
data class NotificationRepository(

    @ColumnInfo(name = "id")
    var id: Long,

    @SerialName("node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "name")
    var name: String,

    @SerialName("full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String,

    @Embedded(prefix = "owner_")
    @Contextual
    var owner: NotificationRepositoryOwner,

    // note the difference of serialized name, column name and field name
    @SerialName("private")
    @ColumnInfo(name = "is_private")
    var isPrivate: Boolean,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "fork")
    var fork: Boolean,

    @ColumnInfo(name = "url")
    var url: String

)

@Serializable
data class NotificationRepositoryOwner(

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @SerialName("node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @SerialName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @SerialName("gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var gravatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerialName("followers_url")
    @ColumnInfo(name = "followers_url")
    var followersUrl: String,

    @SerialName("following_url")
    @ColumnInfo(name = "following_url")
    var followingUrl: String,

    @SerialName("gists_url")
    @ColumnInfo(name = "gists_url")
    var gistsUrl: String,

    @SerialName("starred_url")
    @ColumnInfo(name = "starred_url")
    var starredUrl: String,

    @SerialName("subscriptions_url")
    @ColumnInfo(name = "subscriptions_url")
    var subscriptionsUrl: String,

    @SerialName("organizations_url")
    @ColumnInfo(name = "organizations_url")
    var organizationsUrl: String,

    @SerialName("repos_url")
    @ColumnInfo(name = "repos_url")
    var reposUrl: String,

    @SerialName("events_url")
    @ColumnInfo(name = "events_url")
    var eventsUrl: String,

    @SerialName("received_events_url")
    @ColumnInfo(name = "received_events_url")
    var receivedEventsUrl: String,

    @ColumnInfo(name = "type")
    var type: String,

    @SerialName("site_admin")
    @ColumnInfo(name = "site_admin")
    var siteAdmin: Boolean

)

@Serializable
data class NotificationRepositorySubject(

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "url")
    var url: String,

    @SerialName("latest_comment_url")
    @ColumnInfo(name = "latest_comment_url")
    var latestCommentUrl: String? = null,

    @ColumnInfo(name = "type")
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

val NotificationRepositoryOwner.profileType: ProfileType
    get() = when (type) {
        "Organization" -> {
            ProfileType.ORGANIZATION
        }
        "User" -> {
            ProfileType.USER
        }
        else -> {
            ProfileType.NOT_SPECIFIED
        }
    }

fun Notification?.toDisplayContentText(context: Context): CharSequence {
    this ?: return ""
    val typeRes = when (reason) {
        NotificationReasons.ASSIGN -> {
            R.string.notification_reason_assign
        }
        NotificationReasons.AUTHOR -> {
            R.string.notification_reason_author
        }
        NotificationReasons.COMMENT -> {
            R.string.notification_reason_comment
        }
        NotificationReasons.INVITATION -> {
            R.string.notification_reason_invitation
        }
        NotificationReasons.MANUAL -> {
            R.string.notification_reason_manual
        }
        NotificationReasons.MENTION -> {
            R.string.notification_reason_mention
        }
        NotificationReasons.REVIEW_REQUESTED -> {
            R.string.notification_reason_review_requested
        }
        NotificationReasons.STATE_CHANGE -> {
            R.string.notification_reason_state_change
        }
        NotificationReasons.SUBSCRIBED -> {
            R.string.notification_reason_subscribed
        }
        NotificationReasons.TEAM_MENTION -> {
            R.string.notification_reason_team_mention
        }
        else -> {
            R.string.notification_reason_other
        }
    }
    val notificationReasonContent = context.getString(typeRes)
    val notificationReasonPlusHyphen = context.getString(
        R.string.notification_caption_notification_type,
        notificationReasonContent
    )
    val spannable = SpannableString(notificationReasonPlusHyphen + subject.title)

    val span = ForegroundColorSpan(
        ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)
    )
    spannable.setSpan(
        span,
        0,
        notificationReasonPlusHyphen.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannable
}