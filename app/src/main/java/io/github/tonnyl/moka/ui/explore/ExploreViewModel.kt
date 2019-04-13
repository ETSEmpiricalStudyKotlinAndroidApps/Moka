package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.net.Resource
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperLiveData
import io.github.tonnyl.moka.ui.explore.filters.LanguagesLiveData
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import java.io.InputStream

class ExploreViewModel : ViewModel() {

    /**
     * Triple: first -> time span, second -> language param, third -> language name.
     * Pay attention to its order, DO NOT make a mistake.
     */
    val queryData = MutableLiveData<Triple<ExploreTimeSpanType, String, String>>()

    val trendingRepositories: LiveData<Resource<List<TrendingRepository>>> = Transformations.map(TrendingRepositoryLiveData()) { it }

    val trendingDevelopers: LiveData<Resource<List<TrendingDeveloper>>> = Transformations.map(TrendingDeveloperLiveData()) { it }

    init {
        // todo store/restore value from sp.
        queryData.value = Triple(ExploreTimeSpanType.DAILY, "all", "All language")
    }

    fun languages(inputStream: InputStream): LiveData<List<LocalLanguage>> = Transformations.map(LanguagesLiveData(inputStream)) { it }

}