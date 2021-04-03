package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.UsersProjectsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun queryUsersProjects(
    owner: String,
    after: String? = null,
    before: String? = null,
    perPage: Int
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            UsersProjectsQuery(
                owner,
                Input.Present(after),
                Input.Present(before),
                perPage
            )
        )
        .single()
}