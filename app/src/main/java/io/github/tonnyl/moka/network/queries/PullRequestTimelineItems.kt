package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.PullRequestTimelineItemsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryPullRequestTimelineItems(
    owner: String,
    name: String,
    number: Int,
    perPage: Int,
    after: String? = null,
    before: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            PullRequestTimelineItemsQuery(
                owner,
                name,
                number,
                Input.Present(after),
                Input.Present(before),
                perPage
            )
        )
        .single()
}