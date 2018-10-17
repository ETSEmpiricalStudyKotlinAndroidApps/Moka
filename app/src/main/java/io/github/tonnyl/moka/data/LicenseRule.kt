package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.RepositoryQuery
import kotlinx.android.parcel.Parcelize

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
) : Parcelable {

    companion object {

        fun createFromRawCondition(condition: RepositoryQuery.Condition): LicenseRule = LicenseRule(
                condition.description(),
                condition.key(),
                condition.label()
        )

        fun createFromRawLimitation(condition: RepositoryQuery.Limitation): LicenseRule = LicenseRule(
                condition.description(),
                condition.key(),
                condition.label()
        )

        fun createFromRawPermission(condition: RepositoryQuery.Permission): LicenseRule = LicenseRule(
                condition.description(),
                condition.key(),
                condition.label()
        )

    }

}