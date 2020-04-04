package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.UpdateProjectMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ProjectState
import io.github.tonnyl.moka.type.UpdateProjectInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates an existing project.
 *
 * https://developer.github.com/v4/mutation/updateproject/
 *
 * @param projectId The Project ID to update.
 * @param name The name of project.
 * @param body The description of project.
 * @param state Whether the project is open or closed.
 * @param isPublic Whether the project is public or not.
 */
suspend fun updateProject(
    projectId: String,
    name: String? = null,
    body: String? = null,
    state: ProjectState? = null,
    isPublic: Boolean? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateProjectMutation(
                    UpdateProjectInput(
                        projectId,
                        Input.optional(name),
                        Input.optional(body),
                        Input.optional(state),
                        Input.optional(isPublic)
                    )
                )
            )
            .execute()
    }
}