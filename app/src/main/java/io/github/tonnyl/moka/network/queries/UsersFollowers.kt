package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.FollowersQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * A list of users the given user is followed by.
 */
suspend fun queryUsersFollowers(
    login: String,
    perPage: Int,
    before: String? = null,
    after: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            FollowersQuery(
                login,
                perPage,
                Input.Present(before),
                Input.Present(after)
            )
        )
        .single()
}