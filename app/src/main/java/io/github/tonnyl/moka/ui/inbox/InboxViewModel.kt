package io.github.tonnyl.moka.ui.inbox

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.NotificationRepository
import io.github.tonnyl.moka.data.NotificationRepositoryOwner
import io.github.tonnyl.moka.data.profileType
import io.github.tonnyl.moka.db.dao.NotificationDao
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel
import io.github.tonnyl.moka.ui.inbox.NotificationItemEvent.*

class InboxViewModel(
    private val localSource: NotificationDao
) : NetworkDatabaseSourceViewModel<Notification>() {

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<List<Notification>>>()
    val initialLoadStatusLiveData: LiveData<Resource<List<Notification>>>
        get() = _initialLoadStatusLiveData

    private val _previousNextLoadStatusLiveData =
        MutableLiveData<PagedResource2<List<Notification>>>()
    val previousNextLoadStatusLiveData: LiveData<PagedResource2<List<Notification>>>
        get() = _previousNextLoadStatusLiveData

    private val _event = MutableLiveData<Event<NotificationItemEvent>>()
    val event: LiveData<Event<NotificationItemEvent>>
        get() = _event

    private lateinit var sourceFactory: InboxDataSourceFactory

    override fun initLocalSource(): LiveData<PagedList<Notification>> {
        return LivePagedListBuilder(
            localSource.notificationsByDate(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Notification>> {
        sourceFactory = InboxDataSourceFactory(
            RetrofitClient.createService(NotificationsService::class.java),
            localSource,
            _initialLoadStatusLiveData,
            _previousNextLoadStatusLiveData
        )
        return LivePagedListBuilder(
            sourceFactory,
            pagingConfig
        ).build()
    }

    fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

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