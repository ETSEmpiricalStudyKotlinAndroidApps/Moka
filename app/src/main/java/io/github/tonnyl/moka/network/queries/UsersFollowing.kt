package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.FollowingQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryUsersFollowing(
    login: String,
    perPage: Int,
    before: String? = null,
    after: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            FollowingQuery(
                login,
                perPage,
                Input.Present(before),
                Input.Present(after)
            )
        )
        .single()
}