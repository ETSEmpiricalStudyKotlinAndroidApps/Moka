package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an object which can take actions on GitHub. Typically a User or Bot.
 */
@Serializable
data class Actor(

    /**
     * A URL pointing to the actor's public avatar.
     */
    @SerialName("avatar_url")
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