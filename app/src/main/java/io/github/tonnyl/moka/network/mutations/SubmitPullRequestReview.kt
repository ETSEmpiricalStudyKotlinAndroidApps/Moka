package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.SubmitPullRequestReviewMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.PullRequestReviewEvent
import io.github.tonnyl.moka.type.SubmitPullRequestReviewInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Submits a pending pull request review.
 *
 * https://developer.github.com/v4/mutation/submitpullrequestreview/
 *
 * @param pullRequestId The Pull Request ID to submit any pending reviews.
 * @param pullRequestReviewId The Pull Request Review ID to submit.
 * @param event The event to send to the Pull Request Review.
 * @param body The text field to set on the Pull Request Review.
 */
suspend fun submitPullRequestReview(
    event: PullRequestReviewEvent,
    pullRequestId: String? = null,
    pullRequestReviewId: String? = null,
    body: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                SubmitPullRequestReviewMutation(
                    SubmitPullRequestReviewInput(
                        Input.optional(pullRequestId),
                        Input.optional(pullRequestReviewId),
                        event,
                        Input.optional(body)
                    )
                )
            )
            .execute()
    }
}