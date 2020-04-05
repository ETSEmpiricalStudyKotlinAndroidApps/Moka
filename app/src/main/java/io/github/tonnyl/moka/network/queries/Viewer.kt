package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.ViewerQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

/**
 * The currently authenticated user.
 */
@WorkerThread
fun queryViewer() = runBlocking {
    GraphQLClient.apolloClient
        .query(
            ViewerQuery()
        )
        .execute()
}