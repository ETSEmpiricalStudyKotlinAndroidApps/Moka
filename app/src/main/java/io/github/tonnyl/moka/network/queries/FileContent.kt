package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.FileContentQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryFileContent(
    login: String,
    repoName: String,
    expression: String
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            FileContentQuery(login, repoName, expression)
        )
        .single()
}