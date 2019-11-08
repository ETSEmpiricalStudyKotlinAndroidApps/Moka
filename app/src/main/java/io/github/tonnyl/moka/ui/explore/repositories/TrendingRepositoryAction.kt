package io.github.tonnyl.moka.ui.explore.repositories

import io.github.tonnyl.moka.data.TrendingRepository

interface TrendingRepositoryAction {

    fun viewRepository(repository: TrendingRepository)

    fun viewProfile(repository: TrendingRepository)

}