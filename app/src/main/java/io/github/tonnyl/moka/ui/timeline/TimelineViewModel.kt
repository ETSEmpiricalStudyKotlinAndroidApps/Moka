package io.github.tonnyl.moka.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.EventsService

@ExperimentalPagingApi
class TimelineViewModel(
    login: String,
    userId: Long,
    app: Application
) : AndroidViewModel(app) {

    @ExperimentalPagingApi
    val eventsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            remoteMediator = EventRemoteMediator(
                login,
                RetrofitClient.createService(EventsService::class.java),
                MokaDataBase.getInstance(getApplication(), userId)
            ),
            pagingSourceFactory = {
                MokaDataBase.getInstance(getApplication(), userId)
                    .eventDao()
                    .eventsByCreatedAt()
            }
        ).flow.cachedIn(viewModelScope)
    }

}