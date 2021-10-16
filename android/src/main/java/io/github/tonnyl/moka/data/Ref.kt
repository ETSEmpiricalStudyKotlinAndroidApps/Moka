package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.Ref as RawRef

/**
 * Represents a Git reference.
 */
data class Ref(

    val id: String,

    /**
     * The ref name.
     */
    val name: String,

    /**
     * The ref's prefix, such as refs/heads/ or refs/tags/.
     */
    val prefix: String,

    /**
     * The object the ref points to.
     */
    val target: GitObject?

)

fun RawRef.toNonNullRef(): Ref {
    return Ref(
        id,
        name,
        prefix,
        target?.gitObject?.toNonNullGitObject()
    )
}