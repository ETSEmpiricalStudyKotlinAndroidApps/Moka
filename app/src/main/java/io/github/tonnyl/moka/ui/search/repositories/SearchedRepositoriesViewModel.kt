package io.github.tonnyl.moka.ui.search.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoryItemEvent.*

class SearchedRepositoriesViewModel : ViewModel() {

    private val _event = MutableLiveData<Event<SearchedRepositoryItemEvent>>()
    val event: LiveData<Event<SearchedRepositoryItemEvent>>
        get() = _event

    private val _initialLoadStatus = MutableLiveData<Resource<List<SearchedRepositoryItem>>>()
    val initialLoadStatus: LiveData<Resource<List<SearchedRepositoryItem>>>
        get() = _initialLoadStatus

    private val queryStringLiveData = MutableLiveData<String>()

    val repositoryResult = queryStringLiveData.switchMap { queryString ->
        liveData {
            emitSource(
                Pager(
                    config = MokaApp.defaultPagingConfig,
                    pagingSourceFactory = {
                        SearchedRepositoriesPagingSource(queryString, _initialLoadStatus)
                    }
                ).liveData
            )
        }.cachedIn(viewModelScope)
    }

    @MainThread
    fun refresh(queryString: String) {
        if (queryString == queryStringLiveData.value
            && initialLoadStatus.value?.status != Status.ERROR
        ) {
            return
        }

        queryStringLiveData.value = queryString
    }

    @MainThread
    fun viewProfile(login: String) {
        _event.value = Event(ViewProfile(login))
    }

    @MainThread
    fun viewRepository(
        login: String,
        repoName: String
    ) {
        _event.value = Event(ViewRepository(login, repoName))
    }

    @MainThread
    fun starRepository(repo: SearchedRepositoryItem) {
        _event.value = Event(StarRepository(repo))
    }

}