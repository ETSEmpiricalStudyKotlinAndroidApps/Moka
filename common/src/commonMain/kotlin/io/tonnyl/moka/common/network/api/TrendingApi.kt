package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.tonnyl.moka.common.data.TrendingDeveloper
import io.tonnyl.moka.common.data.TrendingRepository

class TrendingApi(private val ktorClient: HttpClient) {

    /**
     * List all trending repositories.
     *
     * @param language list trending repositories of certain programming languages.
     * @param since default to daily, possible values: daily, weekly and monthly.
     *
     * @return If successful, return a list of [TrendingRepository].
     */
    suspend fun listTrendingRepositories(
        language: String,
        spokenLanguage: String,
        since: String
    ): List<TrendingRepository> {
        return ktorClient.get(
            urlString = "https://gtrending.yapie.me/repositories?language=${language}&since=${since}&spoken_language_code=${spokenLanguage}"
        )
    }

    /**
     * List all trending developers.
     *
     * @param language list trending repositories of certain programming languages.
     * @param since default to daily, possible values: daily, weekly and monthly.
     *
     * @return If successful, return a list of [TrendingDeveloper].
     */
    suspend fun listTrendingDevelopers(
        language: String,
        spokenLanguage: String,
        since: String
    ): List<TrendingDeveloper> {
        return ktorClient.get(
            urlString = "https://gtrending.yapie.me/developers?language=${language}&since=${since}&spoken_language_code=${spokenLanguage}"
        )
    }

}