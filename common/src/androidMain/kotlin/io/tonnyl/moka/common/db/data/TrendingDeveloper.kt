package io.tonnyl.moka.common.db.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.tonnyl.moka.common.data.TrendingDeveloper as SerializableTrendingDeveloper
import io.tonnyl.moka.common.data.TrendingDeveloperRepository as SerializableTrendingDeveloperRepositoryData

@Entity(tableName = "trending_developer")
data class TrendingDeveloper(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Transient // local field
    val id: Int = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "name")
    val name: String? = null,

    /**
     * could be organization or user.
     */
    @ColumnInfo(name = "type")
    val type: String? = null,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,

    @Embedded(prefix = "trending_developer_repository_")
    val repository: TrendingDeveloperRepository? = null

)

data class TrendingDeveloperRepository(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "url")
    val url: String

)

val SerializableTrendingDeveloper.dbModel: TrendingDeveloper
    get() = TrendingDeveloper(
        username = username,
        name = name,
        type = type,
        url = url,
        avatar = avatar,
        repository = repository?.dbModel
    )

val SerializableTrendingDeveloperRepositoryData.dbModel: TrendingDeveloperRepository
    get() = TrendingDeveloperRepository(
        name = name,
        description = description,
        url = url
    )