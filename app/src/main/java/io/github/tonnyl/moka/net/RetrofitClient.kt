package io.github.tonnyl.moka.net

import android.content.Context
import com.google.gson.GsonBuilder
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AccessToken
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

object RetrofitClient {

    // The latest token
    private var lastToken: String = BuildConfig.TEST_TOKEN
    // The [retrofit2.Retrofit] instance for whole app.
    private lateinit var retrofit: Retrofit

    private var cache: Cache? = null

    private const val GITHUB_V1_BASE_URL = "https://api.github.com"
    const val GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize"
    const val GITHUB_GET_ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token"
    // Callback urls
    const val GITHUB_AUTHORIZE_CALLBACK_URI = "moka-app://callback"
    const val GITHUB_AUTHORIZE_CALLBACK_URI_SCHEMA = "moka-app"
    const val GITHUB_AUTHORIZE_CALLBACK_URI_HOST = "callback"
    // Scope
    const val SCOPE = "repo+admin:org+admin:public_key+admin:repo_hook+admin:org_hook+gist+notifications+user+delete_repo+write:discussion+admin:gpg_key"

    fun init(context: Context) {
        cache?.let {
            throw IllegalStateException("Retrofit cache already initialized.")
        }
        cache = Cache(context.cacheDir, 20 * 1024 * 1024)
    }

    fun <T> createService(serviceClass: Class<T>, accessToken: AccessToken?): T {
        val currentToken: String = accessToken?.accessToken ?: BuildConfig.TEST_TOKEN

        if (!::retrofit.isInitialized || currentToken != lastToken) {
            lastToken = currentToken

            // Custom the http client.
            val httpClientBuilder = OkHttpClient.Builder()
            httpClientBuilder.addInterceptor { chain ->
                val original = chain.request()

                // Custom the request header.
                val requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer $lastToken")
                        .method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)
            }.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .cache(cache)

            val gson = GsonBuilder()
                    .setDateFormat(DateFormat.FULL, DateFormat.FULL)
                    .create()

            // Set the corresponding convert factory and call adapter factory.
            val retrofitBuilder = Retrofit.Builder()
                    .baseUrl(GITHUB_V1_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

            retrofit = retrofitBuilder
                    .client(httpClientBuilder.build())
                    .build()
        }

        return retrofit.create(serviceClass)
    }

}