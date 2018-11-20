package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.IssueQuery
import kotlinx.android.parcel.Parcelize

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
@Parcelize
data class Actor(
        /**
         * A URL pointing to the actor's public avatar.
         *
         * Argument: size
         * Type: Int
         * Description: The size of the resulting square image.
         */
        val avatarUrl: Uri,
        /**
         * The username of the actor.
         */
        val login: String,
        /**
         * The HTTP path for this actor.
         */
        val resourcePath: Uri,
        /**
         * The HTTP URL for this actor.
         */
        val url: Uri
) : Parcelable {

    companion object {

        fun createFromIssueAuthor(data: IssueQuery.Author?): Actor? = if (data == null) null else Actor(
                data.avatarUrl(),
                data.login(),
                data.resourcePath(),
                data.url()
        )

        fun createFromIssueEditor(data: IssueQuery.Editor?): Actor? = if (data == null) null else Actor(
                data.avatarUrl(),
                data.login(),
                data.resourcePath(),
                data.url()
        )

        fun createFromMilestoneCreator(data: IssueQuery.Creator?): Actor? = if (data == null) null else Actor(
                data.avatarUrl(),
                data.login(),
                data.resourcePath(),
                data.url()
        )

    }

}