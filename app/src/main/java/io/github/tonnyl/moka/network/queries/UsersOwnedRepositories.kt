package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.OwnedRepositoriesQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryOwnedRepositories(
    login: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            OwnedRepositoriesQuery(
                login,
                perPage,
                Input.optional(after),
                Input.optional(before)
            )
        )
        .execute()
}