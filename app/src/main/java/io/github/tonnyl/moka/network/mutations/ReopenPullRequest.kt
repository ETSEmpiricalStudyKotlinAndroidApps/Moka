package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.ReopenPullRequestMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ReopenPullRequestInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Reopen a pull request.
 *
 * https://developer.github.com/v4/mutation/reopenpullrequest/
 *
 * @param pullRequestId ID of the pull request to be reopened.
 */
suspend fun reopenPullRequest(pullRequestId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                ReopenPullRequestMutation(
                    ReopenPullRequestInput(pullRequestId)
                )
            )
            .execute()
    }
}