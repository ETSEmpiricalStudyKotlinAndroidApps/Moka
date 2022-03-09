package io.github.tonnyl.moka.ui.explore.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreSpokenLanguage
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

data class ExploreFiltersViewModelExtra(
    val accountInstance: AccountInstance
)

class ExploreFiltersViewModel(private val extra: ExploreFiltersViewModelExtra): ViewModel() {

    private val _options =
        extra.accountInstance.exploreOptionsDataStore.data.asLiveData(context = viewModelScope.coroutineContext)
    val options: LiveData<ExploreOptions>
        get() = _options

    fun updateExploreOptions(
        exploreLanguage: ExploreLanguage,
        spokenLanguage: ExploreSpokenLanguage,
    ) {
        if (exploreLanguage == options.value?.exploreLanguage
            && spokenLanguage == options.value?.exploreSpokenLanguage
        ) {
            return
        }

        viewModelScope.launch {
            try {
                extra.accountInstance.exploreOptionsDataStore.updateData {
                    ExploreOptions(
                        timeSpan = options.value?.timeSpan ?: ExploreOptionsSerializer.defaultValue.timeSpan,
                        exploreLanguage = exploreLanguage,
                        exploreSpokenLanguage = spokenLanguage
                    )
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}