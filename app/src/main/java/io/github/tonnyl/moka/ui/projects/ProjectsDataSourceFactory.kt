package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.network.PagedResource
import kotlinx.coroutines.CoroutineScope

class ProjectsDataSourceFactory(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val repositoryName: String?,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : DataSource.Factory<String, Project>() {

    private val projectsLiveData = MutableLiveData<ProjectsDataSource>()

    override fun create(): DataSource<String, Project> {
        return ProjectsDataSource(coroutineScope, login, repositoryName, loadStatusLiveData).apply {
            projectsLiveData.postValue(this)
        }
    }

    fun invalidate() {
        projectsLiveData.value?.invalidate()
    }

}