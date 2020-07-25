package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.DeleteRefMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.DeleteRefInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Delete a Git Ref.
 *
 * https://developer.github.com/v4/mutation/deleteref/
 *
 * @param refId The Node ID of the Ref to be deleted.
 */
suspend fun deleteRef(refId: String) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                DeleteRefMutation(
                    DeleteRefInput(refId = refId)
                )
            )
            .execute()
    }
}