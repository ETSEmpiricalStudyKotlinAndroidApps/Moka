package io.tonnyl.moka.common.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tonnyl.moka.common.data.TrendingRepository as SerializableTrendingRepository
import io.tonnyl.moka.common.data.TrendingRepositoryBuiltBy as SerializableTrendingRepositoryBuiltBy

@Entity(tableName = "trending_repository")
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

    val builtBy: List<TrendingRepositoryBuiltBy>?

)

data class TrendingRepositoryBuiltBy(

    @ColumnInfo(name = "href")
    val href: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @ColumnInfo(name = "username")
    val username: String

)

val SerializableTrendingRepository.dbModel: TrendingRepository
    get() = TrendingRepository(
        author = author,
        name = name,
        avatar = avatar,
        url = url,
        description = description,
        language = language,
        languageColor = languageColor,
        stars = stars,
        forks = forks,
        currentPeriodStars = currentPeriodStars,
        builtBy = builtBy?.map { it.dbModel }
    )

val SerializableTrendingRepositoryBuiltBy.dbModel: TrendingRepositoryBuiltBy
    get() = TrendingRepositoryBuiltBy(
        href = href,
        avatar = avatar,
        username = username
    )