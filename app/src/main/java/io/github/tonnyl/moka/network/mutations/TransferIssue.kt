package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.TransferIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.TransferIssueInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
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
    GraphQLClient.apolloClient
        .mutate(
            TransferIssueMutation(
                TransferIssueInput(
                    issueId = issueId,
                    repositoryId = repositoryId
                )
            )
        )
        .single()
}