package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import io.github.tonnyl.moka.fragment.UserStatus as RawUserStatus

/**
 * The user's description of what they're currently doing.
 */
@Parcelize
data class UserStatus(

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Date,

    /**
     * An emoji summarizing the user's status.
     */
    val emoji: String?,

    /**
     * ID of the object.
     */
    val id: String,

    /**
     * Whether this status indicates the user is not fully available on GitHub.
     */
    val indicatesLimitedAvailability: Boolean,

    /**
     * A brief message describing what the user is doing.
     */
    val message: String?,

    /**
     * Identifies the date and time when the object was last updated.
     */
    val updatedAt: Date

) : Parcelable

fun RawUserStatus.toNonNullUserStatus(): UserStatus {
    return UserStatus(
        createdAt(),
        emoji(),
        id(),
        indicatesLimitedAvailability(),
        message(),
        updatedAt()
    )
}