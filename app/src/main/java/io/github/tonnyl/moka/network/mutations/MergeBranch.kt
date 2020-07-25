package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.MergeBranchMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.MergeBranchInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Merge a head into a branch.
 *
 * https://developer.github.com/v4/mutation/mergebranch/
 *
 * @param repositoryId The Node ID of the Repository containing the base branch that will be modified.
 * @param base The name of the base branch that the provided head will be merged into.
 * @param head The head to merge into the base branch. This can be a branch name or a commit GitObjectID.
 * @param commitMessage Message to use for the merge commit. If omitted, a default will be used.
 */
suspend fun mergeBranch(
    repositoryId: String,
    base: String,
    head: String,
    commitMessage: String? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                MergeBranchMutation(
                    MergeBranchInput(
                        repositoryId = repositoryId,
                        base = base,
                        head = head,
                        commitMessage = Input.optional(commitMessage)
                    )
                )
            )
            .execute()
    }
}