package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class EventRepo(
        val id: String,
        val name: String,
        val url: String
) : Parcelable