package io.github.tonnyl.moka.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import io.github.tonnyl.moka.data.Notification
import java.util.*

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification ORDER BY updated_at DESC")
    fun notificationsByDate(): PagingSource<Int, Notification>

    /**
     * SQLite does not have a boolean data type.
     * Room maps it to an INTEGER column, mapping true to 1 and false to 0.
     */
    @Query("SELECT * FROM notification WHERE has_displayed = 0 ORDER BY updated_at ASC LIMIT :limit")
    fun notificationsToDisplayWithLimit(limit: Int): List<Notification>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(notifications: List<Notification>)

    @Query("UPDATE notification SET last_read_at = :lastReadAt WHERE last_read_at < last_read_at")
    fun markAsRead(lastReadAt: Date)

    @Update
    fun markAsDisplayed(notification: Notification)

    @Query("DELETE FROM notification")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM notification")
    fun notificationsCount(): Int

}