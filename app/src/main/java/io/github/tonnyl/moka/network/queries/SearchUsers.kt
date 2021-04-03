package io.github.tonnyl.moka.network.queries

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.SearchUsersQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

suspend fun querySearchUsers(
    queryWords: String,
    first: Int? = null,
    last: Int? = null,
    after: String? = null,
    before: String? = null
) = withContext(Dispatchers.IO) {
    GraphQLClient.apolloClient
        .query(
            SearchUsersQuery(
                queryWords,
                Input.Present(first),
                Input.Present(last),
                Input.Present(after),
                Input.Present(before)
            )
        )
        .single()
}