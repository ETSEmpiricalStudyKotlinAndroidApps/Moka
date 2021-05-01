package io.github.tonnyl.moka.ui.explore

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

@ExperimentalSerializationApi
class ExploreViewModel(
    private val accountInstance: AccountInstance
) : ViewModel() {

    private val _queryData = MutableLiveData<Pair<ExploreTimeSpanType, LocalLanguage?>>()
    val queryData: LiveData<Pair<ExploreTimeSpanType, LocalLanguage?>>
        get() = _queryData

    private val _repositoriesRemoteStatus = MutableLiveData<Resource<List<TrendingRepository>>>()
    val repositoriesRemoteStatus: LiveData<Resource<List<TrendingRepository>>>
        get() = _repositoriesRemoteStatus

    private val _developersRemoteStatus = MutableLiveData<Resource<List<TrendingDeveloper>>>()
    val developersRemoteStatus: LiveData<Resource<List<TrendingDeveloper>>>
        get() = _developersRemoteStatus

    val repositoriesLocalData =
        accountInstance.database.trendingRepositoriesDao().trendingRepositories()
    val developersLocalData = accountInstance.database.trendingDevelopersDao().trendingDevelopers()

    init {
        // todo store/restore value from sp.
        _queryData.value = Pair(
            ExploreTimeSpanType.DAILY,
            LocalLanguage(null, "All Languages", "#ECECEC")
        )

        refreshTrendingDevelopers()
        refreshTrendingRepositories()
    }

    @MainThread
    fun refreshTrendingDevelopers() {
        val queryDataValue = queryData.value ?: return

        _developersRemoteStatus.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    accountInstance.trendingApi.listTrendingDevelopers(
                        since = queryDataValue.first.value,
                        language = queryDataValue.second?.urlParam
                    )
                }

                withContext(Dispatchers.IO) {
                    if (!response.isNullOrEmpty()) {
                        accountInstance.database.trendingDevelopersDao().deleteAll()
                        accountInstance.database.trendingDevelopersDao().insert(response)
                    }
                }

                _developersRemoteStatus.value = Resource.success(response)
            } catch (e: Exception) {
                Timber.e(e)

                _developersRemoteStatus.value = Resource.error(e.message, null)
            }
        }
    }

    @MainThread
    fun refreshTrendingRepositories() {
        val queryDataValue = queryData.value ?: return

        _repositoriesRemoteStatus.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    accountInstance.trendingApi.listTrendingRepositories(
                        since = queryDataValue.first.value,
                        language = queryDataValue.second?.urlParam
                    )
                }

                withContext(Dispatchers.IO) {
                    if (!response.isNullOrEmpty()) {
                        accountInstance.database.trendingRepositoriesDao().deleteAll()
                        accountInstance.database.trendingRepositoriesDao().insert(response)
                    }
                }

                _repositoriesRemoteStatus.value = Resource.success(response)
            } catch (e: Exception) {
                Timber.e(e)

                _repositoriesRemoteStatus.value = Resource.error(e.message, null)
            }
        }
    }

}