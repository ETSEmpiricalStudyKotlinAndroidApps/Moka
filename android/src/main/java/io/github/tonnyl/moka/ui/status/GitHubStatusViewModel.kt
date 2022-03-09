package io.github.tonnyl.moka.ui.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.GitHubStatus
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class GitHubStatusViewModel(val accountInstance: AccountInstance) : ViewModel() {

    private val _gitHubStatus = MutableLiveData<GitHubStatus>()
    val gitHubStatus: LiveData<GitHubStatus>
        get() = _gitHubStatus

    private val _requestResource = MutableLiveData<Resource<Unit>?>()
    val requestResource: LiveData<Resource<Unit>?>
        get() = _requestResource

    var s = false

    init {
        refresh()
    }

    fun refresh() {
        if (requestResource.value?.status == Status.LOADING) {
            return
        }

        viewModelScope.launch {
            try {
                _requestResource.value = Resource.loading(data = null)

                if (!s) {
                    val resp = accountInstance.gitHubStatusApi.getSummary()

                    _gitHubStatus.value = resp.copy(
                        components = resp.components.filter {
                            !it.name.startsWith("Visit www") // has some unexpected items...
                        }
                    )

                    s = true
                } else {
                    throw IllegalStateException()
                }


                _requestResource.value = Resource.success(data = null)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _requestResource.value = Resource.error(exception = e, data = null)
            }
        }
    }

    fun onGitHubStatusDataErrorDismissed() {
        _requestResource.value = null
    }

}