package io.tonnyl.moka.common.ui.timeline

import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class TimelineViewModelExtra(
    val accountInstance: AccountInstance
)

@ExperimentalSerializationApi
@ExperimentalPagingApi
class TimelineViewModel(extra: TimelineViewModelExtra) : ViewModel() {

    private val _isNeedDisplayPlaceholderLiveData = MutableLiveData<Boolean>()
    val isNeedDisplayPlaceholderLiveData: LiveData<Boolean>
        get() = _isNeedDisplayPlaceholderLiveData

    @ExperimentalPagingApi
    val eventsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            remoteMediator = EventRemoteMediator(
                login = extra.accountInstance.signedInAccount.account.login,
                eventApi = extra.accountInstance.eventApi,
                database = extra.accountInstance.database,
                isNeedDisplayPlaceholder = _isNeedDisplayPlaceholderLiveData
            ),
            pagingSourceFactory = {
                extra.accountInstance.database
                    .eventDao()
                    .eventsByCreatedAt()
            }
        ).flow.cachedIn(viewModelScope)
    }

}