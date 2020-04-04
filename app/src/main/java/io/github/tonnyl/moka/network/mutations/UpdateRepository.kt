package io.github.tonnyl.moka.network.mutations

import android.net.Uri
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.UpdateRepositoryMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateRepositoryInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
    homepageUrl: Uri? = null,
    hasWikiEnabled: Boolean? = null,
    hasIssuesEnabled: Boolean? = null,
    hasProjectsEnabled: Boolean? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateRepositoryMutation(
                    UpdateRepositoryInput(
                        repositoryId,
                        Input.optional(name),
                        Input.optional(description),
                        Input.optional(template),
                        Input.optional(homepageUrl),
                        Input.optional(hasWikiEnabled),
                        Input.optional(hasIssuesEnabled),
                        Input.optional(hasProjectsEnabled)
                    )
                )
            )
            .execute()
    }
}