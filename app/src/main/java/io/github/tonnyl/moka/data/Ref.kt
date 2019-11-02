package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.Ref as RawRef

/**
 * Represents a Git reference.
 */
@Parcelize
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
    val target: GitObject

) : Parcelable

fun RawRef.toNonNullRef(): Ref {
    return Ref(
        id,
        name,
        prefix,
        target.fragments.gitObject.toNonNullGitObject()
    )
}