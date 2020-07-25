package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.AddProjectColumnMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.AddProjectColumnInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Adds a column to a Project.
 *
 * https://developer.github.com/v4/mutation/addprojectcolumn/
 *
 * @param projectId The Node ID of the project.
 * @param name The name of the column.
 */
suspend fun addProjectColumn(
    projectId: String,
    name: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                AddProjectColumnMutation(
                    AddProjectColumnInput(
                        projectId = projectId,
                        name = name
                    )
                )
            )
            .execute()
    }
}