package io.github.tonnyl.moka.network

import com.apollographql.apollo.api.Response

class NonErrorResponse<T>(
    private val response: Response<T>
) {

    fun data(): T? = response.data()

}