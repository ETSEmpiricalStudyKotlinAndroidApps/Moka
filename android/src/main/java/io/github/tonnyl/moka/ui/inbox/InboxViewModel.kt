package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class InboxViewModelExtra(
    val accountInstance: AccountInstance,
)

@ExperimentalSerializationApi
@ExperimentalPagingApi
class InboxViewModel(
    extra: InboxViewModelExtra,
    app: Application
) : AndroidViewModel(app) {

    private val _isNeedDisplayPlaceholderLiveData = MutableLiveData<Boolean>()
    val isNeedDisplayPlaceholderLiveData: LiveData<Boolean>
        get() = _isNeedDisplayPlaceholderLiveData

    @ExperimentalPagingApi
    val notificationsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            remoteMediator = NotificationRemoteMediator(
                notificationsApi = extra.accountInstance.notificationApi,
                database = extra.accountInstance.database,
                isNeedDisplayPlaceholder = _isNeedDisplayPlaceholderLiveData
            ),
            pagingSourceFactory = {
                extra.accountInstance.database.notificationsDao().notificationsByDate()
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object InboxViewModelExtraKeyImpl : CreationExtras.Key<InboxViewModelExtra>

        val INBOX_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<InboxViewModelExtra> =
            InboxViewModelExtraKeyImpl

    }

}