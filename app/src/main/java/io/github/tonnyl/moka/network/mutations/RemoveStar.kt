package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.RemoveStarMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.RemoveStarInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Autogenerated input type of RemoveStar
 *
 * https://developer.github.com/v4/mutation/removestar/
 *
 * @param starrableId The Starrable ID to unstar.
 */
suspend fun removeStar(starrableId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            mutation = RemoveStarMutation(
                RemoveStarInput(starrableId = starrableId)
            )
        )
}