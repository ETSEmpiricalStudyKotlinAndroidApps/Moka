package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Gist(

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: String,

    @SerializedName("html_url")
    @ColumnInfo(name = "html_url")
    var htmlUrl: String,

    @SerializedName("files")
    @ColumnInfo(name = "files")
    var files: Map<String, EventGistFile>,

    // note the difference of serialized name, column name and field name
    @SerializedName("public")
    @ColumnInfo(name = "is_public")
    var isPublic: Boolean,

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    var createdAt: Date,

    @SerializedName("updated_at")
    @ColumnInfo(name = "updated_at")
    var updatedAt: Date,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String,

    @SerializedName("comments")
    @ColumnInfo(name = "comments")
    var comments: Int,

    @SerializedName("owner")
    @Embedded(prefix = "owner_")
    var owner: EventActor,

    @SerializedName("truncated")
    @ColumnInfo(name = "truncated")
    var truncated: Boolean

) : Parcelable