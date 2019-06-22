package io.github.tonnyl.moka.network.service

import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingLanguages
import io.github.tonnyl.moka.data.TrendingRepository
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface TrendingService {

    /**
     * List all trending repositories.
     *
     * @param language optional, list trending repositories of certain programming languages.
     * @param since optional, default to daily, possible values: daily, weekly and monthly.
     * @param url url endpoint. DO NOT change it if not necessary.
     *
     * @return If successful, return a list of [TrendingRepository].
     */
    @GET
    suspend fun listTrendingRepositories(
        @Url url: String = "https://github-trending-api.now.sh/repositories",
        @Query("language") language: String,
        @Query("since") since: String
    ): Response<List<TrendingRepository>>

    /**
     * List all trending developers.
     *
     * @param language optional, list trending repositories of certain programming languages.
     * @param since optional, default to daily, possible values: daily, weekly and monthly.
     * @param url url endpoint. DO NOT change it if not necessary.
     *
     * @return If successful, return a list of [TrendingDeveloper].
     */
    @GET
    suspend fun listTrendingDevelopers(
        @Url url: String = "https://github-trending-api.now.sh/developers",
        @Query("language") language: String,
        @Query("since") since: String
    ): Response<List<TrendingDeveloper>>

    /**
     * List popular languages and all languages.
     *
     * @param url Url endpoint. DO NOT change it if not necessary.
     *
     * @return If successful, return a list of [TrendingLanguages].
     */
    @GET
    suspend fun listLanguages(
        @Url url: String = "https://github-trending-api.now.sh/languages"
    ): Response<List<TrendingLanguages>>

}