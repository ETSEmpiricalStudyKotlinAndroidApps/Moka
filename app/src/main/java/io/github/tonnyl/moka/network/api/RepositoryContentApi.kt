package io.github.tonnyl.moka.network.api

import io.github.tonnyl.moka.data.RepositoryReadmeResponse
import io.github.tonnyl.moka.network.KtorClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class RepositoryContentApi(private val ktorClient: HttpClient) {

    suspend fun getReadme(
        owner: String,
        repo: String,
        ref: String
    ): RepositoryReadmeResponse {
        return ktorClient.get(
            urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/${owner}/${repo}/readme?ref=${ref}"
        ) {
            header(HttpHeaders.Accept, "application/vnd.github.v3+json")
        }
    }

}