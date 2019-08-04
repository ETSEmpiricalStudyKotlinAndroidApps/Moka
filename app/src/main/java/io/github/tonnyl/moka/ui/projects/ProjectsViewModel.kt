package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class ProjectsViewModel(
    private val isMyself: Boolean,
    private val localSource: ProjectsDao,
    private val repositoryName: String?
) : NetworkDatabaseSourceViewModel<Project>() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Project>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Project>>>
        get() = _loadStatusLiveData

    override fun initLocalSource(): LiveData<PagedList<Project>> {
        return LivePagedListBuilder(
            localSource.projectsByUpdatedAt(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Project>> {
        return LivePagedListBuilder(
            ProjectsDataSourceFactory(
                viewModelScope,
                login,
                isMyself,
                localSource,
                repositoryName,
                _loadStatusLiveData
            ),
            pagingConfig
        ).build()
    }

}