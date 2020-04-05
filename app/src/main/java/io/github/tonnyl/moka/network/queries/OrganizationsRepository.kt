package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationsRepositoryQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryOrganizationsRepository(
    login: String,
    repoName: String
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            OrganizationsRepositoryQuery(login, repoName)
        )
        .execute()
}