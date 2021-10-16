package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class TrendingApi(private val ktorClient: HttpClient) {

    /**
     * List all trending repositories.
     *
     * @param language optional, list trending repositories of certain programming languages.
     * @param since optional, default to daily, possible values: daily, weekly and monthly.
     *
     * @return If successful, return a list of [TrendingRepository].
     */
    suspend fun listTrendingRepositories(
        language: String?,
        since: String
    ): HttpResponse {
        return ktorClient.get(
            urlString = "https://gtrending.yapie.me/repositories?language=${language.orEmpty()}&since=${since}"
        )
    }

    /**
     * List all trending developers.
     *
     * @param language optional, list trending repositories of certain programming languages.
     * @param since optional, default to daily, possible values: daily, weekly and monthly.
     *
     * @return If successful, return a list of [TrendingDeveloper].
     */
    suspend fun listTrendingDevelopers(
        language: String?,
        since: String
    ): HttpResponse {
        return ktorClient.get(
            urlString = "https://gtrending.yapie.me/developers?language=${language.orEmpty()}&since=${since}"
        )
    }

}