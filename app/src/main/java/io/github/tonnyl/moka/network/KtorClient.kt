package io.github.tonnyl.moka.network

import android.content.Context
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.util.json
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import okhttp3.Interceptor
import okhttp3.Response

class KtorClient(
    context: Context,
    requireAuth: Boolean,
    accessToken: String?
) {

    val httpClient = HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        engine {
            preconfigured = (context.applicationContext as MokaApp).okHttpClient
            if (requireAuth
                && !accessToken.isNullOrEmpty()
            ) {
                addInterceptor(object : Interceptor {

                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()

                        // Custom the request header.
                        val requestBuilder = original.newBuilder()
                            .header(
                                "Authorization",
                                "Bearer $accessToken"
                            )
                            .method(original.method, original.body)
                        val request = requestBuilder.build()
                        return chain.proceed(request)
                    }

                })
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

    }

}