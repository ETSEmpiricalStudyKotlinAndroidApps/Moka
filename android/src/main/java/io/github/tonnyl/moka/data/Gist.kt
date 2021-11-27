package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import kotlinx.datetime.Instant
import io.tonnyl.moka.common.data.Gist as SerializableGist

data class Gist(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "files")
    var files: Map<String, EventGistFile>,

    // note the difference of serialized name, column name and field name
    @ColumnInfo(name = "is_public")
    var isPublic: Boolean,

    @ColumnInfo(name = "created_at")
    var createdAt: Instant,

    @ColumnInfo(name = "updated_at")
    var updatedAt: Instant,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "comments")
    var comments: Int,

    @Embedded(prefix = "owner_")
    var owner: EventActor,

    @ColumnInfo(name = "truncated")
    var truncated: Boolean

)

val SerializableGist.toDbModel: Gist
    get() = Gist(
        id = id,
        htmlUrl = htmlUrl,
        files = files.mapValues { it.value.dbModel },
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
        description = description,
        comments = comments,
        owner = owner.dbModel,
        truncated = truncated
    )