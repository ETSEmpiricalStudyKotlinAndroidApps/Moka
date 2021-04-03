package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventActor(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "login")
    var login: String,

    @SerialName("avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String? = null,

    @ColumnInfo(name = "type")
    var type: String? = null

)