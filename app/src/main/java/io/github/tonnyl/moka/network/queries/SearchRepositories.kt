package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun querySearchRepositories(
    queryWords: String,
    first: Int? = null,
    last: Int? = null,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            SearchRepositoriesQuery(
                queryWords,
                Input.optional(first),
                Input.optional(last),
                Input.optional(after),
                Input.optional(before)
            )
        )
        .execute()
}