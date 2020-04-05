package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.FollowersQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

/**
 * A list of users the given user is followed by.
 */
@WorkerThread
fun queryUsersFollowers(
    login: String,
    perPage: Int,
    before: String? = null,
    after: String? = null
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            FollowersQuery(
                login,
                perPage,
                Input.optional(before),
                Input.optional(after)
            )
        )
        .execute()
}