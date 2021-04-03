package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.ResolveReviewThreadMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ResolveReviewThreadInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Marks a review thread as resolved.
 *
 * https://developer.github.com/v4/mutation/resolvereviewthread/
 *
 * @param threadId The ID of the thread to resolve.
 */
suspend fun resolveReviewThread(threadId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            ResolveReviewThreadMutation(
                ResolveReviewThreadInput(threadId = threadId)
            )
        )
        .single()
}