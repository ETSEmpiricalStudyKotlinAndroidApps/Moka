package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.CloseIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.CloseIssueInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Close an issue.
 *
 * https://developer.github.com/v4/mutation/closeissue/
 *
 * @param issueId ID of the issue to be closed.
 */
suspend fun closeIssue(issueId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                CloseIssueMutation(
                    CloseIssueInput(issueId)
                )
            )
            .execute()
    }
}