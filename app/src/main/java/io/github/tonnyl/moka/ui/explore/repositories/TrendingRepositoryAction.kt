package io.github.tonnyl.moka.ui.explore.repositories

import io.github.tonnyl.moka.data.TrendingRepository

interface TrendingRepositoryAction {

    fun openRepository(repository: TrendingRepository)

}