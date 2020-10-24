package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventOrg(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "login")
    var login: String,

    @Json(name = "gravatar_id")
    @ColumnInfo(name = "gravatar_id")
    var grAvatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @Json(name = "avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String

)