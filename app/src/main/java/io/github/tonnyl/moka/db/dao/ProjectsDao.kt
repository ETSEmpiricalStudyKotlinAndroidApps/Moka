package io.github.tonnyl.moka.db.dao

import androidx.paging.DataSource
import androidx.room.*
import io.github.tonnyl.moka.data.item.Project

@Dao
interface ProjectsDao {

    @Query("SELECT * FROM project ORDER BY updated_at DESC")
    fun projectsByUpdatedAt(): DataSource.Factory<Int, Project>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(projects: List<Project>)

    @Delete
    fun delete(project: Project)

}