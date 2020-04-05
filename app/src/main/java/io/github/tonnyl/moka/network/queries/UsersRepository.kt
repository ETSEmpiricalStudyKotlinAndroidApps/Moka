package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.UsersRepositoryQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

/**
 * A repository contains the content for a project.
 */
@WorkerThread
fun queryUsersRepository(
    login: String,
    repoName: String
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            UsersRepositoryQuery(login, repoName)
        )
        .execute()
}