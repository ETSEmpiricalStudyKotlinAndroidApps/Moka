package io.github.tonnyl.moka.network

data class PagedResource<out T>(
    val direction: PagedResourceDirection,
    val resource: Resource<T>? = null
)