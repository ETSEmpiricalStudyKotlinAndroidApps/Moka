package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Gist(

    @ColumnInfo(name = "id")
    var id: String,

    @SerialName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "files")
    @Contextual
    var files: Map<String, EventGistFile>,

    // note the difference of serialized name, column name and field name
    @SerialName("public")
    @ColumnInfo(name = "is_public")
    var isPublic: Boolean,

    @SerialName("created_at")
    @ColumnInfo(name = "created_at")
    @Contextual
    var createdAt: Instant,

    @SerialName("updated_at")
    @ColumnInfo(name = "updated_at")
    @Contextual
    var updatedAt: Instant,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "comments")
    var comments: Int,

    @Embedded(prefix = "owner_")
    @Contextual
    var owner: EventActor,

    @ColumnInfo(name = "truncated")
    var truncated: Boolean

)