package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.RemoveReactionMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.type.RemoveReactionInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Removes a reaction from a subject.
 *
 * https://developer.github.com/v4/mutation/removereaction/
 *
 * @param subjectId The Node ID of the subject to modify.
 * @param content The name of the emoji reaction to remove.
 */
suspend fun removeReaction(
    subjectId: String,
    content: ReactionContent
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                RemoveReactionMutation(
                    RemoveReactionInput(
                        subjectId = subjectId,
                        content = content
                    )
                )
            )
            .execute()
    }
}