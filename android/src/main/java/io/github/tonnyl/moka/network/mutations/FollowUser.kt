package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import io.tonnyl.moka.graphql.FollowUserMutation
import io.tonnyl.moka.graphql.type.FollowUserInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Follow a user.
 *
 * https://developer.github.com/v4/mutation/followuser/
 *
 * @param userId ID of the user to follow.
 */
suspend fun followUser(
    apolloClient: ApolloClient,
    userId: String
) = withContext(Dispatchers.IO) {
    apolloClient
        .mutate(
            mutation = FollowUserMutation(
                FollowUserInput(userId = userId)
            )
        )
}