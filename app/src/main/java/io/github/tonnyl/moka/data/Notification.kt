package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
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
    var url: String

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
    var description: String,

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