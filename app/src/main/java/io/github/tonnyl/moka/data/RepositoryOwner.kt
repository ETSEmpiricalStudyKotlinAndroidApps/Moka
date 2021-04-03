package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.RepositoryOwner as RawRepositoryOwner

/**
 * Represents an owner of a Repository.
 */
data class RepositoryOwner(

    /**
     * A URL pointing to the owner's public avatar.
     *
     * Argument: size
     * Type: Int
     * Description: The size of the resulting square image.
     */
    val avatarUrl: String,

    val id: String,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The HTTP URL for the owner.
     */
    val resourcePath: String,

    /**
     * The HTTP URL for the owner.
     */
    val url: String

)

fun RawRepositoryOwner.toNonNullRepositoryOwner(): RepositoryOwner {
    return RepositoryOwner(avatarUrl, id, login, resourcePath, url)
}