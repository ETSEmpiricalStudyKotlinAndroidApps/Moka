package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdatePullRequestReviewCommentMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdatePullRequestReviewCommentInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates a pull request review comment.
 *
 * https://developer.github.com/v4/mutation/updatepullrequestreviewcomment/
 *
 * @param pullRequestReviewCommentId The Node ID of the comment to modify.
 * @param body The text of the comment.
 */
suspend fun updatePullRequestReviewComment(
    pullRequestReviewCommentId: String,
    body: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdatePullRequestReviewCommentMutation(
                    UpdatePullRequestReviewCommentInput(
                        pullRequestReviewCommentId = pullRequestReviewCommentId,
                        body = body
                    )
                )
            )
            .execute()
    }
}