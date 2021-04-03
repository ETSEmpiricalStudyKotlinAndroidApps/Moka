package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryCurrentLevelTreeView(
    login: String,
    repoName: String,
    expression: String
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            CurrentLevelTreeViewQuery(
                login, repoName, expression
            )
        )
        .single()
}