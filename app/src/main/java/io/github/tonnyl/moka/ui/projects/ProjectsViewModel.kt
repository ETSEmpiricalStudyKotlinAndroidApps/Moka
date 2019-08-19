package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.dao.ProjectsDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel

class ProjectsViewModel(
    private val isMyself: Boolean,
    private val localSource: ProjectsDao,
    private val repositoryName: String?
) : NetworkDatabaseSourceViewModel<Project>() {

    private var sourceFactory: ProjectsDataSourceFactory? = null

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<List<Project>>>()
    val initialLoadStatusLiveData: LiveData<Resource<List<Project>>>
        get() = _initialLoadStatusLiveData

    private val _previousNextLoadStatusLiveData = MutableLiveData<PagedResource2<List<Project>>>()
    val previousNextLoadStatusLiveData: LiveData<PagedResource2<List<Project>>>
        get() = _previousNextLoadStatusLiveData

    override fun initLocalSource(): LiveData<PagedList<Project>> {
        return LivePagedListBuilder(
            localSource.projectsByUpdatedAt(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Project>> {
        val sourceFactory = ProjectsDataSourceFactory(
            login,
            isMyself,
            localSource,
            repositoryName,
            _initialLoadStatusLiveData,
            _previousNextLoadStatusLiveData
        )

        return LivePagedListBuilder(
            sourceFactory,
            pagingConfig
        ).build()
    }

    fun retryLoadPreviousNext() {
        sourceFactory?.retryLoadPreviousNext()
    }

}