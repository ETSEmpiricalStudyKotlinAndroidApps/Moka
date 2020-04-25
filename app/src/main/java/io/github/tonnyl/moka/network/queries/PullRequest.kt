package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.PullRequestQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryPullRequest(
    owner: String,
    name: String,
    number: Int,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            PullRequestQuery(
                owner,
                name,
                number,
                Input.optional(after),
                Input.optional(before),
                perPage
            )
        )
        .execute()
}