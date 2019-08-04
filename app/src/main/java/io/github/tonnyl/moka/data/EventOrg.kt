package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventOrg(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("login")
    @ColumnInfo(name = "login")
    var login: String,

    @SerializedName("gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var grAvatarId: String,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String

) : Parcelable