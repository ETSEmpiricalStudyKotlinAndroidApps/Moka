package io.github.tonnyl.moka.network

data class PagedResource<out T>(

        val initial: Resource<T>? = null,

        val before: Resource<T>? = null,

        val after: Resource<T>? = null

)
