package io.github.tonnyl.moka.db.dao

import androidx.paging.PagingSource
import androidx.room.*
import io.github.tonnyl.moka.data.item.Project

@Dao
interface ProjectsDao {

    @Query("SELECT * FROM project ORDER BY updated_at DESC")
    fun projectsByUpdatedAt(): PagingSource<Int, Project>

    @Query("SELECT * FROM project ORDER BY updated_at DESC")
    fun projectListByUpdatedAt(): List<Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(projects: List<Project>)

    @Delete
    fun delete(project: Project)

    @Query("DELETE FROM event")
    fun deleteAll()

}