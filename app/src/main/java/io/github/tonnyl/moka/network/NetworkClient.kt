package io.github.tonnyl.moka.network

import android.net.Uri
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.type.CustomType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object NetworkClient {

    var accessToken: String = BuildConfig.TEST_TOKEN

    private const val SERVER_URL = "https://api.github.com/graphql"

    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .authenticator { _, response ->
                response.request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            }
            .build()
    }

    private val dateCustomTypeAdapter = object : CustomTypeAdapter<Date> {

        override fun encode(value: Date): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(DATE_FORMAT.format(value))

        override fun decode(value: CustomTypeValue<*>): Date {
            try {
                return DATE_FORMAT.parse(value.value.toString())
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        }

    }

    private val uriCustomTypeAdapter = object : CustomTypeAdapter<Uri> {

        override fun encode(value: Uri): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(value.toString())

        override fun decode(value: CustomTypeValue<*>): Uri {
            try {
                return Uri.parse(value.value.toString())
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        }

    }

    private val htmlCustomTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(value)

        override fun decode(value: CustomTypeValue<*>): String = value.value.toString()

    }

    private val idCustomTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(value)

        override fun decode(value: CustomTypeValue<*>): String = value.value.toString()

    }

    private val gitObjectIDTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(value)

        override fun decode(value: CustomTypeValue<*>): String = value.value.toString()

    }

    private val gitSSHRemoteTypeAdapter = object : CustomTypeAdapter<String> {

        override fun encode(value: String): CustomTypeValue<*> =
            CustomTypeValue.GraphQLString(value)

        override fun decode(value: CustomTypeValue<*>): String = value.value.toString()

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