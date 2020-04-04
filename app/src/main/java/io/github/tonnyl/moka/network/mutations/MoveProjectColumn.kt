package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.MoveProjectColumnMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.MoveProjectColumnInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Moves a project column to another place.
 *
 * https://developer.github.com/v4/mutation/moveprojectcolumn/
 *
 * @param columnId The id of the column to move.
 * @param afterColumnId Place the new column after the column with this id. Pass null to place it at the front.
 */
suspend fun moveProjectColumn(
    columnId: String,
    afterColumnId: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                MoveProjectColumnMutation(
                    MoveProjectColumnInput(columnId, Input.optional(afterColumnId))
                )
            )
            .execute()
    }
}