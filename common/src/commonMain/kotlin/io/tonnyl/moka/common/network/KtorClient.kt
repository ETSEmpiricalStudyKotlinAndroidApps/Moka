package io.tonnyl.moka.common.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import io.tonnyl.moka.common.serialization.json

class KtorClient(
    requireAuth: Boolean,
    accessToken: String?
) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json = json)
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        if (requireAuth) {
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(accessToken = accessToken ?: "", refreshToken = "")
                    }
                }
            }
        }
    }

    companion object {

        const val GITHUB_V1_BASE_URL = "https://api.github.com"
        const val GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
        const val GITHUB_GET_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token"

        // Callback urls
        const val GITHUB_AUTHORIZE_CALLBACK_URI = "https://tonnyl.io/moka/callback"
        const val GITHUB_AUTHORIZE_CALLBACK_URI_SCHEMA = "moka-app"
        const val GITHUB_AUTHORIZE_CALLBACK_URI_HOST = "callback"

        // Scope
        const val SCOPE =
            "repo+admin:org+admin:public_key+admin:repo_hook+admin:org_hook+gist+notifications+user+delete_repo+write:discussion+admin:gpg_key"

        val unauthenticatedKtorClient = KtorClient(
            requireAuth = false,
            accessToken = null
        ).httpClient

    }

}