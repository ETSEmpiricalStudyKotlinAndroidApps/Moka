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
import com.google.gson.annotations.SerializedName
import io.github.tonnyl.moka.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "notification")
data class Notification(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey
    var id: String,

    @SerializedName("repository")
    @Embedded(prefix = "repository_")
    var repository: NotificationRepository,

    @SerializedName("subject")
    @Embedded(prefix = "subject_")
    var subject: NotificationRepositorySubject,

    @SerializedName("reason")
    @ColumnInfo(name = "reason")
    var reason: String,

    @SerializedName("unread")
    @ColumnInfo(name = "unread")
    var unread: Boolean,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @SerializedName("last_read_at")
    @ColumnInfo(name = "last_read_at")
    var lastReadAt: Date,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    // local only
    // indicates if the notification has been displayed to user.
    @ColumnInfo(name = "has_displayed")
    var hasDisplayed: Boolean = false

) : Parcelable

@Parcelize
data class NotificationRepository(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String,

    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    var owner: NotificationRepositoryOwner,

    // note the difference of serialized name, column name and field name
    @SerializedName("private")
    @ColumnInfo(name = "is_private")
    var isPrivate: Boolean,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String?,

    @SerializedName("fork")
    @ColumnInfo(name = "fork")
    var fork: Boolean,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String

) : Parcelable

@Parcelize
data class NotificationRepositoryOwner(

    @SerializedName("login")
    @ColumnInfo(name = "login")
    var login: String,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("node_id")
    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @SerializedName("gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var gravatarId: String,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("followers_url")
    @ColumnInfo(name = "followers_url")
    var followersUrl: String,

    @SerializedName("following_url")
    @ColumnInfo(name = "following_url")
    var followingUrl: String,

    @SerializedName("gists_url")
    @ColumnInfo(name = "gists_url")
    var gistsUrl: String,

    @SerializedName("starred_url")
    @ColumnInfo(name = "starred_url")
    var starredUrl: String,

    @SerializedName("subscriptions_url")
    @ColumnInfo(name = "subscriptions_url")
    var subscriptionsUrl: String,

    @SerializedName("organizations_url")
    @ColumnInfo(name = "organizations_url")
    var organizationsUrl: String,

    @SerializedName("repos_url")
    @ColumnInfo(name = "repos_url")
    var reposUrl: String,

    @SerializedName("events_url")
    @ColumnInfo(name = "events_url")
    var eventsUrl: String,

    @SerializedName("received_events_url")
    @ColumnInfo(name = "received_events_url")
    var receivedEventsUrl: String,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: String,

    @SerializedName("site_admin")
    @ColumnInfo(name = "site_admin")
    var siteAdmin: Boolean

) : Parcelable

@Parcelize
data class NotificationRepositorySubject(

    @SerializedName("title")
    @ColumnInfo(name = "title")
    var title: String,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("latest_comment_url")
    @ColumnInfo(name = "latest_comment_url")
    var latestCommentUrl: String,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: String

) : Parcelable

/**
 * When retrieving responses from the Notifications API, each payload has a key titled reason.
 * These correspond to events that trigger a notification.
 */
enum class NotificationReasons(var value: String) {

    /**
     * You were assigned to the Issue.
     */
    ASSIGN("assign"),

    /**
     * You created the thread.
     */
    AUTHOR("author"),

    /**
     * You commented on the thread.
     */
    COMMENT("comment"),

    /**
     * You accepted an invitation to contribute to the repository.
     */
    INVITATION("invitation"),

    /**
     * You subscribed to the thread (via an Issue or Pull Request).
     */
    MANUAL("manual"),

    /**
     * You were specifically @mentioned in the content.
     */
    MENTION("mention"),

    /**
     * You, or a team you're a member of, were requested to review a pull request.
     */
    REVIEW_REQUESTED("review_requested"),

    /**
     * You changed the thread state (for example, closing an Issue or merging a Pull Request).
     */
    STATE_CHANGE("state_change"),

    /**
     * You're watching the repository.
     */
    SUBSCRIBED("subscribed"),

    /**
     * You were on a team that was mentioned.
     */
    TEAM_MENTION("team_mention"),

}

fun Notification.toDisplayContentText(context: Context): CharSequence {
    val notificationReasons = try {
        NotificationReasons.valueOf(reason.toUpperCase(Locale.US))
    } catch (e: Exception) {
        null
    }

    val typeRes = when (notificationReasons) {
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
        ResourcesCompat.getColor(context.resources, R.color.colorTextPrimary, null)
    )
    spannable.setSpan(
        span,
        0,
        notificationReasonPlusHyphen.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannable
}