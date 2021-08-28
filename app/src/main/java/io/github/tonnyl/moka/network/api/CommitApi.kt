package io.github.tonnyl.moka.network.api

import io.github.tonnyl.moka.network.KtorClient
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class CommitApi(private val ktorClient: HttpClient) {

    /**
     * Returns the contents of a single commit reference.
     * You must have read access for the repository to use this endpoint.
     */
    suspend fun getACommit(
        owner: String,
        repo: String,
        ref: String,
        page: Int,
        perPage: Int
    ): HttpResponse {
        return ktorClient.get(
            urlString = "${KtorClient.GITHUB_V1_BASE_URL}/repos/${owner}/${repo}/commits/${ref}?page=${page}&per_page=${perPage}"
        )
    }

    suspend fun getACommitByUrl(url: String): HttpResponse {
        return ktorClient.get(url)
    }

}