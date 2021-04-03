package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo3.api.Input
import io.github.tonnyl.moka.mutations.UpdatePullRequestMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.PullRequestUpdateState
import io.github.tonnyl.moka.type.UpdatePullRequestInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.single
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
    GraphQLClient.apolloClient
        .mutate(
            UpdatePullRequestMutation(
                UpdatePullRequestInput(
                    pullRequestId = pullRequestId,
                    baseRefName = Input.Present(baseRefName),
                    title = Input.Present(title),
                    body = Input.Present(body),
                    state = Input.Present(state),
                    maintainerCanModify = Input.Present(maintainerCanModify),
                    assigneeIds = Input.Present(assigneeIds),
                    milestoneId = Input.Present(milestoneId),
                    labelIds = Input.Present(labelIds),
                    projectIds = Input.Present(projectIds)
                )
            )
        )
        .single()
}