package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.tonnyl.moka.common.data.RepositoryReadmeResponse
import io.tonnyl.moka.common.network.KtorClient

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

    suspend fun getFile(url: String): String {
        return ktorClient.get(
            urlString = url
        ) {
            header(HttpHeaders.Accept, "application/vnd.github.VERSION.raw")
        }
    }

    suspend fun getRedirectedUrl(url: String): String {
        val resp = ktorClient.get<HttpResponse>(urlString = url) {
            header(HttpHeaders.Accept, "application/vnd.github.v3+json")
        }
        return resp.request.url.toString()
    }

}