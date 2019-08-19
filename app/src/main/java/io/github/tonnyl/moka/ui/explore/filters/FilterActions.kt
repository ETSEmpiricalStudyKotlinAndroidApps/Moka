package io.github.tonnyl.moka.ui.explore.filters

interface FilterActions {

    fun actionSelect(language: LocalLanguage)

    fun actionSelectDone()

    fun timeSpanSelect(id: Int)

}