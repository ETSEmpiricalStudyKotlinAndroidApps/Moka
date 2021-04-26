package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.mutations.ChangeUserStatusMutation
import io.github.tonnyl.moka.type.ChangeUserStatusInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

suspend fun changeUserStatus(
    apolloClient: ApolloClient,
    emoji: String? = null,
    message: String? = null,
    organizationId: String? = null,
    limitedAvailability: Boolean? = null,
    expiresAt: Instant? = null
) = withContext(Dispatchers.IO) {
    apolloClient
        .mutate(
            mutation = ChangeUserStatusMutation(
                ChangeUserStatusInput(
                    emoji = Input.Present(emoji),
                    message = Input.Present(message),
                    organizationId = Input.Present(organizationId),
                    limitedAvailability = Input.Present(limitedAvailability),
                    expiresAt = Input.Present(expiresAt)
                )
            )
        )
}