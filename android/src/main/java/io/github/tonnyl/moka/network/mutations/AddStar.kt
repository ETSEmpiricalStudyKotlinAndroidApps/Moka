package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import io.tonnyl.moka.graphql.AddStarMutation
import io.tonnyl.moka.graphql.type.AddStarInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Adds a star to a Starrable.
 *
 * https://developer.github.com/v4/mutation/addstar/
 *
 * @param starrableId The Starrable ID to star.
 */
suspend fun addStar(
    apolloClient: ApolloClient,
    starrableId: String
) = withContext(Dispatchers.IO) {
    apolloClient
        .mutate(
            mutation = AddStarMutation(
                AddStarInput(starrableId = starrableId)
            )
        )
}