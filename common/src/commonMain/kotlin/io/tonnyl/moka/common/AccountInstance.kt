package io.tonnyl.moka.common

import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.network.api.TrendingApi

class AccountInstance {

    private val unauthenticatedKtorClient = KtorClient(
        requireAuth = false,
        accessToken = null
    ).httpClient

    private val authenticatedKtorClient = KtorClient(
        requireAuth = true,
        accessToken = ""
    ).httpClient

    val trendingApi = TrendingApi(ktorClient = unauthenticatedKtorClient)

}