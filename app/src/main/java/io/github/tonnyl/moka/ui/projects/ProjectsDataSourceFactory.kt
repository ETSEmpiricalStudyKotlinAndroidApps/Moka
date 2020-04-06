package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.PageKeyedDataSourceWithLoadState

class ProjectsDataSourceFactory(
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val type: ProjectsType,
    private val repoName: String,
    private val initialLoadStatusLiveData: MutableLiveData<Resource<List<Project>>>,
    private val previousNextStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : DataSource.Factory<String, Project>() {

    private var dataSource: PageKeyedDataSourceWithLoadState<Project>? = null

    override fun create(): DataSource<String, Project> {
        return when (type) {
            ProjectsType.UsersProjects -> {
                UsersProjectsDataSource(
                    login,
                    isMyself,
                    projectsDao,
                    initialLoadStatusLiveData,
                    previousNextStatusLiveData
                )
            }
            ProjectsType.OrganizationsProjects -> {
                OrganizationsProjectsDataSource(
                    login,
                    initialLoadStatusLiveData,
                    previousNextStatusLiveData
                )
            }
            ProjectsType.RepositoriesProjects -> {
                RepositoriesProjectsDataSource(
                    login,
                    repoName,
                    initialLoadStatusLiveData,
                    previousNextStatusLiveData
                )
            }
        }.also {
            dataSource = it
        }
    }

    fun retryLoadPreviousNext() {
        dataSource?.retry?.invoke()
    }

}