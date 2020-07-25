package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.LinkRepositoryToProjectMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.LinkRepositoryToProjectInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Creates a repository link for a project.
 *
 * https://developer.github.com/v4/mutation/linkrepositorytoproject/
 *
 * @param projectId The ID of the Project to link to a Repository.
 * @param repositoryId The ID of the Repository to link to a Project.
 */
suspend fun linkRepositoryToProject(
    projectId: String,
    repositoryId: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                LinkRepositoryToProjectMutation(
                    LinkRepositoryToProjectInput(
                        projectId = projectId,
                        repositoryId = repositoryId
                    )
                )
            )
            .execute()
    }
}