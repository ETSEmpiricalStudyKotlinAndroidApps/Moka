package io.github.tonnyl.moka.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ResponseAdapter
import com.apollographql.apollo3.api.ResponseAdapterCache
import com.apollographql.apollo3.api.StringResponseAdapter
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.apollographql.apollo3.network.http.ApolloHttpNetworkTransport
import io.github.tonnyl.moka.type.CustomScalars
import kotlinx.datetime.Instant
import java.util.concurrent.atomic.AtomicReference

object GraphQLClient {

    val accessToken = AtomicReference<String>()

    private const val SERVER_URL = "https://api.github.com/graphql"

    object DateCustomScalarAdapter : ResponseAdapter<Instant> {

        override fun fromResponse(
            reader: JsonReader,
            responseAdapterCache: ResponseAdapterCache
        ): Instant {
            return Instant.parse(reader.nextString()!!)
        }

        override fun toResponse(
            writer: JsonWriter,
            responseAdapterCache: ResponseAdapterCache,
            value: Instant
        ) {
            writer.value(value.toString())
        }

    }

    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .networkTransport(
                networkTransport = ApolloHttpNetworkTransport(
                    serverUrl = SERVER_URL,
                    headers = mapOf(
                        "Accept" to "application/json",
                        "Content-Type" to "application/json",
                        "Authorization" to "Bearer ${accessToken.get()}"
                    )
                )
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.GitTimestamp,
                customScalarAdapter = DateCustomScalarAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.DateTime,
                customScalarAdapter = DateCustomScalarAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.PreciseDateTime,
                customScalarAdapter = DateCustomScalarAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.Date,
                customScalarAdapter = DateCustomScalarAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.HTML,
                customScalarAdapter = StringResponseAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.URI,
                customScalarAdapter = StringResponseAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.GitObjectID,
                customScalarAdapter = StringResponseAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.GitSSHRemote,
                customScalarAdapter = StringResponseAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.X509Certificate,
                customScalarAdapter = StringResponseAdapter
            )
            .addScalarTypeAdapter(
                customScalar = CustomScalars.GitRefname,
                customScalarAdapter = StringResponseAdapter
            )
            .build()
    }

}