package io.github.tonnyl.moka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE

abstract class NetworkDatabaseSourceViewModel<T> : ViewModel() {

    var login = ""
        private set

    private lateinit var remoteData: LiveData<PagedList<T>>
    private lateinit var localData: LiveData<PagedList<T>>

    private val _data = MediatorLiveData<PagedList<T>>()
    val data: LiveData<PagedList<T>>
        get() = _data

    val pagingConfig = PagedList.Config.Builder()
        .setPageSize(PER_PAGE)
        .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
        .setInitialLoadSizeHint(PER_PAGE)
        .setEnablePlaceholders(false)
        .build()

    abstract fun initLocalSource(): LiveData<PagedList<T>>

    abstract fun initRemoteSource(): LiveData<PagedList<T>>

    fun refreshData(login: String, forceUpdate: Boolean) {
        when {
            forceUpdate -> {
                this.login = login
                refresh()
            }
            login == this.login -> {
                return
            }
            else -> {
                this.login = login
                refresh()
            }
        }
    }

    private fun refresh() {
        if (::localData.isInitialized) {
            _data.removeSource(localData)
        }
        if (::remoteData.isInitialized) {
            _data.removeSource(remoteData)
        }

        localData = initLocalSource()
        remoteData = initRemoteSource()

        _data.addSource(localData) { firstLocalEventsData ->
            if (_data.value != firstLocalEventsData) {
                _data.value = firstLocalEventsData
            }

            _data.removeSource(localData)
            _data.addSource(remoteData) { remoteChangedData ->
                if (remoteChangedData.isNullOrEmpty()) {
                    _data.removeSource(remoteData)
                    _data.addSource(localData) { secondLocalEventData ->
                        if (_data.value != secondLocalEventData) {
                            _data.value = secondLocalEventData
                        }
                    }
                } else {
                    if (_data.value != remoteChangedData) {
                        _data.value = remoteChangedData
                    }
                }
            }
        }
    }

}