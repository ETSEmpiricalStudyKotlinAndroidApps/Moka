package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteProjectColumnMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteProjectColumnInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Deletes a project column.
 *
 * https://developer.github.com/v4/mutation/deleteprojectcolumn/
 *
 * @param columnId The id of the column to delete.
 */
suspend fun deleteProjectColumn(columnId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                DeleteProjectColumnMutation(
                    DeleteProjectColumnInput(columnId = columnId)
                )
            )
            .execute()
    }
}