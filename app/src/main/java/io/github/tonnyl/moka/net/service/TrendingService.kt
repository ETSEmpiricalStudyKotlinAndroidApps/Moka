package io.github.tonnyl.moka.net.service

import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingLanguages
import io.github.tonnyl.moka.data.TrendingRepository
import retrofit2.Call
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
    fun listTrendingRepositories(
            @Url url: String = "https://github-trending-api.now.sh/repositories",
            @Query("language") language: String,
            @Query("since") since: String
    ): Call<List<TrendingRepository>>

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
    fun listTrendingDevelopers(
            @Url url: String = "https://github-trending-api.now.sh/developers",
            @Query("language") language: String,
            @Query("since") since: String
    ): Call<List<TrendingDeveloper>>

    /**
     * List popular languages and all languages.
     *
     * @param url Url endpoint. DO NOT change it if not necessary.
     *
     * @return If successful, return a list of [TrendingLanguages].
     */
    @GET
    fun listLanguages(
            @Url url: String = "https://github-trending-api.now.sh/languages"
    ): Call<List<TrendingLanguages>>

}