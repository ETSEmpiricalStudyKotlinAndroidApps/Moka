package io.github.tonnyl.moka.network

import android.net.Uri
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import io.github.tonnyl.moka.type.CustomType
import io.github.tonnyl.moka.util.Iso8601Utils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object GraphQLClient {

    val accessToken = AtomicReference<String>()

    private const val SERVER_URL = "https://api.github.com/graphql"

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

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

    private val dateCustomTypeAdapter = object : CustomTypeAdapter<Date> {

        override fun encode(value: Date): CustomTypeValue<*> {
            return CustomTypeValue.GraphQLString(Iso8601Utils.format(value))
        }

        override fun decode(value: CustomTypeValue<*>): Date {
            try {
                return Iso8601Utils.parse(value.value.toString())
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