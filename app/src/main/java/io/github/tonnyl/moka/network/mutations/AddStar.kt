package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.AddStarMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.AddStarInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Adds a star to a Starrable.
 *
 * https://developer.github.com/v4/mutation/addstar/
 *
 * @param starrableId The Starrable ID to star.
 */
suspend fun addStar(starrableId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                AddStarMutation(
                    AddStarInput(starrableId)
                )
            )
            .execute()
    }
}