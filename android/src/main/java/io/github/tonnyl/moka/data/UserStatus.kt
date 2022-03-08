package io.github.tonnyl.moka.data

import android.os.Parcelable
import io.github.tonnyl.moka.parcelization.InstantParceler
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

/**
 * The user's description of what they're currently doing.
 */
@Parcelize
@TypeParceler<Instant, InstantParceler>
@TypeParceler<Instant?, InstantParceler>
data class UserStatus(

    /**
     * Identifies the date and time when the object was created.
     */
    val createdAt: Instant,

    /**
     * An emoji summarizing the user's status.
     */
    val emoji: String?,

    /**
     * If set, the status will not be shown after this date.
     */
    val expiresAt: Instant?,

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
    val updatedAt: Instant

) : Parcelable