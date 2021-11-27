package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import io.tonnyl.moka.common.data.EventActor as SerializableEventActor

data class EventActor(

    @ColumnInfo(name = "id")
    var id: Long,

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String? = null,

    @ColumnInfo(name = "type")
    var type: String? = null

)

val SerializableEventActor.dbModel: EventActor
    get() = EventActor(
        id = id,
        login = login,
        avatarUrl = avatarUrl,
        htmlUrl = htmlUrl,
        type = type
    )