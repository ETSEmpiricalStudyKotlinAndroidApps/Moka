package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "trending_developer")
@Parcelize
@JsonClass(generateAdapter = true)
data class TrendingDeveloper(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Transient // local field
    var id: Int = 0,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "name")
    var name: String?,

    /**
     * could be organization or user.
     */
    @ColumnInfo(name = "type")
    var type: String?,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "avatar")
    var avatar: String,

    @Json(name = "repo")
    @Embedded(prefix = "trending_developer_repository_")
    var repository: TrendingDeveloperRepository

) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class TrendingDeveloperRepository(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "url")
    var url: String

) : Parcelable