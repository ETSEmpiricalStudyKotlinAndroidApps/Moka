package io.github.tonnyl.moka.ui.explore.developers

import io.github.tonnyl.moka.data.TrendingDeveloper

interface TrendingDeveloperAction {

    fun openProfile(developer: TrendingDeveloper)

    fun openRepository(developer: TrendingDeveloper)

}