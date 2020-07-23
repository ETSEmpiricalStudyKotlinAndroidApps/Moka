package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.NotificationRepository
import io.github.tonnyl.moka.data.NotificationRepositoryOwner
import io.github.tonnyl.moka.data.profileType
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.inbox.NotificationItemEvent.*

class InboxViewModel(
    app: Application
) : AndroidViewModel(app) {

    var login: String = ""
    var userId: Long = 0L

    private val _initialLoadStatus = MutableLiveData<Resource<Boolean?>>()
    val initialLoadStatus: LiveData<Resource<Boolean?>>
        get() = _initialLoadStatus

    private val _event = MutableLiveData<Event<NotificationItemEvent>>()
    val event: LiveData<Event<NotificationItemEvent>>
        get() = _event

    val notificationResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                remoteMediator = NotificationRemoteMediator(
                    RetrofitClient.createService(NotificationsService::class.java),
                    MokaDataBase.getInstance(getApplication(), userId),
                    _initialLoadStatus
                ),
                pagingSourceFactory = {
                    MokaDataBase.getInstance(getApplication(), userId)
                        .notificationsDao()
                        .notificationsByDate()
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

    @MainThread
    fun viewNotification(notification: Notification) {
        _event.value = Event(ViewNotification(notification))
    }

    @MainThread
    fun viewProfile(owner: NotificationRepositoryOwner) {
        _event.value = Event(ViewProfile(owner.login, owner.profileType))
    }

    @MainThread
    fun viewRepository(repo: NotificationRepository) {
        _event.value = Event(ViewRepository(repo.owner.login, repo.name, repo.owner.profileType))
    }

}