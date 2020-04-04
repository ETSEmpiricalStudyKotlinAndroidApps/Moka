package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UnfollowUserMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UnfollowUserInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Unfollow a user.
 *
 * https://developer.github.com/v4/mutation/unfollowuser/
 *
 * @param userId ID of the user to unfollow.
 */
suspend fun unfollowUser(userId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UnfollowUserMutation(
                    UnfollowUserInput(userId)
                )
            )
            .execute()
    }
}