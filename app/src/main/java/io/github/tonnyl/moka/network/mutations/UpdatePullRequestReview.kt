package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdatePullRequestReviewMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdatePullRequestReviewInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates the body of a pull request review.
 *
 * https://developer.github.com/v4/mutation/updatepullrequestreview/
 *
 * @param pullRequestReviewId The Node ID of the pull request review to modify.
 * @param body The contents of the pull request review body.
 */
suspend fun updatePullRequestReview(
    pullRequestReviewId: String,
    body: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdatePullRequestReviewMutation(
                    UpdatePullRequestReviewInput(
                        pullRequestReviewId = pullRequestReviewId,
                        body = body
                    )
                )
            )
            .execute()
    }
}