package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.UpdatePullRequestMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.PullRequestUpdateState
import io.github.tonnyl.moka.type.UpdatePullRequestInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Update a pull request.
 *
 * https://developer.github.com/v4/mutation/updatepullrequest/
 *
 * @param pullRequestId The Node ID of the pull request.
 * @param baseRefName The name of the branch you want your changes pulled into. This should be an existing branch
 * on the current repository.
 * @param title The title of the pull request.
 * @param body The contents of the pull request.
 * @param state The target state of the pull request.
 * @param maintainerCanModify Indicates whether maintainers can modify the pull request.
 * @param assigneeIds An array of Node IDs of users for this pull request.
 * @param milestoneId The Node ID of the milestone for this pull request.
 * @param labelIds An array of Node IDs of labels for this pull request.
 * @param projectIds An array of Node IDs for projects associated with this pull request.
 */
suspend fun updatePullRequest(
    pullRequestId: String,
    baseRefName: String? = null,
    title: String? = null,
    body: String? = null,
    state: PullRequestUpdateState? = null,
    maintainerCanModify: Boolean? = null,
    assigneeIds: List<String>? = null,
    milestoneId: String? = null,
    labelIds: List<String>? = null,
    projectIds: List<String>? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdatePullRequestMutation(
                    UpdatePullRequestInput(
                        pullRequestId = pullRequestId,
                        baseRefName = Input.optional(baseRefName),
                        title = Input.optional(title),
                        body = Input.optional(body),
                        state = Input.optional(state),
                        maintainerCanModify = Input.optional(maintainerCanModify),
                        assigneeIds = Input.optional(assigneeIds),
                        milestoneId = Input.optional(milestoneId),
                        labelIds = Input.optional(labelIds),
                        projectIds = Input.optional(projectIds)
                    )
                )
            )
            .execute()
    }
}