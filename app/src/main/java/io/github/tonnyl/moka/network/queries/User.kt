package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.UserQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryUser(login: String) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            UserQuery(login)
        )
        .single()
}