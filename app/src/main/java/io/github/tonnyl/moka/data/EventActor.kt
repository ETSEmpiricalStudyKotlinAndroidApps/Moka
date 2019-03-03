package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class EventActor(
        @SerializedName("id")
        val id: Long,
        @SerializedName("login")
        val login: String,
        @SerializedName("avatar_url")
        val avatarUrl: String,
        @SerializedName("html_url")
        val htmlUrl: String,
        @SerializedName("type")
        val type: String
) : Parcelable