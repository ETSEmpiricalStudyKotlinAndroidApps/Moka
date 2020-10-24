package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.LicenseRule as RawLicenseRule

/**
 * Describes a License's conditions, permissions, and limitations.
 */
data class LicenseRule(

    /**
     * A description of the rule
     */
    val description: String,

    /**
     * The machine-readable rule key
     */
    val key: String,

    /**
     * The human-readable rule label
     */
    val label: String

)

fun RawLicenseRule.toNonNullLicenseRule(): LicenseRule {
    return LicenseRule(description, key, label)
}