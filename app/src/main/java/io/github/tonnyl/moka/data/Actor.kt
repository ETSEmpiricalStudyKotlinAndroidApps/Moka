package io.github.tonnyl.moka.data

import kotlinx.serialization.Serializable
import io.github.tonnyl.moka.fragment.Actor as RawActor

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
@Serializable
data class Actor(

    /**
     * A URL pointing to the actor's public avatar.
     */
    val avatarUrl: String,

    /**
     * The username of the actor.
     */
    val login: String,

    /**
     * The HTTP URL for this actor.
     */
    val url: String

)

fun RawActor.toNonNullActor(): Actor {
    return Actor(avatarUrl, login, url)
}