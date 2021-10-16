package io.github.tonnyl.moka.data

import io.tonnyl.moka.graphql.fragment.License as RawLicense

/**
 * A repository's open source license.
 */
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
    val url: String?

)

fun RawLicense.toNonNullLicense(): License {
    return License(
        body,
        conditions.mapNotNull {
            it?.licenseRule?.toNonNullLicenseRule()
        },
        description,
        featured,
        hidden,
        id,
        implementation,
        key,
        limitations.mapNotNull {
            it?.licenseRule?.toNonNullLicenseRule()
        },
        name,
        nickname,
        permissions.mapNotNull {
            it?.licenseRule?.toNonNullLicenseRule()
        },
        pseudoLicense,
        spdxId,
        url
    )
}