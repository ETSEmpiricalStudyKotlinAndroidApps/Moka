package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.StarredRepositoriesQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Repositories that the user has starred.
 */
suspend fun queryStarredRepositories(
    login: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            StarredRepositoriesQuery(
                login,
                perPage,
                Input.Present(after),
                Input.Present(before)
            )
        )
        .single()
}