package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.ParentOfForkedRepository as RawParentOfForkedRepository

data class ParentOrMirrorRepository(

    /**
     * The HTTP URL for this repository
     */
    val url: String,

    /**
     * The name of the repository.
     */
    val name: String,

    /**
     * The repository's name with owner.
     */
    val nameWithOwner: String

)

fun RawParentOfForkedRepository.toNonNullParentOfForkedRepository(): ParentOrMirrorRepository {
    return ParentOrMirrorRepository(url, name, nameWithOwner)
}