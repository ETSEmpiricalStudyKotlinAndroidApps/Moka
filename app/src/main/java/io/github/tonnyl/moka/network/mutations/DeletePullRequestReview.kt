package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeletePullRequestReviewMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeletePullRequestReviewInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Deletes a pull request review.
 *
 * https://developer.github.com/v4/mutation/deletepullrequestreview/
 *
 * @param pullRequestReviewId The Node ID of the pull request review to delete.
 */
suspend fun deletePullRequestReview(pullRequestReviewId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                DeletePullRequestReviewMutation(
                    DeletePullRequestReviewInput(pullRequestReviewId)
                )
            )
            .execute()
    }
}