package io.github.tonnyl.moka.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository

@Dao
interface TrendingRepositoryDao {

    @Query("SELECT * FROM trending_repository")
    fun trendingRepositories(): LiveData<List<TrendingRepository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(repositories: List<TrendingRepository>)

    @Query("DELETE FROM trending_repository")
    fun deleteAll()

}

@Dao
interface TrendingDeveloperDao {

    @Query("SELECT * FROM trending_developer")
    fun trendingDevelopers(): LiveData<List<TrendingDeveloper>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(developers: List<TrendingDeveloper>)

    @Query("DELETE FROM trending_developer")
    fun deleteAll()

}