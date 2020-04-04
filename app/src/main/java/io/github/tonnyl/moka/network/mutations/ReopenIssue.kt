package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.ReopenIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ReopenIssueInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Reopen a issue.
 *
 * https://developer.github.com/v4/mutation/reopenissue/
 *
 * @param issueId ID of the issue to be opened.
 */
suspend fun reopenIssue(issueId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                ReopenIssueMutation(
                    ReopenIssueInput(issueId)
                )
            )
            .execute()
    }
}