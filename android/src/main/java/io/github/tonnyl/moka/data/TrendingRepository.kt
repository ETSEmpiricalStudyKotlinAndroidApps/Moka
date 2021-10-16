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
    val author: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "language")
    val language: String? = null,

    @ColumnInfo(name = "language_color")
    val languageColor: String? = null,

    @ColumnInfo(name = "stars")
    val stars: Int,

    @ColumnInfo(name = "forks")
    val forks: Int,

    @ColumnInfo(name = "current_period_stars")
    val currentPeriodStars: Int,

    @Contextual
    val builtBy: List<TrendingRepositoryBuiltBy>

)

@Serializable
data class TrendingRepositoryBuiltBy(

    @ColumnInfo(name = "href")
    val href: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "username")
    val username: String

)