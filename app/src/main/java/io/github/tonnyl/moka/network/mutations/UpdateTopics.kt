package io.github.tonnyl.moka.network.mutations

import io.github.tonnyl.moka.mutations.UpdateTopicsMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.UpdateTopicsInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Replaces the repository's topics with the given topics.
 *
 * https://developer.github.com/v4/mutation/updatetopics/
 *
 * @param repositoryId The Node ID of the repository.
 * @param topicNames An array of topic names.
 */
suspend fun updateTopics(
    repositoryId: String,
    topicNames: List<String>
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateTopicsMutation(
                    UpdateTopicsInput(
                        repositoryId = repositoryId,
                        topicNames = topicNames
                    )
                )
            )
            .execute()
    }
}