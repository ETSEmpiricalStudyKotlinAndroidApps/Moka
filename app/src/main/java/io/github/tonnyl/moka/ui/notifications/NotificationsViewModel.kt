package io.github.tonnyl.moka.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.net.NetworkState

class NotificationsViewModel(
        private val repository: NotificationsRepository
) : ViewModel() {

    private val refreshTime = MutableLiveData<String>()
    private val notificationsResult = map(refreshTime) { repository.notifications(20) }

    val posts: LiveData<PagedList<Notification>> = switchMap(notificationsResult) { it.pagedList }
    val networkState: LiveData<NetworkState> = switchMap(notificationsResult) { it.networkState }
    val refreshState: LiveData<NetworkState> = switchMap(notificationsResult) { it.refreshState }

    fun refreshNotificationList(timeString: String) {
        refreshTime.value = timeString

        notificationsResult.value?.refresh?.invoke()
    }

    fun retry() {
        notificationsResult?.value?.retry?.invoke()
    }

}