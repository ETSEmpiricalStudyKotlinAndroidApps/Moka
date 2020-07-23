package io.github.tonnyl.moka.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.tonnyl.moka.data.RemoteKeys

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    fun remoteKeysId(id: String): RemoteKeys?

    /**
     * @param idLike should be formatted like:
     */
    @Query("DELETE FROM remote_keys WHERE id LIKE :idLike")
    fun clearRemoteKeys(idLike: String)

}