package io.tonnyl.moka.common.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tonnyl.moka.common.db.data.Event
import kotlinx.datetime.Instant

@Dao
interface EventDao {

    @Query("SELECT * FROM event ORDER BY created_at DESC")
    fun eventsByCreatedAt(): PagingSource<Int, Event>

    @Query("SELECT * FROM event ORDER BY created_at DESC LIMIT 20")
    fun eventsByCreatedAtLimit20(): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Query("DELETE FROM event WHERE created_at < :date")
    fun deleteByCreatedAt(date: Instant)

    @Query("DELETE FROM event")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM event")
    fun eventsCount(): Int

}