package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.FollowUserMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.FollowUserInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Follow a user.
 *
 * https://developer.github.com/v4/mutation/followuser/
 *
 * @param userId ID of the user to follow.
 */
suspend fun followUser(userId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                FollowUserMutation(
                    FollowUserInput(userId)
                )
            )
            .execute()
    }
}