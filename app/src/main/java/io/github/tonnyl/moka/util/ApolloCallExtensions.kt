package io.github.tonnyl.moka.util

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> ApolloCall<T>.execute() = suspendCoroutine<Response<T>> { continuation ->
    enqueue(object : ApolloCall.Callback<T>() {

        override fun onResponse(response: Response<T>) {
            continuation.resume(response)
        }

        override fun onFailure(e: ApolloException) {
            continuation.resumeWithException(e)
        }

    })
}