package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

/**
 * The Code of Conduct for a repository.
 */
@Parcelize
data class CodeOfConduct(
        /**
         * The body of the CoC.
         */
        val body: String?,
        /**
         * The key for the CoC.
         */
        val key: String,
        /**
         * The formal name of the CoC.
         */
        val name: String,
        /**
         * The path to the CoC.
         */
        val url: Uri?
) : Parcelable {

    companion object {

        fun createFromRaw(data: RepositoryQuery.CodeOfConduct?): CodeOfConduct? = if (data == null) null else CodeOfConduct(
                data.body(),
                data.key(),
                data.name(),
                data.url()
        )

        fun createFromRaw(data: RepositoryFragment.CodeOfConduct?): CodeOfConduct? = if (data == null) null else CodeOfConduct(
                data.body(),
                data.key(),
                data.name(),
                data.url()
        )

    }

}