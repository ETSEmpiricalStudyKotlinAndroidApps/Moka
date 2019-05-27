package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperLiveData
import io.github.tonnyl.moka.ui.explore.filters.LanguagesLiveData
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoryLiveData
import java.io.InputStream

class ExploreViewModel : ViewModel() {

    /**
     * Triple: first -> time span, second -> language param, third -> language name.
     * Pay attention to its order, DO NOT make a mistake.
     */
    val queryData = MutableLiveData<Triple<ExploreTimeSpanType, String, String>>()

    val trendingRepositories = TrendingRepositoryLiveData()

    val trendingDevelopers = TrendingDeveloperLiveData()

    init {
        // todo store/restore value from sp.
        queryData.value = Triple(ExploreTimeSpanType.DAILY, "all", "All language")
    }

    fun languages(inputStream: InputStream): LiveData<List<LocalLanguage>> = Transformations.map(LanguagesLiveData(inputStream)) { it }

}