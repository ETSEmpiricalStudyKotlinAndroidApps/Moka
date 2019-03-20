package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccessToken(
        @SerializedName("access_token")
        val accessToken: String,

        @SerializedName("scope")
        val scope: String,

        @SerializedName("token_type")
        val tokenType: String
) : Parcelable