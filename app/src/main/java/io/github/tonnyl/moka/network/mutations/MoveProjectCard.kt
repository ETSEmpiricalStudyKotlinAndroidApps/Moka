package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.MoveProjectCardMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.MoveProjectCardInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Moves a project card to another place.
 *
 * https://developer.github.com/v4/mutation/moveprojectcard/
 *
 * @param cardId The id of the card to move.
 * @param columnId The id of the column to move it into.
 * @param afterCardId Place the new card after the card with this id. Pass null to place it at the top.
 */
suspend fun moveProjectCard(
    cardId: String,
    columnId: String,
    afterCardId: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                MoveProjectCardMutation(
                    MoveProjectCardInput(
                        cardId = cardId,
                        columnId = columnId,
                        afterCardId = Input.optional(afterCardId)
                    )
                )
            )
            .execute()
    }
}