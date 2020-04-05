package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryCurrentLevelTreeView(
    login: String,
    repoName: String,
    expression: String
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            CurrentLevelTreeViewQuery(
                login, repoName, expression
            )
        )
        .execute()
}