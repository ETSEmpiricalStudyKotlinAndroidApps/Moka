package io.github.tonnyl.moka.ui.explore

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.db.dao.TrendingDeveloperDao
import io.github.tonnyl.moka.db.dao.TrendingRepositoryDao
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.TrendingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class ExploreViewModel(
    private val localDevelopersSource: TrendingDeveloperDao,
    private val localRepositoriesSource: TrendingRepositoryDao
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

    val repositoriesLocalData = localRepositoriesSource.trendingRepositories()
    val developersLocalData = localDevelopersSource.trendingDevelopers()

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
                    RetrofitClient.createService(TrendingService::class.java)
                        .listTrendingDevelopers(
                            since = queryDataValue.first.value,
                            language = queryDataValue.second?.urlParam
                        )
                }

                val list = response.body()

                withContext(Dispatchers.IO) {
                    if (!list.isNullOrEmpty()) {
                        localDevelopersSource.deleteAll()
                        localDevelopersSource.insert(list)
                    }
                }

                _developersRemoteStatus.value = Resource.success(list)
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
                    RetrofitClient.createService(TrendingService::class.java)
                        .listTrendingRepositories(
                            since = queryDataValue.first.value,
                            language = queryDataValue.second?.urlParam
                        )
                }

                val list = response.body()

                withContext(Dispatchers.IO) {
                    if (!list.isNullOrEmpty()) {
                        localRepositoriesSource.deleteAll()
                        localRepositoriesSource.insert(list)
                    }
                }

                _repositoriesRemoteStatus.value = Resource.success(list)
            } catch (e: Exception) {
                Timber.e(e)

                _repositoriesRemoteStatus.value = Resource.error(e.message, null)
            }
        }
    }

}