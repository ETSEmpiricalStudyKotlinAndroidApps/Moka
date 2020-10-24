package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventActor(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "login")
    var login: String,

    @Json(name = "avatar_url")
    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String?,

    @ColumnInfo(name = "type")
    var type: String?

)