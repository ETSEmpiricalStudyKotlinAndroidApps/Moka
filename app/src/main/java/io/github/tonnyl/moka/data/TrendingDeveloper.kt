package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "trending_developer")
@Serializable
data class TrendingDeveloper(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Transient // local field
    var id: Int = 0,

    @ColumnInfo(name = "username")
    var username: String,

    @ColumnInfo(name = "name")
    var name: String? = null,

    /**
     * could be organization or user.
     */
    @ColumnInfo(name = "type")
    var type: String? = null,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "avatar")
    var avatar: String,

    @Contextual
    @Embedded(prefix = "trending_developer_repository_")
    var repository: TrendingDeveloperRepository

)

@Serializable
data class TrendingDeveloperRepository(

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "url")
    var url: String

)