package io.github.tonnyl.moka.network

import android.net.Uri
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import io.github.tonnyl.moka.type.CustomType
import kotlinx.datetime.Instant
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.text.ParseException
import java.util.concurrent.atomic.AtomicReference

object GraphQLClient {

    val accessToken = AtomicReference<String>()

    private const val SERVER_URL = "https://api.github.com/graphql"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .authenticator(object : Authenticator {

                override fun authenticate(route: Route?, response: Response): Request? {
                    return response.request
                        .newBuilder()
                        .addHeader("Authorization", "Bearer ${accessToken.get()}")
                        .build()
                }

            })
            .build()
    }

    private val dateCustomTypeAdapter = object : CustomTypeAdapter<Instant> {

        override fun encode(value: Instant): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value.toString())
        }

        override fun decode(value: CustomTypeValue<*>): Instant {
            try {
                return Instant.parse(value.value.toString())
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        }

    }

    private val uriCustomTypeAdapter = object : CustomTypeAdapter<Uri> {

        override fun encode(value: Uri): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value.toString())
        }

        override fun decode(value: CustomTypeValue<*>): Uri {
            try {
                return Uri.parse(value.value.toString())
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        }

    }

    private val htmlCustomTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value)
        }

        override fun decode(value: CustomTypeValue<*>): String {
            return value.value.toString()
        }

    }

    private val idCustomTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value)
        }

        override fun decode(value: CustomTypeValue<*>): String {
            return value.value.toString()
        }

    }

    private val gitObjectIDTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value)
        }

        override fun decode(value: CustomTypeValue<*>): String {
            return value.value.toString()
        }

    }

    private val gitSSHRemoteTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(value)
        }

        override fun decode(value: CustomTypeValue<*>): String {
            return value.value.toString()
        }

    }

    val apolloClient: ApolloClient by lazy {
        ApolloClient.builder()
            .okHttpClient(okHttpClient)
            .addCustomTypeAdapter(
                CustomType.DATETIME,
                dateCustomTypeAdapter
            )
            .addCustomTypeAdapter(
                CustomType.URI,
                uriCustomTypeAdapter
            )
            .addCustomTypeAdapter(
                CustomType.HTML,
                htmlCustomTypeAdapter
            )
            .addCustomTypeAdapter(
                CustomType.ID,
                idCustomTypeAdapter
            )
            .addCustomTypeAdapter(
                CustomType.GITOBJECTID,
                gitObjectIDTypeAdapter
            )
            .addCustomTypeAdapter(
                CustomType.GITSSHREMOTE,
                gitSSHRemoteTypeAdapter
            )
            .serverUrl(SERVER_URL)
            .build()
    }

}