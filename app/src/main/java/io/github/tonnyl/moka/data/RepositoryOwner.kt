package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.RepositoryOwner as RawRepositoryOwner

/**
 * Represents an owner of a Repository.
 */
@Parcelize
data class RepositoryOwner(

    /**
     * A URL pointing to the owner's public avatar.
     *
     * Argument: size
     * Type: Int
     * Description: The size of the resulting square image.
     */
    val avatarUrl: Uri,

    val id: String,

    /**
     * The username used to login.
     */
    val login: String,

    /**
     * The HTTP URL for the owner.
     */
    val resourcePath: Uri,

    /**
     * The HTTP URL for the owner.
     */
    val url: Uri

) : Parcelable

fun RawRepositoryOwner?.toRepositoryOwner(): RepositoryOwner? {
    this ?: return null

    return toNonNullRepositoryOwner()
}

fun RawRepositoryOwner.toNonNullRepositoryOwner(): RepositoryOwner {
    return RepositoryOwner(
        avatarUrl(),
        id(),
        login(),
        resourcePath(),
        url()
    )
}