package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.mutations.UpdateRepositoryMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateRepositoryInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * Update information about a repository.
 *
 * https://developer.github.com/v4/mutation/updaterepository/
 *
 * @param repositoryId The ID of the repository to update.
 * @param name The new name of the repository.
 * @param description A new description for the repository. Pass an empty string to erase the existing description.
 * @param template Whether this repository should be marked as a template such that anyone who
 * can access it can create new repositories with the same files and directory structure.
 * @param homepageUrl The URL for a web page about this repository. Pass an empty string to erase the existing URL.
 * @param hasWikiEnabled Indicates if the repository should have the wiki feature enabled.
 * @param hasIssuesEnabled Indicates if the repository should have the issues feature enabled.
 * @param hasProjectsEnabled Indicates if the repository should have the project boards feature enabled.
 */
suspend fun updateRepository(
    repositoryId: String,
    name: String? = null,
    description: String? = null,
    template: Boolean? = null,
    homepageUrl: String? = null,
    hasWikiEnabled: Boolean? = null,
    hasIssuesEnabled: Boolean? = null,
    hasProjectsEnabled: Boolean? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .mutate(
            UpdateRepositoryMutation(
                UpdateRepositoryInput(
                    repositoryId = repositoryId,
                    name = Input.Present(name),
                    description = Input.Present(description),
                    template = Input.Present(template),
                    homepageUrl = Input.Present(homepageUrl),
                    hasWikiEnabled = Input.Present(hasWikiEnabled),
                    hasIssuesEnabled = Input.Present(hasIssuesEnabled),
                    hasProjectsEnabled = Input.Present(hasProjectsEnabled)
                )
            )
        )
        .single()
}