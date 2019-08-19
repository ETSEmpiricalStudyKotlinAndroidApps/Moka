package io.github.tonnyl.moka.ui

import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE

abstract class NetworkCacheSourceViewModel<T> : ViewModel() {

    private lateinit var remoteData: LiveData<PagedList<T>>
    private val cachedData: MutableLiveData<PagedList<T>> = MutableLiveData()

    private val _data = MediatorLiveData<PagedList<T>>()
    val data: LiveData<PagedList<T>>
        get() = _data

    val pagingConfig = PagedList.Config.Builder()
        .setPageSize(PER_PAGE)
        .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
        .setInitialLoadSizeHint(PER_PAGE)
        .setEnablePlaceholders(false)
        .build()

    abstract fun initRemoteSource(): LiveData<PagedList<T>>

    abstract fun retryLoadPreviousNext()

    @CallSuper
    open fun refresh() {
        _data.removeSource(cachedData)

        if (::remoteData.isInitialized) {
            _data.removeSource(remoteData)
        }

        remoteData = initRemoteSource()

        _data.value = cachedData.value

        _data.addSource(remoteData) { remoteValue ->
            if (remoteValue.isNullOrEmpty()) {
                _data.removeSource(remoteData)
                _data.addSource(cachedData) { cachedData ->
                    if (_data.value != cachedData) {
                        _data.value = cachedData
                    }
                }
            } else {
                if (_data.value != remoteValue) {
                    _data.value = remoteValue
                }

                cachedData.value = remoteValue
            }
        }
    }

}