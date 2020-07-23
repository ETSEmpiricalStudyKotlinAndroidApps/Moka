package io.github.tonnyl.moka.ui.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.repositories.RepositoryItemEvent.*

class RepositoriesViewModel(
    private val args: RepositoriesFragmentArgs
) : ViewModel() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<RepositoryItem>>>()
    val initialLoadStatus: LiveData<Resource<List<RepositoryItem>>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<RepositoryItemEvent>>()
    val event: LiveData<Event<RepositoryItemEvent>>
        get() = _event

    val repositoriesResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                pagingSourceFactory = {
                    when (args.repositoriesType) {
                        RepositoryType.STARRED -> {
                            StarredRepositoriesDataSource(args.login, _initialLoadStatus)
                        }
                        RepositoryType.OWNED -> {
                            OwnedRepositoriesDataSource(args.login, _initialLoadStatus)
                        }
                    }
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

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