package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeletePullRequestReviewCommentMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeletePullRequestReviewCommentInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Deletes a pull request review comment.
 *
 * https://developer.github.com/v4/mutation/deletepullrequestreviewcomment/
 *
 * @param id The ID of the comment to delete.
 */
suspend fun deletePullRequestReviewComment(id: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            DeletePullRequestReviewCommentMutation(
                DeletePullRequestReviewCommentInput(id = id)
            )
        )
        .single()
}