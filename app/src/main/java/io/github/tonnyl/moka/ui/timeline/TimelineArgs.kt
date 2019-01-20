package io.github.tonnyl.moka.ui.timeline

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class TimelineArgs(
        val username: String
) : Parcelable