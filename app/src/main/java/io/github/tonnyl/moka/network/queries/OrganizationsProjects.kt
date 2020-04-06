package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationsProjectsQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

/**
 * A list of projects associated with the owner.
 */
@WorkerThread
fun queryOrganizationsProjects(
    owner: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            OrganizationsProjectsQuery(
                owner,
                Input.optional(after),
                Input.optional(before),
                perPage
            )
        )
        .execute()
}