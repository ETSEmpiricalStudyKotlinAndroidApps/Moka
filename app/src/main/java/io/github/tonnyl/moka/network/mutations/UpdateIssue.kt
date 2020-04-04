package io.github.tonnyl.moka.network.mutations

import com.apollographql.apollo.api.Input
import io.github.tonnyl.moka.mutations.UpdateIssueMutation
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.type.IssueState
import io.github.tonnyl.moka.type.UpdateIssueInput
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Updates an Issue.
 *
 * https://developer.github.com/v4/mutation/updateissue/
 *
 * @param id The ID of the Issue to modify.
 * @param title The title for the issue.
 * @param body The body for the issue description.
 * @param assigneeIds An array of Node IDs of users for this issue.
 * @param milestoneId The Node ID of the milestone for this issue.
 * @param labelIds An array of Node IDs of labels for this issue.
 * @param state The desired issue state.
 * @param projectIds An array of Node IDs for projects associated with this issue.
 */
suspend fun updateIssue(
    id: String,
    title: String? = null,
    body: String? = null,
    assigneeIds: List<String>? = null,
    milestoneId: String? = null,
    labelIds: List<String>? = null,
    state: IssueState? = null,
    projectIds: List<String>? = null
) = withContext(Dispatchers.IO) {
    runBlocking {
        GraphQLClient.apolloClient
            .mutate(
                UpdateIssueMutation(
                    UpdateIssueInput(
                        id,
                        Input.optional(title),
                        Input.optional(body),
                        Input.optional(assigneeIds),
                        Input.optional(milestoneId),
                        Input.optional(labelIds),
                        Input.optional(state),
                        Input.optional(projectIds)
                    )
                )
            )
            .execute()
    }
}