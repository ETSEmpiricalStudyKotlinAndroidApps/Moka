package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import io.tonnyl.moka.common.data.EventRepository as SerializableEventRepository

data class EventRepository(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "full_name")
    var fullName: String? = null,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String? = null

)

val SerializableEventRepository.dbModel: EventRepository
    get() = EventRepository(
        id = id,
        name = name,
        fullName = fullName,
        url = url,
        htmlUrl = htmlUrl
    )