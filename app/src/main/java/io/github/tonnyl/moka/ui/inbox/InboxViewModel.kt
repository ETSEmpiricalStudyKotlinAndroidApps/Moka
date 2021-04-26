package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp

@ExperimentalPagingApi
class InboxViewModel(
    accountInstance: AccountInstance,
    app: Application
) : AndroidViewModel(app) {

    @ExperimentalPagingApi
    val notificationsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            remoteMediator = NotificationRemoteMediator(
                notificationsApi = accountInstance.notificationApi,
                database = accountInstance.database
            ),
            pagingSourceFactory = {
                accountInstance.database.notificationsDao().notificationsByDate()
            }
        ).flow.cachedIn(viewModelScope)
    }

}