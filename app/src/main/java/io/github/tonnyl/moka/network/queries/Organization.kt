package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryOrganization(login: String) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            OrganizationQuery(login)
        )
        .execute()
}