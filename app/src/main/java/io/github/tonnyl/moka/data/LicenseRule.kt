package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.fragment.LicenseRule as RawLicenseRule

/**
 * Describes a License's conditions, permissions, and limitations.
 */
@Parcelize
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

) : Parcelable

fun RawLicenseRule.toNonNullLicenseRule(): LicenseRule {
    return LicenseRule(description(), key(), label())
}