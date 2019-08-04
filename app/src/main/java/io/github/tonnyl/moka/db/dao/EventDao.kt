package io.github.tonnyl.moka.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.tonnyl.moka.data.Event
import java.util.*

@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY created_at DESC")
    fun eventsByCreatedAt(): DataSource.Factory<Int, Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Query("DELETE FROM event WHERE created_at < :date")
    fun deleteByCreatedAt(date: Date)

}