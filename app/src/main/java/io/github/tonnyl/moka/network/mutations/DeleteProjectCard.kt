package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteProjectCardMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteProjectCardInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Deletes a project card.
 *
 * https://developer.github.com/v4/mutation/deleteprojectcard/
 *
 * @param cardId The id of the card to delete.
 */
suspend fun deleteProjectCard(cardId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            DeleteProjectCardMutation(
                DeleteProjectCardInput(cardId)
            )
        )
        .single()
}