package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.AddProjectCardMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.AddProjectCardInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Adds a card to a ProjectColumn.
 * Either [contentId] or [note] must be provided but not both.
 *
 * https://developer.github.com/v4/mutation/addprojectcard/
 *
 * @param projectColumnId The Node ID of the ProjectColumn.
 * @param contentId The content of the card. Must be a member of the ProjectCardItem union.
 * @param note The note on the card.
 */
suspend fun addProjectCard(
    projectColumnId: String,
    contentId: String? = null,
    note: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                AddProjectCardMutation(
                    AddProjectCardInput(
                        projectColumnId,
                        Input.optional(contentId),
                        Input.optional(note)
                    )
                )
            )
            .execute()
    }
}