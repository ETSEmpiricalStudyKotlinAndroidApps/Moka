package io.github.tonnyl.moka.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ResponseAdapter
import com.apollographql.apollo3.api.ResponseAdapterCache
import com.apollographql.apollo3.api.StringResponseAdapter
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import com.apollographql.apollo3.network.http.ApolloHttpNetworkTransport
import io.github.tonnyl.moka.type.Types.Date
import io.github.tonnyl.moka.type.Types.DateTime
import io.github.tonnyl.moka.type.Types.GitObjectID
import io.github.tonnyl.moka.type.Types.GitRefname
import io.github.tonnyl.moka.type.Types.GitSSHRemote
import io.github.tonnyl.moka.type.Types.GitTimestamp
import io.github.tonnyl.moka.type.Types.HTML
import io.github.tonnyl.moka.type.Types.PreciseDateTime
import io.github.tonnyl.moka.type.Types.URI
import io.github.tonnyl.moka.type.Types.X509Certificate
import kotlinx.datetime.Instant

class ApolloGraphQLClient(accessToken: String) {

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
        ApolloClient(
            networkTransport = ApolloHttpNetworkTransport(
                serverUrl = SERVER_URL,
                headers = mapOf(
                    "Accept" to "application/json",
                    "Content-Type" to "application/json",
                    "Authorization" to "Bearer $accessToken"
                )
            )
        ).withCustomScalarAdapter(
            graphqlName = GitTimestamp.name,
            customScalarAdapter = DateCustomScalarAdapter
        ).withCustomScalarAdapter(
            graphqlName = DateTime.name,
            customScalarAdapter = DateCustomScalarAdapter
        ).withCustomScalarAdapter(
            graphqlName = PreciseDateTime.name,
            customScalarAdapter = DateCustomScalarAdapter
        ).withCustomScalarAdapter(
            graphqlName = Date.name,
            customScalarAdapter = DateCustomScalarAdapter
        ).withCustomScalarAdapter(
            graphqlName = HTML.name,
            customScalarAdapter = StringResponseAdapter
        ).withCustomScalarAdapter(
            graphqlName = URI.name,
            customScalarAdapter = StringResponseAdapter
        ).withCustomScalarAdapter(
            graphqlName = GitObjectID.name,
            customScalarAdapter = StringResponseAdapter
        ).withCustomScalarAdapter(
            graphqlName = GitSSHRemote.name,
            customScalarAdapter = StringResponseAdapter
        ).withCustomScalarAdapter(
            graphqlName = X509Certificate.name,
            customScalarAdapter = StringResponseAdapter
        ).withCustomScalarAdapter(
            graphqlName = GitRefname.name,
            customScalarAdapter = StringResponseAdapter
        )
    }

    companion object {

        private const val SERVER_URL = "https://api.github.com/graphql"

    }

}