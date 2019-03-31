package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

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
        val target: GitObject?
) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryQuery.DefaultBranchRef?): Ref? = if (data == null) null else Ref(
                data.id(),
                data.name(),
                data.prefix(),
                GitObject.createFromRaw(data.target())
        )

        fun createFromRepositoryQueryBaseRef(data: PullRequestQuery.BaseRef?): Ref? = if (data == null) null else Ref(
                data.id(),
                data.name(),
                data.prefix(),
                GitObject.createFromRaw(data.target())
        )

        fun createFromRepositoryQueryHeadRef(data: PullRequestQuery.HeadRef?): Ref? = if (data == null) null else Ref(
                data.id(),
                data.name(),
                data.prefix(),
                GitObject.createFromRaw(data.target())
        )

        fun createFromRaw(data: RepositoryFragment.DefaultBranchRef?): Ref? = if (data == null) null else Ref(
                data.id(),
                data.name(),
                data.prefix(),
                GitObject.createFromRaw(data.target())
        )

    }

}