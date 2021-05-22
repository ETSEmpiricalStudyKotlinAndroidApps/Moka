package io.github.tonnyl.moka.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Entity(tableName = "trending_developer")
@Serializable
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

    @SerialName("repo")
    @Contextual
    @Embedded(prefix = "trending_developer_repository_")
    val repository: TrendingDeveloperRepository? = null

)

@Serializable
data class TrendingDeveloperRepository(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "url")
    val url: String

)