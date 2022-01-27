package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.CreationExtras
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.db.data.dbModel
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreTimeSpan
import io.tonnyl.moka.common.store.data.urlParamValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
data class ExploreViewModelExtra(
    val accountInstance: AccountInstance
)

@ExperimentalSerializationApi
class ExploreViewModel(private val extra: ExploreViewModelExtra) : ViewModel() {

    private val _queryData =
        extra.accountInstance.exploreOptionsDataStore.data.asLiveData(context = viewModelScope.coroutineContext)
    val queryData: LiveData<ExploreOptions>
        get() = _queryData

    private val _refreshDataStatus = MutableLiveData<Resource<Boolean>>()
    val refreshDataStatus: LiveData<Resource<Boolean>>
        get() = _refreshDataStatus

    val repositoriesLocalData =
        extra.accountInstance.database.trendingRepositoriesDao().trendingRepositories()
    val developersLocalData =
        extra.accountInstance.database.trendingDevelopersDao().trendingDevelopers()

    init {
        refreshTrendingData()
    }

    fun refreshTrendingData() {
        val queryDataValue = queryData.value ?: ExploreOptionsSerializer.defaultValue

        val countTrendingData: () -> Boolean = {
            extra.accountInstance.database.trendingDevelopersDao()
                .trendingDevelopersCount() == 0
                    && extra.accountInstance.database.trendingRepositoriesDao()
                .trendingRepositoriesCount() == 0
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _refreshDataStatus.postValue(Resource.loading(data = countTrendingData.invoke()))

                val developers = extra.accountInstance.trendingApi.listTrendingDevelopers(
                    since = queryDataValue.timeSpan.urlParamValue,
                    language = queryDataValue.exploreLanguage.urlParam
                ).map {
                    it.dbModel
                }

                val repositories = extra.accountInstance.trendingApi.listTrendingRepositories(
                    since = queryDataValue.timeSpan.urlParamValue,
                    language = queryDataValue.exploreLanguage.urlParam
                ).map {
                    it.dbModel
                }

                if (!developers.isNullOrEmpty()) {
                    extra.accountInstance.database.trendingDevelopersDao().deleteAll()
                    extra.accountInstance.database.trendingDevelopersDao()
                        .insert(developers = developers)
                }

                if (!repositories.isNullOrEmpty()) {
                    extra.accountInstance.database.trendingRepositoriesDao().deleteAll()
                    extra.accountInstance.database.trendingRepositoriesDao()
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
                extra.accountInstance.exploreOptionsDataStore.updateData {
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

    companion object {

        private object ExploreViewModelExtraKeyImpl : CreationExtras.Key<ExploreViewModelExtra>

        val EXPLORE_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<ExploreViewModelExtra> =
            ExploreViewModelExtraKeyImpl

    }

}