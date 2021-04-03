package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.mutations.RequestReviewsMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.RequestReviewsInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Set review requests on a pull request.
 *
 * https://developer.github.com/v4/mutation/requestreviews/
 *
 * @param pullRequestId The Node ID of the pull request to modify.
 * @param userIds The Node IDs of the user to request.
 * @param teamIds The Node IDs of the team to request.
 * @param union Add users to the set rather than replace.
 */
suspend fun requestReviews(
    pullRequestId: String,
    userIds: List<String>? = null,
    teamIds: List<String>? = null,
    union: Boolean? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            RequestReviewsMutation(
                RequestReviewsInput(
                    pullRequestId = pullRequestId,
                    userIds = Input.Present(userIds),
                    teamIds = Input.Present(teamIds),
                    union = Input.Present(union)
                )
            )
        )
        .single()
}