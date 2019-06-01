package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.network.PagedResource

class ProjectsDataSourceFactory(
        private val login: String,
        private val repositoryName: String?,
        private val loadStatusLiveData: MutableLiveData<PagedResource<List<Project>>>
) : DataSource.Factory<String, Project>() {

    private val projectsLiveData = MutableLiveData<ProjectsDataSource>()

    override fun create(): DataSource<String, Project> = ProjectsDataSource(login, repositoryName, loadStatusLiveData).apply {
        projectsLiveData.postValue(this)
    }

    fun invalidate() {
        projectsLiveData.value?.invalidate()
    }

}