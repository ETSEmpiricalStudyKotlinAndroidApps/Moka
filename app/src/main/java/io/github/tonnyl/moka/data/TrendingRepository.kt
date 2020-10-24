package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "trending_repository")
@JsonClass(generateAdapter = true)
data class TrendingRepository(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Transient // local field.
    val id: Int = 0,

    @ColumnInfo(name = "author")
    var author: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "avatar")
    var avatar: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "description")
    var description: String?,

    @ColumnInfo(name = "language")
    var language: String?,

    @ColumnInfo(name = "language_color")
    var languageColor: String?,

    @ColumnInfo(name = "stars")
    var stars: Int,

    @ColumnInfo(name = "forks")
    var forks: Int,

    @ColumnInfo(name = "current_period_stars")
    var currentPeriodStars: Int,

    var builtBy: List<TrendingRepositoryBuiltBy>

)

@JsonClass(generateAdapter = true)
data class TrendingRepositoryBuiltBy(

    @ColumnInfo(name = "href")
    var href: String,

    @ColumnInfo(name = "avatar")
    var avatar: String,

    @ColumnInfo(name = "username")
    var username: String

)