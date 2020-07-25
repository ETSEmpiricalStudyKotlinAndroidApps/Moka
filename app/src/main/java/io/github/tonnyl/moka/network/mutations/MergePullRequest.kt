package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.MergePullRequestMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.MergePullRequestInput
import io.github.tonnyl.moka.type.PullRequestMergeMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Merge a pull request.
 *
 * https://developer.github.com/v4/mutation/mergepullrequest/
 *
 * @param pullRequestId ID of the pull request to be merged.
 * @param commitHeadline Commit headline to use for the merge commit; if omitted, a default message will be used.
 * @param commitBody Commit body to use for the merge commit; if omitted, a default message will be used.
 * @param expectedHeadOid OID that the pull request head ref must match to allow merge; if omitted, no check is
 * performed.
 * @param mergeMethod The merge method to use. If omitted, defaults to [PullRequestMergeMethod.MERGE].
 */
suspend fun mergePullRequest(
    pullRequestId: String,
    commitHeadline: String? = null,
    commitBody: String? = null,
    expectedHeadOid: String? = null,
    mergeMethod: PullRequestMergeMethod? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                MergePullRequestMutation(
                    MergePullRequestInput(
                        pullRequestId = pullRequestId,
                        commitHeadline = Input.optional(commitHeadline),
                        commitBody = Input.optional(commitBody),
                        expectedHeadOid = Input.optional(expectedHeadOid),
                        mergeMethod = Input.optional(mergeMethod)
                    )
                )
            )
    }
}