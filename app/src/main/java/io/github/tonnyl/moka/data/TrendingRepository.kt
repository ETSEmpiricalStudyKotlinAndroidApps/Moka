package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "trending_repository")
@Serializable
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
    var description: String? = null,

    @ColumnInfo(name = "language")
    var language: String? = null,

    @ColumnInfo(name = "language_color")
    var languageColor: String? = null,

    @ColumnInfo(name = "stars")
    var stars: Int,

    @ColumnInfo(name = "forks")
    var forks: Int,

    @ColumnInfo(name = "current_period_stars")
    var currentPeriodStars: Int,

    @Contextual
    var builtBy: List<TrendingRepositoryBuiltBy>

)

@Serializable
data class TrendingRepositoryBuiltBy(

    @ColumnInfo(name = "href")
    var href: String,

    @ColumnInfo(name = "avatar")
    var avatar: String,

    @ColumnInfo(name = "username")
    var username: String

)