package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.EventsService
import com.apollographql.apollo.api.Response as ApolloResponse
import retrofit2.Response as RetrofitResponse

class TimelineViewModel : ViewModel() {

    private val sourceFactory = TimelineDataSourceFactory(RetrofitClient.createService(EventsService::class.java, null), "tonnyl")

    val eventsList: LiveData<PagedList<Event>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

}