package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Notification(
        @SerializedName("id")
        var id: String,
        @SerializedName("repository")
        val repository: NotificationRepository,
        @SerializedName("subject")
        val subject: NotificationRepositorySubject,
        @SerializedName("reason")
        val reason: String,
        @SerializedName("unread")
        val unread: Boolean,
        @SerializedName("updated_at")
        val updatedAt: Date,
        @SerializedName("last_read_at")
        val lastReadAt: Date,
        @SerializedName("url")
        val url: String
) : Parcelable

@Parcelize
data class NotificationRepository(
        @SerializedName("id")
        val id: Long,
        @SerializedName("node_id")
        val nodeId: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("full_name")
        val fullName: String,
        @SerializedName("owner")
        val owner: NotificationRepositoryOwner,
        @SerializedName("private")
        val `private`: Boolean,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("description")
        val description: String,
        @SerializedName("fork")
        val fork: Boolean,
        @SerializedName("url")
        val url: String
) : Parcelable

@Parcelize
data class NotificationRepositoryOwner(
        @SerializedName("login")
        val login: String,
        @SerializedName("id")
        val id: Long,
        @SerializedName("node_id")
        val nodeId: String,
        @SerializedName("avatar_url")
        val avatarUrl: String,
        @SerializedName("gravatar_id")
        val gravatarId: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("followers_url")
        val followersUrl: String,
        @SerializedName("following_url")
        val followingUrl: String,
        @SerializedName("gists_url")
        val gistsUrl: String,
        @SerializedName("starred_url")
        val starredUrl: String,
        @SerializedName("subscriptions_url")
        val subscriptionsUrl: String,
        @SerializedName("organizations_url")
        val organizationsUrl: String,
        @SerializedName("repos_url")
        val reposUrl: String,
        @SerializedName("events_url")
        val eventsUrl: String,
        @SerializedName("received_events_url")
        val receivedEventsUrl: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("site_admin")
        val siteAdmin: Boolean
) : Parcelable

@Parcelize
data class NotificationRepositorySubject(
        @SerializedName("title")
        val title: String,
        @SerializedName("url")
        val url: String,
        @SerializedName("latest_comment_url")
        val latestCommentUrl: String,
        @SerializedName("type")
        val type: String
) : Parcelable

/**
 * When retrieving responses from the Notifications API, each payload has a key titled reason.
 * These correspond to events that trigger a notification.
 */
enum class NotificationReasons(val value: String) {

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