package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

/**
 * Represents a Git object.
 */
@Parcelize
data class GitObject(
        /**
         * An abbreviated version of the Git object ID.
         */
        val abbreviatedOid: String,
        /**
         * The HTTP path for this Git object.
         */
        val commitResourcePath: Uri,
        /**
         * The HTTP URL for this Git object.
         */
        val commitUrl: Uri,
        val id: String,
        /**
         * The Git object ID.
         */
        val oid: String
) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryQuery.Target): GitObject = GitObject(
                data.abbreviatedOid(),
                data.commitResourcePath(),
                data.commitUrl(),
                data.id(),
                data.oid()
        )

        fun createFromRaw(data: PullRequestQuery.Target?): GitObject? = if (data == null) null else GitObject(
                data.abbreviatedOid(),
                data.commitResourcePath(),
                data.commitUrl(),
                data.id(),
                data.oid()
        )

        fun createFromRaw(data: PullRequestQuery.Target1?): GitObject? = if (data == null) null else GitObject(
                data.abbreviatedOid(),
                data.commitResourcePath(),
                data.commitUrl(),
                data.id(),
                data.oid()
        )

        fun createFromRaw(data: RepositoryFragment.Target?): GitObject? = if (data == null) null else GitObject(
                data.abbreviatedOid(),
                data.commitResourcePath(),
                data.commitUrl(),
                data.id(),
                data.oid()
        )

        fun createFromRaw(data: RepositoryFragment.Object?): GitObject? = if (data == null) null else GitObject(
                data.abbreviatedOid(),
                data.commitResourcePath(),
                data.commitUrl(),
                data.id(),
                data.oid()
        )

    }

}