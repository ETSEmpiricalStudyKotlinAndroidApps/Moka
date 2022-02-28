package io.tonnyl.moka.common.api

import io.tonnyl.moka.common.data.TrendingDeveloper
import io.tonnyl.moka.common.data.TrendingRepository
import io.tonnyl.moka.common.network.api.TrendingApi

@Suppress("unused")
class TrendingApiWrapper(private val api: TrendingApi) {

    suspend fun listTrendingRepositories(
        language: String,
        spokenLanguage: String,
        since: String
    ): Result<List<TrendingRepository>> {
        return try {
            Result.success(
                api.listTrendingRepositories(
                    language = language,
                    spokenLanguage = spokenLanguage,
                    since = since
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun listTrendingDevelopers(
        language: String,
        spokenLanguage: String,
        since: String
    ): Result<List<TrendingDeveloper>> {
        return try {
            Result.success(
                api.listTrendingDevelopers(
                    language = language,
                    spokenLanguage = spokenLanguage,
                    since = since
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}