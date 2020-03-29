package io.github.tonnyl.moka.ui.explore.repositories

import io.github.tonnyl.moka.data.TrendingRepository

sealed class TrendingRepositoryItemEvent {

    data class ViewRepository(val repository: TrendingRepository) : TrendingRepositoryItemEvent()

    data class ViewProfile(val repository: TrendingRepository) : TrendingRepositoryItemEvent()

}