package io.github.tonnyl.moka.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.store.data.KeepData
import io.tonnyl.moka.common.store.data.NotificationSyncInterval
import io.tonnyl.moka.common.store.data.Theme
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
data class SettingsViewModelExtra(
    val accountInstance: AccountInstance,
)

@ExperimentalPagingApi
@ExperimentalSerializationApi
class SettingsViewModel(
    app: Application,
    private val extra: SettingsViewModelExtra
) : AndroidViewModel(app) {

    private val _updateSettingsStatus = MutableLiveData<Status?>()
    val updateSettingsStatus: LiveData<Status?>
        get() = _updateSettingsStatus

    private val _clearHistoryStatus = MutableLiveData<Status?>()
    val clearHistoryStatus: LiveData<Status?>
        get() = _clearHistoryStatus

    val settingsDataStore = getApplication<MokaApp>().settingsDataStore

    fun updateSettings(
        theme: Theme? = null,
        enableNotifications: Boolean? = null,
        notificationSyncInterval: NotificationSyncInterval? = null,
        dnd: Boolean? = null,
        doNotKeepSearchHistory: Boolean? = null,
        keepData: KeepData? = null,
        autoSave: Boolean? = null
    ) {
        viewModelScope.launch {
            try {
                _updateSettingsStatus.value = Status.LOADING

                settingsDataStore.updateData { oldValue ->
                    oldValue.copy(
                        theme = theme ?: oldValue.theme,
                        enableNotifications = enableNotifications ?: oldValue.enableNotifications,
                        notificationSyncInterval = notificationSyncInterval
                            ?: oldValue.notificationSyncInterval,
                        dnd = dnd ?: oldValue.dnd,
                        doNotKeepSearchHistory = doNotKeepSearchHistory
                            ?: oldValue.doNotKeepSearchHistory,
                        keepData = keepData ?: oldValue.keepData,
                        autoSave = autoSave ?: oldValue.autoSave
                    )
                }

                _updateSettingsStatus.value = Status.SUCCESS
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    "failed to update settings: ${e.asLog()}"
                }

                _updateSettingsStatus.value = Status.ERROR
            }
        }
    }

    fun onUpdateSettingsErrorDismissed() {
        _updateSettingsStatus.value = null
    }

    fun clearSearchHistory(updateStatus: Boolean = true) {
        viewModelScope.launch {
            try {
                if (updateStatus) {
                    _clearHistoryStatus.value = Status.LOADING
                }

                extra.accountInstance.searchHistoryDataStore.updateData {
                    it.copy(queries = emptyList())
                }

                if (updateStatus) {
                    _clearHistoryStatus.value = Status.SUCCESS
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    "failed to clear search history: ${e.asLog()}"
                }

                if (updateStatus) {
                    _clearHistoryStatus.value = Status.ERROR
                }
            }
        }
    }

    fun onClearSearchHistoryUiDismissed() {
        _clearHistoryStatus.value = null
    }

}