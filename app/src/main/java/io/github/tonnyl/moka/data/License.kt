package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.fragment.RepositoryFragment
import kotlinx.android.parcel.Parcelize

/**
 * A repository's open source license.
 */
@Parcelize
data class License(
        /**
         * The full text of the license
         */
        val body: String,
        /**
         * The conditions set by the license.
         */
        val conditions: List<LicenseRule>,
        /**
         * A human-readable description of the license.
         */
        val description: String?,
        /**
         * Whether the license should be featured.
         */
        val featured: Boolean,
        /**
         * Whether the license should be displayed in license pickers.
         */
        val hidden: Boolean,
        val id: String,
        /**
         * Instructions on how to implement the license.
         */
        val implementation: String?,
        /**
         * The lowercased SPDX ID of the license.
         */
        val key: String,
        /**
         * The limitations set by the license.
         */
        val limitations: List<LicenseRule>,
        /**
         * The license full name specified by https://spdx.org/licenses .
         */
        val name: String,
        /**
         * Customary short name if applicable (e.g, GPLv3).
         */
        val nickname: String?,
        /**
         * The permissions set by the license.
         */
        val permissions: List<LicenseRule>,
        /**
         * Whether the license is a pseudo-license placeholder (e.g., other, no-license).
         */
        val pseudoLicense: Boolean,
        /**
         * Short identifier specified by https://spdx.org/licenses .
         */
        val spdxId: String?,
        /**
         * URL to the license on https://choosealicense.com .
         */
        val url: Uri?
) : Parcelable {

    companion object {

        fun createFromRaw(license: RepositoryQuery.LicenseInfo?): License? = if (license == null) null else License(
                license.body(),
                license.conditions().map { LicenseRule.createFromRawCondition(it) },
                license.description(),
                license.featured(),
                license.hidden(),
                license.id(),
                license.implementation(),
                license.key(),
                license.limitations().map { LicenseRule.createFromRawLimitation(it) },
                license.name(),
                license.nickname(),
                license.permissions().map { LicenseRule.createFromRawPermission(it) },
                license.pseudoLicense(),
                license.spdxId(),
                license.url()
        )

        // todo fix query error of Search.graphql
        fun createFromRaw(data: RepositoryFragment.LicenseInfo?): License? = if (data == null) null else License(
                "",
                emptyList(),
                data.description(),
                data.featured(),
                data.hidden(),
                data.id(),
                data.implementation(),
                "",
                emptyList(),
                data.name(),
                data.nickname(),
                emptyList(),
                data.pseudoLicense(),
                data.spdxId(),
                data.url()
        )

    }

}