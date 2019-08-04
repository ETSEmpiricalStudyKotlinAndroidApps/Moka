package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.db.dao.TrendingDeveloperDao
import io.github.tonnyl.moka.db.dao.TrendingRepositoryDao

class ViewModelFactory(
    private val localDevelopersSource: TrendingDeveloperDao,
    private val localRepositoriesSource: TrendingRepositoryDao
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ExploreViewModel(localDevelopersSource, localRepositoriesSource) as T
    }

}