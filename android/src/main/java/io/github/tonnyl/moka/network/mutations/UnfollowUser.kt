package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import io.tonnyl.moka.graphql.UnfollowUserMutation
import io.tonnyl.moka.graphql.type.UnfollowUserInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Unfollow a user.
 *
 * https://developer.github.com/v4/mutation/unfollowuser/
 *
 * @param userId ID of the user to unfollow.
 */
suspend fun unfollowUser(
    apolloClient: ApolloClient,
    userId: String
) = withContext(Dispatchers.IO) {
    apolloClient
        .mutate(
            mutation = UnfollowUserMutation(
                UnfollowUserInput(userId = userId)
            )
        )
}