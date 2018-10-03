package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class EventActor(
        val id: Int,
        val login: String,
        @SerializedName("gravatar_id")
        val grAvatarId: String,
        @SerializedName("avatar_url")
        val avatarUrl: String,
        val url: String
) : Parcelable