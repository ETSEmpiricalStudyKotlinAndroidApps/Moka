package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.AddCommentMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.AddCommentInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Adds a comment to an Issue or Pull Request.
 *
 * https://developer.github.com/v4/mutation/addcomment/
 *
 * @param subjectId The Node ID of the subject to modify.
 * @param body The contents of the comment.
 */
suspend fun addComment(
    subjectId: String,
    body: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                AddCommentMutation(
                    AddCommentInput(
                        body = body,
                        subjectId = subjectId
                    )
                )
            )
            .execute()
    }
}