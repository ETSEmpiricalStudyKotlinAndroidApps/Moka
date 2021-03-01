package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService

@ExperimentalPagingApi
class InboxViewModel(
    userId: Long,
    app: Application
) : AndroidViewModel(app) {

    @ExperimentalPagingApi
    val notificationsFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            remoteMediator = NotificationRemoteMediator(
                RetrofitClient.createService(NotificationsService::class.java),
                MokaDataBase.getInstance(getApplication(), userId)
            ),
            pagingSourceFactory = {
                MokaDataBase.getInstance(getApplication(), userId)
                    .notificationsDao()
                    .notificationsByDate()
            }
        ).flow.cachedIn(viewModelScope)
    }

}