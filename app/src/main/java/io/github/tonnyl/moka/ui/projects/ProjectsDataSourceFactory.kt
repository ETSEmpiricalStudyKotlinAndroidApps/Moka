package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource

class ProjectsDataSourceFactory(
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    private val initialLoadStatusLiveData: MutableLiveData<Resource<List<Project>>>,
    private val previousNextStatusLiveData: MutableLiveData<PagedResource2<List<Project>>>
) : DataSource.Factory<String, Project>() {

    private var dataSource: ProjectsDataSource? = null

    override fun create(): DataSource<String, Project> {
        return ProjectsDataSource(
            login,
            isMyself,
            projectsDao,
            repositoryName,
            initialLoadStatusLiveData,
            previousNextStatusLiveData
        ).also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}