package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteIssueInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Deletes an Issue object.
 *
 * https://developer.github.com/v4/mutation/deleteissue/
 *
 * @param issueId The ID of the issue to delete.
 */
suspend fun deleteIssue(issueId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            DeleteIssueMutation(
                DeleteIssueInput(issueId = issueId)
            )
        )
        .single()
}