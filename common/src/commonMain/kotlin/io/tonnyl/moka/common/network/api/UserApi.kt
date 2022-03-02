package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.tonnyl.moka.common.data.Account
import io.tonnyl.moka.common.network.KtorClient.Companion.GITHUB_V1_BASE_URL

class UserApi(private val ktorClient: HttpClient) {

    suspend fun getAuthenticatedUser(accessToken: String): Account {
        return ktorClient.get(urlString = "$GITHUB_V1_BASE_URL/user") {
            header("Authorization", "Bearer $accessToken")
        }
    }

    /**
     * @param params keys: name, email, blog, company, location, bio, twitter_username
     */
    suspend fun updateUseInformation(params: Map<String, String?>) {
        ktorClient.patch<Unit>(urlString = "$GITHUB_V1_BASE_URL/user") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            body = params
        }
    }

}