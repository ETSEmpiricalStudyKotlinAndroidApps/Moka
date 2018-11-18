package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccessToken(
        val accessToken: String,
        val scope: String,
        val tokenType: String
) : Parcelable