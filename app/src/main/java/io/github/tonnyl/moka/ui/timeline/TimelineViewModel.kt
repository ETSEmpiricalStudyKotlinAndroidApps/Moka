package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.EventsService

class TimelineViewModel : ViewModel() {

    private var userLogin: String = ""

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<Event>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<Event>>>
        get() = _loadStatusLiveData

    private val sourceFactory: TimelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(RetrofitClient.createService(EventsService::class.java), userLogin, _loadStatusLiveData)
    }

    val eventsResult: LiveData<PagedList<Event>> by lazy {
        val pagingConfig = PagedList.Config.Builder()
                .setPageSize(PER_PAGE)
                .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
                .setInitialLoadSizeHint(PER_PAGE)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, pagingConfig).build()
    }

    fun refreshEventsData(login: String, forceUpdate: Boolean) {
        fun refresh() {
            userLogin = login

            sourceFactory.login = userLogin
            sourceFactory.invalidate()
        }

        if (forceUpdate) {
            refresh()
        } else if (login == userLogin) {
            return
        }

        refresh()
    }

}