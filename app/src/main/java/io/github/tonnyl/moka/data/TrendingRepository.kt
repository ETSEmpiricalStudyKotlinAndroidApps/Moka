package io.github.tonnyl.moka.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "trending_repository")
@Parcelize
data class TrendingRepository(

    @PrimaryKey(autoGenerate = true)
    var id: Int,

    @SerializedName("author")
    @ColumnInfo(name = "author")
    var author: String,

    @SerializedName("name")
    @ColumnInfo(name = "name")
    var name: String,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar")
    var avatar: String,

    @SerializedName("url")
    @ColumnInfo(name = "url")
    var url: String,

    @SerializedName("description")
    @ColumnInfo(name = "description")
    var description: String?,

    @SerializedName("language")
    @ColumnInfo(name = "language")
    var language: String?,

    @SerializedName("languageColor")
    @ColumnInfo(name = "language_color")
    var languageColor: String?,

    @SerializedName("stars")
    @ColumnInfo(name = "stars")
    var stars: Int,

    @SerializedName("forks")
    @ColumnInfo(name = "forks")
    var forks: Int,

    @SerializedName("currentPeriodStars")
    @ColumnInfo(name = "current_period_stars")
    var currentPeriodStars: Int,

    @SerializedName("builtBy")
    var builtBy: List<TrendingRepositoryBuiltBy>

) : Parcelable

@Parcelize
data class TrendingRepositoryBuiltBy(

    @SerializedName("href")
    @ColumnInfo(name = "href")
    var href: String,

    @SerializedName("avatar")
    @ColumnInfo(name = "avatar")
    var avatar: String,

    @SerializedName("username")
    @ColumnInfo(name = "username")
    var username: String

) : Parcelable