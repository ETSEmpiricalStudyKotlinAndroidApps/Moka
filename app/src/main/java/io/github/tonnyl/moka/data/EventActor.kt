package io.github.tonnyl.moka.data

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@SuppressLint("ParcelCreator")
data class EventActor(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Long,

    @SerializedName("login")
    @ColumnInfo(name = "login")
    var login: String,

    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String?,

    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: String?

) : Parcelable