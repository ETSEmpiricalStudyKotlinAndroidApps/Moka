package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRepository(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @SerialName("full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String? = null,

    @ColumnInfo(name = "url")
    var url: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String? = null

)