package io.github.tonnyl.moka.ui.explore.filters

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LocalLanguage(

    val urlParam: String?,

    val name: String,

    val color: String

) : Parcelable