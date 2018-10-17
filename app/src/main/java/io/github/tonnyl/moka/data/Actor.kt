package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
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
        val avatarUrl: String,
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
) : Parcelable