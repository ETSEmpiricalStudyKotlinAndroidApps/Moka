package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.GitObject as RawGitObject

/**
 * Represents a Git object.
 */
data class GitObject(

    /**
     * An abbreviated version of the Git object ID.
     */
    val abbreviatedOid: String,

    /**
     * The HTTP path for this Git object.
     */
    val commitResourcePath: String,

    /**
     * The HTTP URL for this Git object.
     */
    val commitUrl: String,

    val id: String,

    /**
     * The Git object ID.
     */
    val oid: String

)

fun RawGitObject.toNonNullGitObject(): GitObject {
    return GitObject(abbreviatedOid, commitResourcePath, commitUrl, id, oid)
}