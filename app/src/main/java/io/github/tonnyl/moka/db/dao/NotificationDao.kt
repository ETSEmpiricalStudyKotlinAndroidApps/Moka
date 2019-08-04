package io.github.tonnyl.moka.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.tonnyl.moka.data.Notification
import java.util.*

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification ORDER BY updated_at DESC")
    fun notificationsByDate(): DataSource.Factory<Int, Notification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notifications: List<Notification>)

    @Query("UPDATE notification SET last_read_at = :lastReadAt WHERE last_read_at < last_read_at")
    fun markRead(lastReadAt: Date)

    @Query("DELETE FROM notification")
    fun deleteAll()

}