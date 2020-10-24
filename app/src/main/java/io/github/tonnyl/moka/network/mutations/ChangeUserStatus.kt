package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.ChangeUserStatusMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ChangeUserStatusInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

suspend fun changeUserStatus(
    emoji: String? = null,
    message: String? = null,
    organizationId: String? = null,
    limitedAvailability: Boolean? = null,
    expiresAt: Instant? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                ChangeUserStatusMutation(
                    ChangeUserStatusInput(
                        emoji = Input.optional(emoji),
                        message = Input.optional(message),
                        organizationId = Input.optional(organizationId),
                        limitedAvailability = Input.optional(limitedAvailability),
                        expiresAt = Input.optional(expiresAt)
                    )
                )
            )
            .execute()
    }
}