package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.PullRequestsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryPullRequests(
    owner: String,
    name: String,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            PullRequestsQuery(
                owner,
                name,
                Input.Present(after),
                Input.Present(before),
                perPage
            )
        )
        .single()
}