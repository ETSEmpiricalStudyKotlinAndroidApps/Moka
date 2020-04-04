package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.TransferIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.TransferIssueInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 *
 *
 * @param issueId The Node ID of the issue to be transferred
 * @param repositoryId The Node ID of the repository the issue should be transferred to
 */
suspend fun transferIssue(
    issueId: String,
    repositoryId: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                TransferIssueMutation(
                    TransferIssueInput(issueId, repositoryId)
                )
            )
            .execute()
    }
}