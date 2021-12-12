package io.tonnyl.moka.common.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.tonnyl.moka.common.db.data.Notification
import kotlinx.datetime.Instant

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

    @Query("UPDATE notification SET last_read_at = :lastReadAt WHERE last_read_at < :lastReadAt")
    fun markAsRead(lastReadAt: Instant)

    @Query("UPDATE notification SET has_displayed = 1 WHERE id = :id")
    fun markAsDisplayed(id: String)

    @Query("SELECT * FROM notification WHERE id = :id")
    fun notificationById(id: String): Notification?

    @Query("DELETE FROM notification")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM notification")
    fun notificationsCount(): Int

}