package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.graphql.ChangeUserStatusMutation
import io.tonnyl.moka.graphql.type.ChangeUserStatusInput
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
                    emoji = Optional.Present(emoji),
                    message = Optional.Present(message),
                    organizationId = Optional.Present(organizationId),
                    limitedAvailability = Optional.Present(limitedAvailability),
                    expiresAt = Optional.Present(expiresAt)
                )
            )
        )
}