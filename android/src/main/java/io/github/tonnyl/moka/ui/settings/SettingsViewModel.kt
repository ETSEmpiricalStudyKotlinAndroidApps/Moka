package io.github.tonnyl.moka.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
class SettingsViewModel(
    private val accountInstance: AccountInstance,
    app: Application
) : AndroidViewModel(app) {

    private val mokaApp = app as MokaApp

    private val _updateSettingsStatus = MutableLiveData<Status>()
    val updateSettingsStatus: LiveData<Status>
        get() = _updateSettingsStatus

    private val _clearHistoryStatus = MutableLiveData<Status>()
    val clearHistoryStatus: LiveData<Status>
        get() = _clearHistoryStatus

    val settingsDataStore = mokaApp.settingsDataStore

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

    fun clearSearchHistory(updateStatus: Boolean = true) {
        viewModelScope.launch {
            try {
                if (updateStatus) {
                    _clearHistoryStatus.value = Status.LOADING
                }

                accountInstance.searchHistoryDataStore.updateData {
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

}