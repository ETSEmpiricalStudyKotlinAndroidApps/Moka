package io.tonnyl.moka.common.api

sealed interface Result<T> {

    data class Success<T>(val value: T) : Result<T>

    data class Failure<T>(val error: Exception) : Result<T>

    companion object {

        fun <T> success(value: T): Success<T> {
            return Success(value = value)
        }

        fun <T> failure(error: Exception): Failure<T> {
            return Failure(error = error)
        }

    }

}