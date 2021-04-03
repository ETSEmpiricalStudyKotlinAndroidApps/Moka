package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryOrganizationsRepository(
    login: String,
    repoName: String
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            OrganizationsRepositoryQuery(login, repoName)
        )
        .single()
}