package io.tonnyl.moka.common.api

import io.tonnyl.moka.common.data.GitHubStatus
import io.tonnyl.moka.common.network.api.GitHubStatusApi

@Suppress("unused")
class GitHubStatusApiWrapper(private val api: GitHubStatusApi) {

    suspend fun getSummary(): Result<GitHubStatus> {
        return try {
            return Result.success(api.getSummary())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}