package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventOrg(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "login")
    var login: String,

    @SerialName("gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var grAvatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @SerialName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String

)