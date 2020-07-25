package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteIssueCommentMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteIssueCommentInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Deletes an IssueComment object.
 *
 * https://developer.github.com/v4/mutation/deleteissuecomment/
 *
 * @param id The ID of the comment to delete.
 */
suspend fun deleteIssueComment(id: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                DeleteIssueCommentMutation(
                    DeleteIssueCommentInput(id = id)
                )
            )
            .execute()
    }
}