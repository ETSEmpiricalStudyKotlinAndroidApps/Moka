package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.UsersProjectsQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryUsersProjects(
    owner: String,
    after: String? = null,
    before: String? = null,
    perPage: Int
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            UsersProjectsQuery(
                owner,
                Input.optional(after),
                Input.optional(before),
                perPage
            )
        )
        .execute()
}