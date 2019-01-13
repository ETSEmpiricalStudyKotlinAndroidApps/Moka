package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrendingDeveloper(
        val username: String,
        val name: String?,
        val url: String,
        val avatar: String,
        @SerializedName("repo")
        val repository: TrendingDeveloperRepository
) : Parcelable

@Parcelize
data class TrendingDeveloperRepository(
        val name: String,
        val description: String?,
        val url: String
) : Parcelable