package io.github.tonnyl.moka.network.api

import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.KtorClient.Companion.GITHUB_V1_BASE_URL
import io.ktor.client.*
import io.ktor.client.request.*

class UserApi(private val ktorClient: HttpClient) {

    suspend fun getAuthenticatedUser(accessToken: String): AuthenticatedUser {
        return ktorClient.get(urlString = "$GITHUB_V1_BASE_URL/user") {
            header("Authorization", "Bearer $accessToken")
        }
    }

    /**
     * @param params keys: name, email, blog, company, location, bio, twitter_username
     */
    suspend fun updateUseInformation(params: Map<String, String?>) {
        ktorClient.patch<Unit>(urlString = "$GITHUB_V1_BASE_URL/user") {
            body = params
        }
    }

}