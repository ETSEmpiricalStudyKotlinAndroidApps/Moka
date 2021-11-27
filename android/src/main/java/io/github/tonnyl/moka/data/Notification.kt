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
import io.tonnyl.moka.common.data.NotificationReasons
import kotlinx.datetime.Instant
import io.tonnyl.moka.common.data.Notification as SerializableNotification
import io.tonnyl.moka.common.data.NotificationRepository as SerializableNotificationRepository
import io.tonnyl.moka.common.data.NotificationRepositoryOwner as SerializableNotificationRepositoryOwner
import io.tonnyl.moka.common.data.NotificationRepositorySubject as SerializableNotificationRepositorySubject

@Entity(tableName = "notification")
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

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant,

    @ColumnInfo(name = "last_read_at")
    var lastReadAt: Instant? = null,

    @ColumnInfo(name = "url")
    var url: String,

    // local only
    // indicates if the notification has been displayed to user.
    @ColumnInfo(name = "has_displayed")
    @Transient
    var hasDisplayed: Boolean = false

)

data class NotificationRepository(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "full_name")
    var fullName: String,

    @Embedded(prefix = "owner_")
    var owner: NotificationRepositoryOwner,

    // note the difference of serialized name, column name and field name
    @ColumnInfo(name = "is_private")
    var isPrivate: Boolean,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "fork")
    var fork: Boolean,

    @ColumnInfo(name = "url")
    var url: String

)

data class NotificationRepositoryOwner(

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "node_id")
    var nodeId: String,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @ColumnInfo(name = "gravatar_id")
    var gravatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "followers_url")
    var followersUrl: String,

    @ColumnInfo(name = "following_url")
    var followingUrl: String,

    @ColumnInfo(name = "gists_url")
    var gistsUrl: String,

    @ColumnInfo(name = "starred_url")
    var starredUrl: String,

    @ColumnInfo(name = "subscriptions_url")
    var subscriptionsUrl: String,

    @ColumnInfo(name = "organizations_url")
    var organizationsUrl: String,

    @ColumnInfo(name = "repos_url")
    var reposUrl: String,

    @ColumnInfo(name = "events_url")
    var eventsUrl: String,

    @ColumnInfo(name = "received_events_url")
    var receivedEventsUrl: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "site_admin")
    var siteAdmin: Boolean

)

data class NotificationRepositorySubject(

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "latest_comment_url")
    var latestCommentUrl: String? = null,

    @ColumnInfo(name = "type")
    var type: String

)

val SerializableNotification.dbModel: Notification
    get() = Notification(
        id = id,
        repository = repository.dbModel,
        subject = subject.toDbModel,
        reason = reason,
        unread = unread, updatedAt = updatedAt,
        lastReadAt = lastReadAt,
        url = url,
        hasDisplayed = false
    )

val SerializableNotificationRepository.dbModel: NotificationRepository
    get() = NotificationRepository(
        id = id,
        nodeId = nodeId,
        name = name,
        fullName = fullName,
        owner = owner.toDbModel,
        isPrivate = isPrivate,
        htmlUrl = htmlUrl,
        description = description,
        fork = fork,
        url = url
    )

val SerializableNotificationRepositoryOwner.toDbModel: NotificationRepositoryOwner
    get() = NotificationRepositoryOwner(
        login = login,
        id = id,
        nodeId = nodeId,
        avatarUrl = avatarUrl,
        gravatarId = gravatarId,
        url = url,
        htmlUrl = htmlUrl,
        followersUrl = followersUrl,
        followingUrl = followingUrl,
        gistsUrl = gistsUrl,
        starredUrl = starredUrl,
        subscriptionsUrl = subscriptionsUrl,
        organizationsUrl = organizationsUrl,
        reposUrl = reposUrl,
        eventsUrl = eventsUrl,
        receivedEventsUrl = receivedEventsUrl,
        type = type,
        siteAdmin = siteAdmin
    )

val SerializableNotificationRepositorySubject.toDbModel: NotificationRepositorySubject
    get() = NotificationRepositorySubject(
        title = title,
        url = url,
        latestCommentUrl = latestCommentUrl,
        type = type
    )

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