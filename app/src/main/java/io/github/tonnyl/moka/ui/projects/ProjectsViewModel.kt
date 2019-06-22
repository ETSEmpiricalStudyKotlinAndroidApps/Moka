package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.network.PagedResource
import kotlinx.coroutines.CoroutineScope

class ProjectsViewModel(
    private val login: String,
    private val repositoryName: String?
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Project>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Project>>>
        get() = _loadStatusLiveData

    private val sourceFactory = ProjectsDataSourceFactory(viewModelScope, login, repositoryName, _loadStatusLiveData)

    val projectsResult: LiveData<PagedList<Project>> by lazy {
        val pagingConfig = PagedList.Config.Builder()
            .setPageSize(6)
            .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
            .setInitialLoadSizeHint(PER_PAGE)
            .setEnablePlaceholders(false)
            .build()

        LivePagedListBuilder(sourceFactory, pagingConfig).build()
    }

    fun refreshProjectsData() {
        sourceFactory.invalidate()
    }

}