package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperLiveData
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoryLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.charset.Charset

class ExploreViewModel : ViewModel() {

    /**
     * Triple: first -> time span, second -> language param, third -> language name.
     * Pay attention to its order, DO NOT make a mistake.
     */
    val queryData = MutableLiveData<Triple<ExploreTimeSpanType, String, String>>()

    val trendingRepositories = TrendingRepositoryLiveData()

    val trendingDevelopers = TrendingDeveloperLiveData()

    private val _languages = MutableLiveData<List<LocalLanguage>>()
    val languages: LiveData<List<LocalLanguage>>
        get() = _languages

    init {
        // todo store/restore value from sp.
        queryData.value = Triple(ExploreTimeSpanType.DAILY, "all", "All language")
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

}