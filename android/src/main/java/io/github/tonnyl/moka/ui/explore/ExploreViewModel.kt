package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.*
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.serializers.store.ExploreOptionsSerializer
import io.github.tonnyl.moka.serializers.store.data.ExploreLanguage
import io.github.tonnyl.moka.serializers.store.data.ExploreOptions
import io.github.tonnyl.moka.serializers.store.data.ExploreTimeSpan
import io.github.tonnyl.moka.serializers.store.data.urlParamValue
import io.tonnyl.moka.common.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
class ExploreViewModel(
    private val accountInstance: AccountInstance
) : ViewModel() {

    private val _queryData =
        accountInstance.exploreOptionsDataStore.data.asLiveData(context = viewModelScope.coroutineContext)
    val queryData: LiveData<ExploreOptions>
        get() = _queryData

    private val _refreshDataStatus = MutableLiveData<Resource<Boolean>>()
    val refreshDataStatus: LiveData<Resource<Boolean>>
        get() = _refreshDataStatus

    val repositoriesLocalData =
        accountInstance.database.trendingRepositoriesDao().trendingRepositories()
    val developersLocalData = accountInstance.database.trendingDevelopersDao().trendingDevelopers()

    init {
        refreshTrendingData()
    }

    fun refreshTrendingData() {
        val queryDataValue = queryData.value ?: ExploreOptionsSerializer.defaultValue

        val countTrendingData: () -> Boolean = {
            accountInstance.database.trendingDevelopersDao()
                .trendingDevelopersCount() == 0
                    && accountInstance.database.trendingRepositoriesDao()
                .trendingRepositoriesCount() == 0
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _refreshDataStatus.postValue(Resource.loading(data = countTrendingData.invoke()))

                val developers = accountInstance.trendingApi.listTrendingDevelopers(
                    since = queryDataValue.timeSpan.urlParamValue,
                    language = queryDataValue.exploreLanguage.urlParam
                )

                val repositories = accountInstance.trendingApi.listTrendingRepositories(
                    since = queryDataValue.timeSpan.urlParamValue,
                    language = queryDataValue.exploreLanguage.urlParam
                )

                if (!developers.isNullOrEmpty()) {
                    accountInstance.database.trendingDevelopersDao().deleteAll()
                    accountInstance.database.trendingDevelopersDao().insert(developers = developers)
                }

                if (!repositories.isNullOrEmpty()) {
                    accountInstance.database.trendingRepositoriesDao().deleteAll()
                    accountInstance.database.trendingRepositoriesDao()
                        .insert(repositories = repositories)
                }

                _refreshDataStatus.postValue(Resource.success(data = countTrendingData.invoke()))
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _refreshDataStatus.postValue(Resource.error(e, null))
            }
        }
    }

    fun updateExploreOptions(
        exploreLanguage: ExploreLanguage,
        timeSpan: ExploreTimeSpan
    ) {
        if (exploreLanguage == queryData.value?.exploreLanguage
            && timeSpan == queryData.value?.timeSpan
        ) {
            return
        }

        viewModelScope.launch {
            try {
                accountInstance.exploreOptionsDataStore.updateData {
                    ExploreOptions(
                        timeSpan = timeSpan,
                        exploreLanguage = exploreLanguage
                    )
                }

                refreshTrendingData()
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }
            }
        }
    }

}