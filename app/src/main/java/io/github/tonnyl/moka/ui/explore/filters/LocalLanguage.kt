package io.github.tonnyl.moka.ui.explore.filters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocalLanguage(
        val urlParam: String,
        val name: String,
        val color: String
) : Parcelable