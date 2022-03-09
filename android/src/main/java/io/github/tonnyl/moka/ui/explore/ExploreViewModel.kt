package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.*
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.db.data.dbModel
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreTimeSpan
import io.tonnyl.moka.common.store.data.urlParamValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

data class ExploreViewModelExtra(
    val accountInstance: AccountInstance
)

class ExploreViewModel(private val extra: ExploreViewModelExtra) : ViewModel() {

    private val _options =
        extra.accountInstance.exploreOptionsDataStore.data.asLiveData(context = viewModelScope.coroutineContext)
    val options: LiveData<ExploreOptions>
        get() = _options

    private val _refreshDataStatus = MutableLiveData<Resource<Unit>>()
    val refreshDataStatus: LiveData<Resource<Unit>>
        get() = _refreshDataStatus

    val repositoriesLocalData =
        extra.accountInstance.database.trendingRepositoriesDao().trendingRepositories()
    val developersLocalData =
        extra.accountInstance.database.trendingDevelopersDao().trendingDevelopers()

    init {
        viewModelScope.launch {
            try {
                extra.accountInstance.exploreOptionsDataStore.data.collect {
                    refreshTrendingData()
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

    fun refreshTrendingData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _refreshDataStatus.postValue(Resource.loading(data = null))

                val options = runBlocking {
                    extra.accountInstance.exploreOptionsDataStore.data.first()
                }

                val developers = extra.accountInstance.trendingApi.listTrendingDevelopers(
                    since = options.timeSpan.urlParamValue,
                    language = options.exploreLanguage.urlParam,
                    spokenLanguage = options.exploreSpokenLanguage.urlParam
                ).map {
                    it.dbModel
                }

                val repositories = extra.accountInstance.trendingApi.listTrendingRepositories(
                    since = options.timeSpan.urlParamValue,
                    language = options.exploreLanguage.urlParam,
                    spokenLanguage = options.exploreSpokenLanguage.urlParam
                ).map {
                    it.dbModel
                }

                extra.accountInstance.database.trendingDevelopersDao().deleteAll()
                extra.accountInstance.database.trendingDevelopersDao()
                    .insert(developers = developers)

                extra.accountInstance.database.trendingRepositoriesDao().deleteAll()
                extra.accountInstance.database.trendingRepositoriesDao()
                    .insert(repositories = repositories)

                _refreshDataStatus.postValue(Resource.success(data = null))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _refreshDataStatus.postValue(Resource.error(exception = e, data = null))
            }
        }
    }

    fun updateExploreOptions(timeSpan: ExploreTimeSpan) {
        if (timeSpan == options.value?.timeSpan) {
            return
        }

        viewModelScope.launch {
            try {
                extra.accountInstance.exploreOptionsDataStore.updateData {
                    ExploreOptions(
                        timeSpan = timeSpan,
                        exploreLanguage = options.value?.exploreLanguage
                            ?: ExploreOptionsSerializer.defaultValue.exploreLanguage,
                        exploreSpokenLanguage = options.value?.exploreSpokenLanguage
                            ?: ExploreOptionsSerializer.defaultValue.exploreSpokenLanguage
                    )
                }

                refreshTrendingData()
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}