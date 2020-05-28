package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.ChangeUserStatusMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ChangeUserStatusInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.*

suspend fun changeUserStatus(
    emoji: String? = null,
    message: String? = null,
    organizationId: String? = null,
    limitedAvailability: Boolean? = null,
    expiresAt: Date? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                ChangeUserStatusMutation(
                    ChangeUserStatusInput(
                        Input.optional(emoji),
                        Input.optional(message),
                        Input.optional(organizationId),
                        Input.optional(limitedAvailability),
                        Input.optional(expiresAt)
                    )
                )
            )
            .execute()
    }
}