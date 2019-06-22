package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.util.formatISO8601String

class NotificationsViewModel : ViewModel() {

    private var userLogin: String = ""

    val refreshTime = MutableLiveData<String>()

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Notification>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Notification>>>
        get() = _loadStatusLiveData

    private val sourceFactory: NotificationsDataSourceFactory by lazy {
        NotificationsDataSourceFactory(
            viewModelScope,
            RetrofitClient.createService(NotificationsService::class.java),
            _loadStatusLiveData
        )
    }

    val notificationsResult: LiveData<PagedList<Notification>> by lazy {
        val pagingConfig = PagedList.Config.Builder()
            .setPageSize(PER_PAGE)
            .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
            .setInitialLoadSizeHint(PER_PAGE)
            .setEnablePlaceholders(false)
            .build()

        LivePagedListBuilder(sourceFactory, pagingConfig).build()
    }

    fun refreshNotificationsData(login: String, forceUpdate: Boolean) {
        fun refresh() {
            userLogin = login
            refreshTime.value = formatISO8601String()

            sourceFactory.invalidate()
        }

        if (forceUpdate) {
            refresh()
        } else if (login == userLogin) {
            return
        }

        refresh()
    }

    fun retry() {
        sourceFactory.retry()
    }

}