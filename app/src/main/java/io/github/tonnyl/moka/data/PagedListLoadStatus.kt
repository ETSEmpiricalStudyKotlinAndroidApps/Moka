package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.net.Resource

data class PagedResource<out T>(

        val initial: Resource<T>? = null,

        val before: Resource<T>? = null,

        val after: Resource<T>? = null

)
