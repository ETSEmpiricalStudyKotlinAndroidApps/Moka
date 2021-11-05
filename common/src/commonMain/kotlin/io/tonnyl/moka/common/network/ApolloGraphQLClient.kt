package io.tonnyl.moka.common.network

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.adapter.KotlinxInstantAdapter
import com.apollographql.apollo3.adapter.KotlinxLocalDateAdapter
import com.apollographql.apollo3.api.StringAdapter
import com.apollographql.apollo3.api.http.HttpHeader
import com.apollographql.apollo3.network.http.HttpNetworkTransport
import com.apollographql.apollo3.network.http.LoggingInterceptor
import com.apollographql.apollo3.network.http.withDefaultHeaders
import io.tonnyl.moka.graphql.type.*

class ApolloGraphQLClient(accessToken: String) {

    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .networkTransport(
                networkTransport = HttpNetworkTransport(
                    serverUrl = SERVER_URL,
                    interceptors = listOf(LoggingInterceptor())
                ).withDefaultHeaders(
                    headers = listOf(
                        HttpHeader("Accept", "application/json"),
                        HttpHeader("Content-Type", "application/json"),
                        HttpHeader("Authorization", "Bearer $accessToken")
                    )
                )
            )
            .addCustomScalarAdapter(
                customScalarType = GitTimestamp.type,
                customScalarAdapter = KotlinxInstantAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = DateTime.type,
                customScalarAdapter = KotlinxInstantAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = PreciseDateTime.type,
                customScalarAdapter = KotlinxInstantAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = Date.type,
                customScalarAdapter = KotlinxLocalDateAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = HTML.type,
                customScalarAdapter = StringAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = URI.type,
                customScalarAdapter = StringAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = GitObjectID.type,
                customScalarAdapter = StringAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = GitSSHRemote.type,
                customScalarAdapter = StringAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = X509Certificate.type,
                customScalarAdapter = StringAdapter
            )
            .addCustomScalarAdapter(
                customScalarType = GitRefname.type,
                customScalarAdapter = StringAdapter
            )
            .build()
    }

    companion object {

        private const val SERVER_URL = "https://api.github.com/graphql"

    }

}