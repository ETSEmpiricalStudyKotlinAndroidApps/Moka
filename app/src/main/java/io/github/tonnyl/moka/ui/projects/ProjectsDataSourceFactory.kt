package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource
import kotlinx.coroutines.CoroutineScope

class ProjectsDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : DataSource.Factory<String, Project>() {

    override fun create(): DataSource<String, Project> {
        return ProjectsDataSource(
            coroutineScope,
            login,
            isMyself,
            projectsDao,
            repositoryName,
            loadStatusLiveData
        )
    }

}