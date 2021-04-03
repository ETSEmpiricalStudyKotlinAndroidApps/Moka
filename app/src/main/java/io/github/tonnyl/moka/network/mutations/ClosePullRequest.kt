package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.ClosePullRequestMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ClosePullRequestInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Close a pull request.
 *
 * https://developer.github.com/v4/mutation/closepullrequest/
 *
 * @param pullRequestId ID of the pull request to be closed.
 */
suspend fun closePullRequest(pullRequestId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            ClosePullRequestMutation(
                ClosePullRequestInput(pullRequestId = pullRequestId)
            )
        )
        .single()
}