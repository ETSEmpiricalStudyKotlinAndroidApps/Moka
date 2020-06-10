package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class AccessToken(

    @Json(name = "access_token")
    val accessToken: String,

    val scope: String,

    @Json(name = "token_type")
    val tokenType: String

) : Parcelable