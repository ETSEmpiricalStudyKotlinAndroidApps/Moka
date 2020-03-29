package io.github.tonnyl.moka.ui.explore.developers

import io.github.tonnyl.moka.data.TrendingDeveloper

sealed class TrendingDeveloperItemEvent {

    data class ViewProfile(val developer: TrendingDeveloper) : TrendingDeveloperItemEvent()

    data class ViewRepository(val developer: TrendingDeveloper) : TrendingDeveloperItemEvent()

}