package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.ui.explore.filters.LanguagesLiveData
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import retrofit2.Response
import java.io.InputStream

class ExploreViewModel : ViewModel() {

    /**
     * Triple: first -> time span, second -> language param, third -> language name.
     * Pay attention to its order, DO NOT make a mistake.
     */
    val queryData = MutableLiveData<Triple<TrendingTimeSpanType, String, String>>()

    val trendingRepositories: LiveData<Response<List<TrendingRepository>>> = Transformations.map(TrendingRepositoryLiveData()) { it }

    val trendingDevelopers: LiveData<Response<List<TrendingDeveloper>>> = Transformations.map(TrendingDeveloperLiveData()) { it }

    init {
        // todo store/restore value from sp.
        queryData.value = Triple(TrendingTimeSpanType.DAILY, "all", "All language")
    }

    fun languages(inputStream: InputStream): LiveData<List<LocalLanguage>> = Transformations.map(LanguagesLiveData(inputStream)) { it }

}