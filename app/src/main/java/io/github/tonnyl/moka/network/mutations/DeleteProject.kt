package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteProjectMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteProjectInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Deletes a project.
 *
 * https://developer.github.com/v4/mutation/deleteproject/
 *
 * @param projectId The Project ID to update.
 */
suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            DeleteProjectMutation(
                DeleteProjectInput(projectId = projectId)
            )
        )
        .single()
}