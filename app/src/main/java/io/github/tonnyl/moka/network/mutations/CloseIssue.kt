package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.CloseIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.CloseIssueInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Close an issue.
 *
 * https://developer.github.com/v4/mutation/closeissue/
 *
 * @param issueId ID of the issue to be closed.
 */
suspend fun closeIssue(issueId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            CloseIssueMutation(
                CloseIssueInput(issueId = issueId)
            )
        )
        .single()
}