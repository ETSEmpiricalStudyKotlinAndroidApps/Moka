package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@JsonClass(generateAdapter = true)
data class Gist(

    @ColumnInfo(name = "id")
    var id: String,

    @Json(name = "html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @ColumnInfo(name = "files")
    var files: Map<String, EventGistFile>,

    // note the difference of serialized name, column name and field name
    @Json(name = "public")
    @ColumnInfo(name = "is_public")
    var isPublic: Boolean,

    @Json(name = "created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @Json(name = "updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "comments")
    var comments: Int,

    @Embedded(prefix = "owner_")
    var owner: EventActor,

    @ColumnInfo(name = "truncated")
    var truncated: Boolean

) : Parcelable