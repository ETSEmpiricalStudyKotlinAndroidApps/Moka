package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.tonnyl.moka.common.data.GitHubStatus

class GitHubStatusApi(private val ktorClient: HttpClient) {

    suspend fun getSummary(): GitHubStatus {
        return ktorClient.get(urlString = "https://www.githubstatus.com/api/v2/summary.json")
    }

}