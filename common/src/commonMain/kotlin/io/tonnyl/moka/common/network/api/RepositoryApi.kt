package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.tonnyl.moka.common.network.KtorClient

class RepositoryApi(private val ktorClient: HttpClient) {

    /**
     * Create a fork for the authenticated user.
     */
    suspend fun createAFork(
        owner: String,
        repo: String
    ) {
        return ktorClient.post(
            urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/$owner/$repo/forks"
        ) {
            header(HttpHeaders.Accept, "application/vnd.github.v3+json")
        }
    }

}