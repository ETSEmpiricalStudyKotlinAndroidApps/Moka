package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.FileContentQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryFileContent(
    login: String,
    repoName: String,
    expression: String
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            FileContentQuery(login, repoName, expression)
        )
        .execute()
}