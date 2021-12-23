package io.tonnyl.moka.common.network.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.tonnyl.moka.common.data.AccessToken
import io.tonnyl.moka.common.network.KtorClient

class AccessTokenApi(private val ktorClient: HttpClient) {

    /**
     * Exchange code for an access token.
     *
     * @param clientId Required. The client ID you received from GitHub for your GitHub App.
     * @param clientSecret Required. The client secret you received from GitHub for your GitHub App.
     * @param code Required. The code you received.
     * @param redirectUrl The URL in your application where users are sent after authorization.
     * @param state The unguessable random string.
     */
    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        redirectUrl: String,
        state: String
    ): AccessToken {
        return ktorClient.post(urlString = KtorClient.GITHUB_GET_ACCESS_TOKEN_URL) {
            header(HttpHeaders.Accept, "application/json")
            header(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
            setBody(
                Parameters.build {
                    append("client_id", clientId)
                    append("client_secret", clientSecret)
                    append("code", code)
                    append("redirect_uri", redirectUrl)
                    append("state", state)
                }.formUrlEncode()
            )
        }.body()
    }

}