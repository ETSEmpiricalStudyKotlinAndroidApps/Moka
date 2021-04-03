package io.github.tonnyl.moka.network.queries

import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

/**
 * A repository contains the content for a project.
 */
suspend fun queryUsersRepository(
    login: String,
    repoName: String
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            UsersRepositoryQuery(login, repoName)
        )
        .single()
}