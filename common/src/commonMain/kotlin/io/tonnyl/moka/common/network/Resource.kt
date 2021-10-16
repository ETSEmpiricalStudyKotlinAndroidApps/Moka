package io.tonnyl.moka.common.network

data class Resource<out T>(

    val status: Status,

    val data: T?,

    val e: Exception?

) {

    companion object {

        fun <T> success(data: T?): Resource<T> = Resource(Status.SUCCESS, data, null)

        fun <T> error(exception: Exception?, data: T?): Resource<T> = Resource(Status.ERROR, data, exception)

        fun <T> loading(data: T?): Resource<T> = Resource(Status.LOADING, data, null)

    }

}