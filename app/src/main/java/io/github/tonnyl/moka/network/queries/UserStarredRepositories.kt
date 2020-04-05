package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.StarredRepositoriesQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

/**
 * Repositories that the user has starred.
 */
@WorkerThread
fun queryStarredRepositories(
    login: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            StarredRepositoriesQuery(
                login,
                perPage,
                Input.optional(after),
                Input.optional(before)
            )
        )
        .execute()
}