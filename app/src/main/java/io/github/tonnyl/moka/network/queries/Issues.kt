package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.IssuesQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryIssues(
    owner: String,
    name: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            IssuesQuery(
                owner,
                name,
                Input.optional(after),
                Input.optional(before),
                perPage
            )
        )
        .execute()
}