package io.github.tonnyl.moka.ui.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.ui.repositories.RepositoryItemEvent.*

class RepositoriesViewModel(
    private val args: RepositoriesFragmentArgs
) : NetworkCacheSourceViewModel<RepositoryItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<RepositoryItem>>>()
    val initialLoadStatus: LiveData<Resource<List<RepositoryItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<RepositoryItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<RepositoryItem>>>
        get() = _pagedLoadStatus

    private val _event = MutableLiveData<Event<RepositoryItemEvent>>()
    val event: LiveData<Event<RepositoryItemEvent>>
        get() = _event

    private lateinit var sourceFactory: RepositoriesDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<RepositoryItem>> {
        sourceFactory = RepositoriesDataSourceFactory(
            args.login,
            args.repositoriesType,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    @MainThread
    fun viewRepository(
        login: String,
        repoName: String
    ) {
        _event.value = Event(ViewRepository(login, repoName))
    }

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login))
    }

    @MainThread
    fun starRepository(
        login: String,
        repoName: String
    ) {
        _event.value = Event(StarRepository(login, repoName))
    }

}