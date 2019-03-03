package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventRepository(
        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("full_name")
        val fullName: String?,

        @SerializedName("url")
        val url: String,

        @SerializedName("html_url")
        val htmlUrl: String?
) : Parcelable