package io.github.tonnyl.moka.ui.profile.status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EditStatusArgs(

    val emoji: String? = null,

    val message: String? = null,

    val indicatesLimitedAvailability: Boolean? = null

) : Parcelable