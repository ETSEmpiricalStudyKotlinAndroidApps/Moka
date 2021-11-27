package io.tonnyl.moka.common.data

import io.tonnyl.moka.graphql.fragment.TreeEntry
import kotlin.jvm.JvmInline

@JvmInline
value class TreeEntryType private constructor(val value: String) {

    companion object {

        val TREE = TreeEntryType("tree")

        val BLOB = TreeEntryType("blob")

        val COMMIT = TreeEntryType("commit")

        val UNKNOWN = TreeEntryType("unknown")

    }

}

val TreeEntry.treeEntryType: TreeEntryType
    get() = when (type) {
        "tree" -> {
            TreeEntryType.TREE
        }
        "commit" -> {
            TreeEntryType.COMMIT
        }
        "blob" -> {
            TreeEntryType.BLOB
        }
        else -> {
            TreeEntryType.UNKNOWN
        }
    }