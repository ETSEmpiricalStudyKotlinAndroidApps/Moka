package io.github.tonnyl.moka.data.extension

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.res.ResourcesCompat
import io.github.tonnyl.moka.R
import io.tonnyl.moka.common.data.AccessToken
import io.tonnyl.moka.common.data.AuthenticatedUser
import io.tonnyl.moka.common.data.NotificationReasons
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.db.data.Notification
import io.tonnyl.moka.common.db.data.NotificationRepositoryOwner
import io.tonnyl.moka.common.store.data.Account
import io.tonnyl.moka.common.store.data.ExploreTimeSpan
import kotlinx.serialization.ExperimentalSerializationApi
import io.tonnyl.moka.common.store.data.AccessToken as PBAccessToken

@ExperimentalSerializationApi
fun AuthenticatedUser.toPbAccount(): Account {
    val user = this
    return Account(
        login = user.login,
        id = user.id,
        nodeId = user.nodeId,
        avatarUrl = user.avatarUrl,
        htmlUrl = user.htmlUrl,
        type = user.type,
        siteAdmin = user.siteAdmin,
        name = user.name,
        company = user.company,
        blog = user.blog,
        location = user.location,
        email = user.email,
        hireable = user.hireable,
        bio = user.bio,
        publicRepos = user.publicRepos,
        publicGists = user.publicGists,
        followers = user.followers,
        following = user.following,
        createdAt = user.createdAt.toString(),
        updatedAt = user.updatedAt.toString(),
        privateGists = user.privateGists,
        totalPrivateRepos = user.totalPrivateRepos,
        ownedPrivateRepos = user.ownedPrivateRepos,
        diskUsage = user.diskUsage,
        collaborators = user.collaborators,
        twoFactorAuthentication = user.twoFactorAuthentication
    )
}

@ExperimentalSerializationApi
fun AccessToken.toPBAccessToken(): PBAccessToken {
    val token = this
    return PBAccessToken(
        accessToken = token.accessToken,
        scope = token.scope,
        tokenType = token.tokenType
    )
}

@ExperimentalSerializationApi
val ExploreTimeSpan.displayStringResId: Int
    get() = when (this) {
        ExploreTimeSpan.DAILY -> {
            R.string.explore_trending_filter_time_span_daily
        }
        ExploreTimeSpan.WEEKLY -> {
            R.string.explore_trending_filter_time_span_weekly
        }
        ExploreTimeSpan.MONTHLY -> {
            R.string.explore_trending_filter_time_span_monthly
        }
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