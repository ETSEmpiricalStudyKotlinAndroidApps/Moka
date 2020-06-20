package io.github.tonnyl.moka.data

import android.net.Uri
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.Gist as RawGist

@Parcelize
data class Gist2(

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Date,

    /**
     * The gist description.
     */
    val description: String?,

    val id: String,

    /**
     * Identifies if the gist is a fork.
     */
    val isFork: Boolean,

    /**
     * Whether the gist is public or not.
     */
    val isPublic: Boolean,

    /**
     * The gist name.
     */
    val name: String,

    /**
     * The gist owner.
     */
    val owner: RepositoryOwner?,

    /**
     * Identifies when the gist was last pushed to.
     */
    val pushedAt: Date?,

    /**
     * The HTML path to this resource.
     */
    val resourcePath: Uri,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Date,

    /**
     * The HTTP URL for this Gist.
     */
    val url: Uri,

    /**
     * Returns a boolean indicating whether the viewing user has starred this starrable.
     */
    val viewerHasStarred: Boolean,

    /**
     * A list of comments associated with the gist.
     */
    val commentsTotalCount: Int,

    /**
     * A list of forks associated with the gist.
     */
    val forksTotalCount: Int,

    /**
     * A list of users who have starred this starrable.
     */
    val stargazersTotalCount: Int,

    /**
     * The gist file name.
     */
    val firstFileName: String?,

    /**
     * UTF8 text data or null if the file is binary
     */
    val firstFileText: String?

) : PinnableItem

fun RawGist.toGist(): Gist2 {
    return Gist2(
        createdAt,
        description,
        id,
        isFork,
        isPublic,
        name,
        owner?.fragments?.repositoryOwner?.toNonNullRepositoryOwner(),
        pushedAt,
        resourcePath,
        updatedAt,
        url,
        viewerHasStarred,
        comments.totalCount,
        forks.totalCount,
        stargazers.totalCount,
        files?.firstOrNull()?.name,
        files?.firstOrNull()?.text
    )
}