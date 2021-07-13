package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalPagingApi
class InboxViewModel(
    accountInstance: AccountInstance,
    app: Application
) : AndroidViewModel(app) {

    private val _isNeedDisplayPlaceholderLiveData = MutableLiveData<Boolean>()
    val isNeedDisplayPlaceholderLiveData: LiveData<Boolean>
        get() = _isNeedDisplayPlaceholderLiveData

    @ExperimentalPagingApi
    val notificationsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            remoteMediator = NotificationRemoteMediator(
                notificationsApi = accountInstance.notificationApi,
                database = accountInstance.database,
                isNeedDisplayPlaceholder = _isNeedDisplayPlaceholderLiveData
            ),
            pagingSourceFactory = {
                accountInstance.database.notificationsDao().notificationsByDate()
            }
        ).flow.cachedIn(viewModelScope)
    }

}