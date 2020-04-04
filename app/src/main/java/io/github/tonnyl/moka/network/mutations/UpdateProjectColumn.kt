package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdateProjectColumnMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateProjectColumnInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates an existing project column.
 *
 * https://developer.github.com/v4/mutation/updateprojectcolumn/
 *
 * @param projectColumnId The ProjectColumn ID to update.
 * @param name The name of project column.
 */
suspend fun updateProjectColumn(
    projectColumnId: String,
    name: String
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateProjectColumnMutation(
                    UpdateProjectColumnInput(projectColumnId, name)
                )
            )
            .execute()
    }
}