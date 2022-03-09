package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

data class InboxViewModelExtra(
    val accountInstance: AccountInstance,
)

class InboxViewModel(
    private val extra: InboxViewModelExtra,
    app: Application
) : AndroidViewModel(app) {

    private val _isNeedDisplayPlaceholderLiveData = MutableLiveData<Boolean>()
    val isNeedDisplayPlaceholderLiveData: LiveData<Boolean>
        get() = _isNeedDisplayPlaceholderLiveData

    private val releaseIdToTagNameMap = mutableMapOf<String, String>()

    private val _releaseData = MutableLiveData<Resource<Unit>?>()
    val releaseData: LiveData<Resource<Unit>?>
        get() = _releaseData

    val pendingJumpState = mutableStateOf<Triple<String, String, String>?>(value = null)

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

    fun fetchReleaseData(
        login: String,
        repoName: String,
        url: String
    ) {
        val tagName = releaseIdToTagNameMap[url]
        if (!tagName.isNullOrEmpty()) {
            pendingJumpState.value = Triple(login, repoName, tagName)

            return
        }

        viewModelScope.launch {
            try {
                _releaseData.value = Resource.loading(data = null)

                val resp = extra.accountInstance.repositoryApi.releaseByUrl(url = url)
                releaseIdToTagNameMap[url] = resp.tagName
                pendingJumpState.value = Triple(login, repoName, resp.tagName)

                _releaseData.value = Resource.success(data = Unit)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    e.asLog()
                }

                _releaseData.value = Resource.error(exception = e, data = null)
            }
        }
    }

    fun onReleaseDataErrorDismissed() {
        _releaseData.value = null
    }

}