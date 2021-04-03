package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UnresolveReviewThreadMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UnresolveReviewThreadInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Marks a review thread as unresolved.
 *
 * https://developer.github.com/v4/mutation/unresolvereviewthread/
 *
 * @param threadId The ID of the thread to unresolve.
 */
suspend fun unresolveReviewThread(threadId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            UnresolveReviewThreadMutation(
                UnresolveReviewThreadInput(threadId = threadId)
            )
        )
        .single()
}