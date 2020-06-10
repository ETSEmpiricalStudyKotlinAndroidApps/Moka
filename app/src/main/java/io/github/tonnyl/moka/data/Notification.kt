package io.github.tonnyl.moka.data

import android.content.Context
import android.os.Parcelable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.profile.ProfileType
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "notification")
@JsonClass(generateAdapter = true)
data class Notification(

    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String,

    @Embedded(prefix = "repository_")
    var repository: NotificationRepository,

    @Embedded(prefix = "subject_")
    var subject: NotificationRepositorySubject,

    @ColumnInfo(name = "reason")
    var reason: NotificationReasons,

    @ColumnInfo(name = "unread")
    var unread: Boolean,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @Json(name = "last_read_at")
    @ColumnInfo(name = "last_read_at")
    var lastReadAt: Date?,

    @ColumnInfo(name = "url")
    var url: String,

    // local only
    // indicates if the notification has been displayed to user.
    @ColumnInfo(name = "has_displayed")
    @Transient
    var hasDisplayed: Boolean = false

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationRepository(

    @ColumnInfo(name = "id")
    var id: Long,

    @Json(name = "node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "name")
    var name: String,

    @Json(name = "full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String,

    @Embedded(prefix = "owner_")
    var owner: NotificationRepositoryOwner,

    // note the difference of serialized name, column name and field name
    @Json(name = "private")
    @ColumnInfo(name = "is_private")
    var isPrivate: Boolean,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "fork")
    var fork: Boolean,

    @ColumnInfo(name = "url")
    var url: String

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationRepositoryOwner(

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @Json(name = "node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @Json(name = "avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @Json(name = "gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var gravatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @Json(name = "followers_url")
    @ColumnInfo(name = "followers_url")
    var followersUrl: String,

    @Json(name = "following_url")
    @ColumnInfo(name = "following_url")
    var followingUrl: String,

    @Json(name = "gists_url")
    @ColumnInfo(name = "gists_url")
    var gistsUrl: String,

    @Json(name = "starred_url")
    @ColumnInfo(name = "starred_url")
    var starredUrl: String,

    @Json(name = "subscriptions_url")
    @ColumnInfo(name = "subscriptions_url")
    var subscriptionsUrl: String,

    @Json(name = "organizations_url")
    @ColumnInfo(name = "organizations_url")
    var organizationsUrl: String,

    @Json(name = "repos_url")
    @ColumnInfo(name = "repos_url")
    var reposUrl: String,

    @Json(name = "events_url")
    @ColumnInfo(name = "events_url")
    var eventsUrl: String,

    @Json(name = "received_events_url")
    @ColumnInfo(name = "received_events_url")
    var receivedEventsUrl: String,

    @ColumnInfo(name = "type")
    var type: String,

    @Json(name = "site_admin")
    @ColumnInfo(name = "site_admin")
    var siteAdmin: Boolean

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class NotificationRepositorySubject(

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "url")
    var url: String,

    @Json(name = "latest_comment_url")
    @ColumnInfo(name = "latest_comment_url")
    var latestCommentUrl: String?,

    @ColumnInfo(name = "type")
    var type: String

) : Parcelable

/**
 * When retrieving responses from the Notifications API, each payload has a key titled reason.
 * These correspond to events that trigger a notification.
 */
enum class NotificationReasons {

    /**
     * You were assigned to the Issue.
     */
    ASSIGN,

    /**
     * You created the thread.
     */
    AUTHOR,

    /**
     * You commented on the thread.
     */
    COMMENT,

    /**
     * You accepted an invitation to contribute to the repository.
     */
    INVITATION,

    /**
     * You subscribed to the thread (via an Issue or Pull Request).
     */
    MANUAL,

    /**
     * You were specifically @mentioned in the content.
     */
    MENTION,

    /**
     * You, or a team you're a member of, were requested to review a pull request.
     */
    REVIEW_REQUESTED,

    /**
     * You changed the thread state (for example, closing an Issue or merging a Pull Request).
     */
    STATE_CHANGE,

    /**
     * You're watching the repository.
     */
    SUBSCRIBED,

    /**
     * You were on a team that was mentioned.
     */
    TEAM_MENTION,

    /**
     * LOCAL FALLBACK ONLY. You will never get an OTHER from server.
     */
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