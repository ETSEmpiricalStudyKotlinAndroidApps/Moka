package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OrganizationsProjectsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * A list of projects associated with the owner.
 */
suspend fun queryOrganizationsProjects(
    owner: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            OrganizationsProjectsQuery(
                owner,
                Input.Present(after),
                Input.Present(before),
                perPage
            )
        )
        .single()
}