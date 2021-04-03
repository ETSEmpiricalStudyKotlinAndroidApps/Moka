package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdateSubscriptionMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.SubscriptionState
import io.github.tonnyl.moka.type.UpdateSubscriptionInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Updates the state for subscribable subjects.
 *
 * https://developer.github.com/v4/mutation/updatesubscription/
 *
 * @param subscribableId The Node ID of the subscribable object to modify.
 * @param state The new state of the subscription.
 */
suspend fun updateSubscription(
    subscribableId: String,
    state: SubscriptionState
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            UpdateSubscriptionMutation(
                UpdateSubscriptionInput(
                    state = state,
                    subscribableId = subscribableId
                )
            )
        )
        .single()
}