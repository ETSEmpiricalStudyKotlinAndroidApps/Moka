package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdateIssueCommentMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateIssueCommentInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates an IssueComment object.
 *
 * https://developer.github.com/v4/mutation/updateissuecomment/
 *
 * @param id The ID of the IssueComment to modify.
 * @param body The updated text of the comment.
 */
suspend fun updateIssueComment(
    id: String,
    body: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateIssueCommentMutation(
                    UpdateIssueCommentInput(
                        id = id,
                        body = body
                    )
                )
            )
            .execute()
    }
}