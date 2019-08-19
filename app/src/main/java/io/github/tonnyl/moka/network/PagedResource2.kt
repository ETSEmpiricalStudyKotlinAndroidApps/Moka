package io.github.tonnyl.moka.network

data class PagedResource2<out T>(
    val direction: PagedResourceDirection,
    val resource: Resource<T>? = null
)