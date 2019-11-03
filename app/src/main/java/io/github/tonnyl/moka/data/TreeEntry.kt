package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.TreeEntry as RawTreeEntry

@Parcelize
data class TreeEntry(

    /**
     * Entry file mode.
     */
    val mode: Int,

    /**
     * Entry file name.
     */
    val name: String,

    /**
     * Entry file object.
     */
    val `object`: GitObject?,

    /**
     * Entry file Git object ID.
     */
    val oid: String,

    /**
     * Entry file type.
     */
    val type: String

) : Parcelable

fun RawTreeEntry.toNonNullTreeEntry(): TreeEntry {
    return TreeEntry(mode, name, object_?.fragments?.gitObject?.toNonNullGitObject(), oid, type)
}