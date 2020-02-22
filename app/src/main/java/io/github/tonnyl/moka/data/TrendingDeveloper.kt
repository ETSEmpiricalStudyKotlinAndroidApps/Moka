package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "trending_developer")
@Parcelize
data class TrendingDeveloper(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @SerializedName("username")
    @ColumnInfo(name = "username")
    var username: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String?,

    /**
     * could be organization or user.
     */
    @SerializedName("type")
    @ColumnInfo(name = "type")
    var type: String?,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar")
    var avatar: String,

    @SerializedName("repo")
    @Embedded(prefix = "trending_developer_repository_")
    var repository: TrendingDeveloperRepository

) : Parcelable

@Parcelize
data class TrendingDeveloperRepository(

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String?,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String

) : Parcelable