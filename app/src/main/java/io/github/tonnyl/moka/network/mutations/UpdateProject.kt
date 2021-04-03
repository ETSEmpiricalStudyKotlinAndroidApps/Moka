package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.mutations.UpdateProjectMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.ProjectState
import io.github.tonnyl.moka.type.UpdateProjectInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
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
    GraphQLClient.apolloClient
        .mutate(
            UpdateProjectMutation(
                UpdateProjectInput(
                    projectId = projectId,
                    name = Input.Present(name),
                    body = Input.Present(body),
                    state = Input.Present(state),
                    public_ = Input.Present(isPublic)
                )
            )
        )
        .single()
}