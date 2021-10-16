package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.TreeEntry as RawTreeEntry

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

)

fun RawTreeEntry.toNonNullTreeEntry(): TreeEntry {
    return TreeEntry(mode, name, object_?.gitObject?.toNonNullGitObject(), oid, type)
}