package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.ViewerQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * The currently authenticated user.
 */
suspend fun queryViewer() = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            ViewerQuery()
        )
        .single()
}