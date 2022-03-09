package io.tonnyl.moka.common.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig

data class TimelineViewModelExtra(
    val accountInstance: AccountInstance
)

class TimelineViewModel(extra: TimelineViewModelExtra) : ViewModel() {

    private val _isNeedDisplayPlaceholderLiveData = MutableLiveData<Boolean>()
    val isNeedDisplayPlaceholderLiveData: LiveData<Boolean>
        get() = _isNeedDisplayPlaceholderLiveData

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