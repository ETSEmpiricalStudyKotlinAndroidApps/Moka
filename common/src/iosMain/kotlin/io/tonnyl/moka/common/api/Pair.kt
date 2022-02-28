package io.tonnyl.moka.common.api

data class Pair<out A, out B>(
    val first: A,
    val second: B
)