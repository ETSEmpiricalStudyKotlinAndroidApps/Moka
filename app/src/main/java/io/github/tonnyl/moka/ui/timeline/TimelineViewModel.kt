package io.github.tonnyl.moka.ui.timeline

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalPagingApi
class TimelineViewModel(
    accountInstance: AccountInstance,
    app: Application
) : AndroidViewModel(app) {

    @ExperimentalPagingApi
    val eventsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            remoteMediator = EventRemoteMediator(
                login = accountInstance.signedInAccount.account.login,
                eventApi = accountInstance.eventApi,
                database = accountInstance.database
            ),
            pagingSourceFactory = {
                accountInstance.database
                    .eventDao()
                    .eventsByCreatedAt()
            }
        ).flow.cachedIn(viewModelScope)
    }

}