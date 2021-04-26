package io.github.tonnyl.moka.network.api

import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingRepository
import io.ktor.client.*
import io.ktor.client.request.*

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
    ): List<TrendingRepository> {
        return ktorClient.get(
            urlString = "https://github-trending-api.now.sh/repositories?language=${language}&since=${since}"
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
    ): List<TrendingDeveloper> {
        return ktorClient.get(
            urlString = "https://github-trending-api.now.sh/developers?language=${language}&since=${since}"
        )
    }

}