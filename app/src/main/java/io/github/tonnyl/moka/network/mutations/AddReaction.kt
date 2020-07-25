package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.AddReactionMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.AddReactionInput
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Adds a reaction to a subject.
 *
 * https://developer.github.com/v4/mutation/addreaction/
 *
 * @param subjectId The Node ID of the subject to modify.
 * @param content The name of the emoji to react with.
 */
suspend fun addReaction(
    subjectId: String,
    content: ReactionContent
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                AddReactionMutation(
                    AddReactionInput(
                        subjectId = subjectId,
                        content = content
                    )
                )
            )
            .execute()
    }
}