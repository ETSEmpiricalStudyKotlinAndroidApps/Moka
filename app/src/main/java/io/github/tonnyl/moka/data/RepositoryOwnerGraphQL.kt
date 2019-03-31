package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

/**
 * Represents an owner of a Repository.
 */
@Parcelize
data class RepositoryOwnerGraphQL(
        /**
         * A URL pointing to the owner's public avatar.
         *
         * Argument: size
         * Type: Int
         * Description: The size of the resulting square image.
         */
        val avatarUrl: Uri,

        val id: String,

        /**
         * The username used to login.
         */
        val login: String,

        /**
         * The HTTP URL for the owner.
         */
        val resourcePath: Uri,

        /**
         * The HTTP URL for the owner.
         */
        val url: Uri

) : Parcelable {

    companion object {

        fun createFromRaw(owner: RepositoryQuery.Owner): RepositoryOwnerGraphQL = RepositoryOwnerGraphQL(
                owner.avatarUrl(),
                owner.id(),
                owner.login(),
                owner.resourcePath(),
                owner.url()
        )

        fun createFromHeadRepositoryOwner(data: PullRequestQuery.HeadRepositoryOwner?): RepositoryOwnerGraphQL? = if (data == null) null else RepositoryOwnerGraphQL(
                data.avatarUrl(),
                data.id(),
                data.login(),
                data.resourcePath(),
                data.url()
        )

        fun createFromRaw(data: RepositoryFragment.Owner): RepositoryOwnerGraphQL = RepositoryOwnerGraphQL(
                data.avatarUrl(),
                data.id(),
                data.login(),
                data.resourcePath(),
                data.url()
        )

    }

}