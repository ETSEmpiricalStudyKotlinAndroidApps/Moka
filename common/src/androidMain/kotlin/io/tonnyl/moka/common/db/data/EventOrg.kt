package io.tonnyl.moka.common.db.data

import androidx.room.ColumnInfo
import io.tonnyl.moka.common.data.EventOrg as SerializableEventOrg

data class EventOrg(

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "gravatar_id")
    var grAvatarId: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String

)

val SerializableEventOrg.dbModel: EventOrg
    get() = EventOrg(
        id = id,
        login = login,
        grAvatarId = grAvatarId,
        url = url,
        avatarUrl = avatarUrl
    )