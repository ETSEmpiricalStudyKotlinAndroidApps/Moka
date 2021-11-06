package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import io.tonnyl.moka.graphql.RemoveStarMutation
import io.tonnyl.moka.graphql.type.RemoveStarInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Autogenerated input type of RemoveStar
 *
 * https://developer.github.com/v4/mutation/removestar/
 *
 * @param starrableId The Starrable ID to unstar.
 */
suspend fun removeStar(
    apolloClient: ApolloClient,
    starrableId: String
) = withContext(Dispatchers.IO) {
    apolloClient
        .mutate(
            mutation = RemoveStarMutation(
                RemoveStarInput(starrableId = starrableId)
            )
        )
}