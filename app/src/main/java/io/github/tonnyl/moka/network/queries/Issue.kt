package io.github.tonnyl.moka.network.queries

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking

@WorkerThread
fun queryIssue(
    owner: String,
    name: String,
    number: Int
) = runBlocking {
    GraphQLClient.apolloClient
        .query(
            IssueQuery(owner, name, number)
        )
        .execute()
}