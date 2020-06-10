package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.Actor as RawActor

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Actor(

    /**
     * A URL pointing to the actor's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * The username of the actor.
     */
    val login: String,

    /**
     * The HTTP URL for this actor.
     */
    val url: Uri

) : Parcelable

fun RawActor.toNonNullActor(): Actor {
    return Actor(avatarUrl, login, url)
}