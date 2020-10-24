package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventRepository(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @Json(name = "full_name")
    @ColumnInfo(name = "full_name")
    var fullName: String?,

    @ColumnInfo(name = "url")
    var url: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String?

)