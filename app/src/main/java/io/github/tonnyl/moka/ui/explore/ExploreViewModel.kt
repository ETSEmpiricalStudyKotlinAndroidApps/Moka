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
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.TrendingService
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset

class ExploreViewModel : ViewModel() {

    /**
     * Triple: first -> time span, second -> language param, third -> language name.
     * Pay attention to its order, DO NOT make a mistake.
     */
    val queryData = MutableLiveData<Triple<ExploreTimeSpanType, String, String>>()

    private val _languages = MutableLiveData<List<LocalLanguage>>()
    val languages: LiveData<List<LocalLanguage>>
        get() = _languages

    private val _trendingRepositories = MutableLiveData<Resource<List<TrendingRepository>>>()
    val trendingRepositories: LiveData<Resource<List<TrendingRepository>>>
        get() = _trendingRepositories

    private val _trendingDevelopers = MutableLiveData<Resource<List<TrendingDeveloper>>>()
    val trendingDevelopers: LiveData<Resource<List<TrendingDeveloper>>>
        get() = _trendingDevelopers

    init {
        // todo store/restore value from sp.
        queryData.value = Triple(ExploreTimeSpanType.DAILY, "all", "All language")

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
                Gson().fromJson<List<LocalLanguage>>(json, object : TypeToken<List<LocalLanguage>>() {}.type)
            }
            _languages.value = languagesResult
        }
    }

    @MainThread
    fun refreshTrendingDevelopers() {
        val queryDataValue = queryData.value ?: return

        _trendingDevelopers.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.createService(TrendingService::class.java)
                        .listTrendingDevelopers(language = queryDataValue.first.value, since = queryDataValue.second)
                }

                _trendingDevelopers.value = Resource.success(response.body())
            } catch (e: Exception) {
                Timber.e(e)

                _trendingDevelopers.value = Resource.error(e.message, null)
            }
        }
    }

    @MainThread
    fun refreshTrendingRepositories() {
        val queryDataValue = queryData.value ?: return

        _trendingRepositories.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.createService(TrendingService::class.java)
                        .listTrendingRepositories(language = queryDataValue.first.value, since = queryDataValue.second)
                }

                _trendingRepositories.value = Resource.success(response.body())
            } catch (e: Exception) {
                Timber.e(e)

                _trendingRepositories.value = Resource.error(e.message, null)
            }
        }
    }

}