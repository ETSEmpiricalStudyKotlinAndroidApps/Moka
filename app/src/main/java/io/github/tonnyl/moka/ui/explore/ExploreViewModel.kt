package io.github.tonnyl.moka.ui.explore

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.db.dao.TrendingDeveloperDao
import io.github.tonnyl.moka.db.dao.TrendingRepositoryDao
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.TrendingService
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperItemEvent
import io.github.tonnyl.moka.ui.explore.filters.FilterEvent
import io.github.tonnyl.moka.ui.explore.filters.FilterEvent.*
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoryItemEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset

class ExploreViewModel(
    private val localDevelopersSource: TrendingDeveloperDao,
    private val localRepositoriesSource: TrendingRepositoryDao
) : ViewModel() {

    private val _queryData = MutableLiveData<Pair<ExploreTimeSpanType, LocalLanguage?>>()
    val queryData: LiveData<Pair<ExploreTimeSpanType, LocalLanguage?>>
        get() = _queryData

    private val _languages = MutableLiveData<List<LocalLanguage>>()
    val languages: LiveData<List<LocalLanguage>>
        get() = _languages

    private val _repositoriesRemoteStatus = MutableLiveData<Resource<List<TrendingRepository>>>()
    val repositoriesRemoteStatus: LiveData<Resource<List<TrendingRepository>>>
        get() = _repositoriesRemoteStatus

    private val _developersRemoteStatus = MutableLiveData<Resource<List<TrendingDeveloper>>>()
    val developersRemoteStatus: LiveData<Resource<List<TrendingDeveloper>>>
        get() = _developersRemoteStatus

    val repositoriesLocalData = localRepositoriesSource.trendingRepositories()
    val developersLocalData = localDevelopersSource.trendingDevelopers()

    private val _developerEvent = MutableLiveData<Event<TrendingDeveloperItemEvent>>()
    val developerEvent: LiveData<Event<TrendingDeveloperItemEvent>>
        get() = _developerEvent

    private val _repositoryEvent = MutableLiveData<Event<TrendingRepositoryItemEvent>>()
    val repositoryEvent: LiveData<Event<TrendingRepositoryItemEvent>>
        get() = _repositoryEvent

    private val _filterEvent = MutableLiveData<Event<FilterEvent>>()
    val filterEvent: LiveData<Event<FilterEvent>>
        get() = _filterEvent

    init {
        // todo store/restore value from sp.
        _queryData.value = Pair(
            ExploreTimeSpanType.DAILY,
            LocalLanguage(null, "All Languages", "#ECECEC")
        )

        refreshTrendingDevelopers()
        refreshTrendingRepositories()
    }

    fun loadLanguagesData(inputStream: InputStream) {
        viewModelScope.launch {
            val languagesResult = withContext(Dispatchers.IO) {
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val json = String(buffer, Charset.forName("UTF-8"))
                Gson().fromJson<List<LocalLanguage>>(
                    json,
                    object : TypeToken<List<LocalLanguage>>() {}.type
                )
            }
            _languages.value = languagesResult
        }
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

    @MainThread
    fun viewProfile(repository: TrendingRepository) {

    }

    @MainThread
    fun viewProfile(developer: TrendingDeveloper) {

    }

    @MainThread
    fun viewRepository(repository: TrendingRepository) {

    }

    @MainThread
    fun viewRepository(developer: TrendingDeveloper) {

    }

    @MainThread
    fun selectLanguage(lang: LocalLanguage) {
        _queryData.value = _queryData.value?.copy(second = lang)

        _filterEvent.value = Event(SelectLanguage(lang))
    }

    @MainThread
    fun confirmSelection() {
        _filterEvent.value = Event(ConfirmSelection)
    }

    @MainThread
    fun selectTimeSpan(type: ExploreTimeSpanType) {
        _queryData.value = _queryData.value?.copy(first = type)

        _filterEvent.value = Event(SelectTimeSpan(type))
    }

    @MainThread
    fun showFilters() {
        _filterEvent.value = Event(ShowFilters)
    }

}