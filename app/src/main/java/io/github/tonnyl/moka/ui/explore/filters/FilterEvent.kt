package io.github.tonnyl.moka.ui.explore.filters

import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType

sealed class FilterEvent {

    object ShowFilters : FilterEvent()

    data class SelectLanguage(val lang: LocalLanguage) : FilterEvent()

    object ConfirmSelection : FilterEvent()

    data class SelectTimeSpan(val type: ExploreTimeSpanType) : FilterEvent()

}