package io.github.tonnyl.moka.ui.explore.developers

import io.github.tonnyl.moka.data.TrendingDeveloper

interface TrendingDeveloperAction {

    fun viewProfile(developer: TrendingDeveloper)

    fun viewRepository(developer: TrendingDeveloper)

}