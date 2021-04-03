package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryOrganization(login: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            OrganizationQuery(login)
        )
        .single()
}