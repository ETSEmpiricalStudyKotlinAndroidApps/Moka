package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.UpdateProjectCardMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateProjectCardInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates an existing project card.
 *
 * https://developer.github.com/v4/mutation/updateprojectcard/
 *
 * @param projectCardId The ProjectCard ID to update.
 * @param isArchived Whether or not the ProjectCard should be archived.
 * @param note The note of ProjectCard.
 */
suspend fun updateProjectCard(
    projectCardId: String,
    isArchived: Boolean? = null,
    note: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateProjectCardMutation(
                    UpdateProjectCardInput(
                        projectCardId = projectCardId,
                        isArchived = Input.optional(isArchived),
                        note = Input.optional(note)
                    )
                )
            )
            .execute()
    }
}