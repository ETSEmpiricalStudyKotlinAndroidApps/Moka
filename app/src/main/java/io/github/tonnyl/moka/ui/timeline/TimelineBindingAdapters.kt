package io.github.tonnyl.moka.ui.timeline

import android.content.res.Resources
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.util.textFuture

@OptIn(ExperimentalPagingApi::class)
@BindingAdapter(value = ["event", "viewModel"], requireAll = true)
fun AppCompatTextView.eventActionTextFuture(
    event: Event?,
    viewModel: TimelineViewModel?
) {
    event ?: return
    viewModel ?: return

    val actionBuilder = SpannableStringBuilder()

    appendActorSpan(event.actor, ProfileType.NOT_SPECIFIED, actionBuilder, resources, viewModel)

    when (event.type) {
        Event.WATCH_EVENT -> {
            // Currently, event.payload.action can only be "started".

            actionBuilder.append(context.getString(R.string.event_star))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github starred github/github
        }
        Event.CREATE_EVENT -> {
            // event.payload.refType -> The object that was created. Can be one of "repository", "branch", or "tag"
            val action = context.getString(
                when (event.payload?.refType) {
                    "repository" -> R.string.event_create_type_repository
                    "branch" -> R.string.event_create_type_branch
                    // including "tag"
                    else -> R.string.event_create_type_tag
                }
            )
            actionBuilder.append(context.getString(R.string.event_create, action))

            // event.payload.ref -> The git ref (or null if only a repository was created).
            event.payload?.ref?.let {
                actionBuilder.append(it)
                actionBuilder.append(context.getString(R.string.event_at))
            }

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created repository github/github
        }
        Event.COMMIT_COMMENT_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_comment_on_commit))

            event.payload?.commitComment?.commitId?.let {
                actionBuilder.append(it.substring(0, 8))
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github commented on commit ec7a2824 at github/github
        }
        Event.DOWNLOAD_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_download))

            actionBuilder.append(event.payload?.download?.name)

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github downloaded logo.jpe at github/github
        }
        Event.FOLLOW_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_follow))

            event.payload?.target?.let {
                appendActorSpan(it, ProfileType.USER, actionBuilder, resources, viewModel)
            }

            // final string example: github followed octocat
        }
        Event.FORK_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_fork))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            actionBuilder.append(context.getString(R.string.event_to))

            // event.payload.forkee -> The created repository.
            event.payload?.forkee?.let {
                appendRepositorySpan(it, true, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github forked actocat/Hello-World to github/Hello-World
        }
        Event.GIST_EVENT -> {
            // event.payload.action -> The action that was performed. Can be "create" or "update".
            val action = context.getString(
                when (event.payload?.action) {
                    "create" -> R.string.event_gist_action_created
                    // including "update".
                    else -> R.string.event_gist_action_updated
                }
            )

            actionBuilder.append(context.getString(R.string.event_gist, action))

            event.payload?.gist?.let {
                appendGistSpan(it, actionBuilder, resources, viewModel)
            }

            // final string example: github created Gist Hello World Examples
        }
        Event.GOLLUM_EVENT -> {
            // event.payload.pages[][action] -> The action that was performed on the page. Can be "created" or "edited".
            val action: String = context.getString(
                when (event.payload?.pages?.firstOrNull()?.action) {
                    "created" -> R.string.event_gollum_event_action_created
                    // including "edited"
                    else -> R.string.event_gollum_event_action_edited
                }
            )

            actionBuilder.append(context.getString(R.string.event_gollum_event, action))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github edit a wiki page at github/github
        }
        Event.ISSUE_COMMENT_EVENT -> {
            // event.payload.action -> The action that was performed on the comment.
            // Can be one of "created", "edited", or "deleted".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_issue_comment_action_created
                    "edited" -> R.string.event_issue_comment_action_edited
                    else -> R.string.event_issue_comment_action_deleted
                }
            )

            actionBuilder.append(context.getString(R.string.event_issue_comment, action))

            event.payload?.issue?.let {
                appendIssueNumberSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github commented on issue #1 at github/github
        }
        Event.ISSUES_EVENT -> {
            // event.payload.action -> The action that was performed. Can be one of "opened",
            // "edited", "deleted", "transferred", "pinned", "unpinned", "closed", "reopened",
            // "assigned", "unassigned", "labeled", "unlabeled", "milestoned", or "demilestoned".
            val action = context.getString(
                when (event.payload?.action) {
                    "opened" -> R.string.event_issue_action_opened
                    "edited" -> R.string.event_issue_action_edited
                    "deleted" -> R.string.event_issue_action_deleted
                    "transferred" -> R.string.event_issue_action_transferred
                    "pinned" -> R.string.event_issue_action_pinned
                    "unpinned" -> R.string.event_issue_action_unpinned
                    "closed" -> R.string.event_issue_action_closed
                    "reopened" -> R.string.event_issue_action_reopened
                    "assigned" -> R.string.event_issue_action_assigned
                    "unassigned" -> R.string.event_issue_action_unassigned
                    "labeled" -> R.string.event_issue_action_labeled
                    "unlabeled" -> R.string.event_issue_action_unlabeled
                    "milestoned" -> R.string.event_issue_action_milestoned
                    // including "demilestoned"
                    else -> R.string.event_issue_action_demilestoned
                }
            )

            actionBuilder.append(context.getString(R.string.event_issue, action))

            event.payload?.issue?.let {
                appendIssueNumberSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created issue #1 at github/github
        }
        Event.MEMBER_EVENT -> {
            val actionStringResId: Int
            val toOrFromStringResId: Int

            when (event.payload?.action) {
                "added" -> {
                    actionStringResId = R.string.event_added
                    toOrFromStringResId = R.string.event_to
                }
                "deleted" -> {
                    actionStringResId = R.string.event_deleted
                    toOrFromStringResId = R.string.event_from
                }
                // including "edited"
                else -> {
                    actionStringResId = R.string.event_edited
                    toOrFromStringResId = R.string.event_at
                }
            }

            actionBuilder.append(context.getString(actionStringResId))

            event.payload?.member?.let {
                appendActorSpan(it, ProfileType.USER, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(toOrFromStringResId))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github added octocat at github/github
        }
        Event.PUBLIC_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_publicized))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github open-sourced github/github
        }
        Event.PULL_REQUEST_EVENT -> {
            // event.payload.action -> The action that was performed. Can be one of "assigned",
            // "unassigned", "review_requested", "review_request_removed", "labeled", "unlabeled",
            // "opened", "edited", "closed", or "reopened".
            val action = context.getString(
                when (event.payload?.action) {
                    "assigned" -> R.string.event_pull_request_action_assigned
                    "unassigned" -> R.string.event_pull_request_action_unassigned
                    "review_requested" -> R.string.event_pull_request_action_review_requested
                    "review_request_removed" -> R.string.event_pull_request_action_review_request_removed
                    "labeled" -> R.string.event_pull_request_action_labeled
                    "unlabeled" -> R.string.event_pull_request_action_unlabeled
                    "opened" -> R.string.event_pull_request_action_opened
                    "edited" -> R.string.event_pull_request_action_edited
                    "closed" -> R.string.event_pull_request_action_closed
                    "reopened" -> R.string.event_pull_request_action_reopened
                    // including "synchronize"
                    else -> R.string.event_pull_request_action_synchronized
                }
            )

            actionBuilder.append(context.getString(R.string.event_pull_request, action))

            event.payload?.pullRequest?.let {
                appendPullRequestNumberSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github opened a pull request #1 at github/github
        }
        Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
            // event.payload.action -> The action that was performed on the comment.
            // Can be one of "created", "edited", or "deleted".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_pull_request_review_comment_action_created
                    "edited" -> R.string.event_pull_request_review_comment_action_edited
                    // including "deleted"
                    else -> R.string.event_pull_request_review_comment_action_deleted
                }
            )

            actionBuilder.append(
                context.getString(
                    R.string.event_pull_request_review_comment,
                    action
                )
            )

            event.payload?.pullRequest?.let {
                appendPullRequestNumberSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github commented on pull request #1 at github/github
        }
        Event.PULL_REQUEST_REVIEW_EVENT -> {
            // event.payload.action -> The action that was performed.
            // Can be "submitted", "edited", or "dismissed".
            val action = context.getString(
                when (event.payload?.action) {
                    "submitted" -> R.string.event_pull_request_review_action_submitted
                    "edited" -> R.string.event_pull_request_review_action_edited
                    // including "dismissed"
                    else -> R.string.event_pull_request_review_action_dismissed
                }
            )

            actionBuilder.append(
                context.getString(
                    R.string.event_pull_request_review,
                    action
                )
            )

            event.payload?.pullRequest?.let {
                appendPullRequestNumberSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github reviewed pull request #1 at github/github
        }
        Event.REPOSITORY_EVENT -> {
            // event.payload.action -> The action that was performed.
            // This can be one of "created", "deleted" (organization hooks only), "archived", "unarchived", "publicized", or "privatized".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_repository_action_created
                    "deleted" -> R.string.event_repository_action_deleted
                    "archived" -> R.string.event_repository_action_archived
                    "unarchived" -> R.string.event_repository_action_unarchived
                    "publicized" -> R.string.event_repository_action_publicized
                    // including "privatized"
                    else -> R.string.event_repository_action_privatized
                }
            )
            actionBuilder.append(context.getString(R.string.event_repository, action))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created repository github/github
        }
        Event.PUSH_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_push))

            // event.payload.ref -> The full Git ref that was pushed. Example: refs/heads/master.
            val ref = event.payload?.ref
            actionBuilder.append(
                when {
                    ref?.startsWith("refs/heads/") == true -> {
                        ref.substring(11)
                    }
                    ref?.startsWith("refs/tags/") == true -> {
                        ref.substring(10)
                    }
                    else -> {
                        ref
                    }
                }
            )

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github pushed 1 commit(s) to github/github
        }
        Event.TEAM_ADD_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_team_add))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github added repository github/github
        }
        Event.DELETE_EVENT -> {
            // event.payload.refType -> The object that was deleted. Can be "branch" or "tag".
            val deleteRefType = context.getString(
                when (event.payload?.refType) {
                    "branch" -> R.string.event_delete_type_branch
                    // including "tag"
                    else -> R.string.event_delete_type_tag
                }
            )
            actionBuilder.append(context.getString(R.string.event_delete, deleteRefType))

            // event.payload?.ref -> The full git ref.
            actionBuilder.append(event.payload?.ref)

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github deleted branch dev at github/github
        }
        Event.RELEASE_EVENT -> {
            // event.payload.action -> The action that was performed. Currently, can only be "published".

            actionBuilder.append(context.getString(R.string.event_release))

            event.payload?.release?.let {
                appendReleaseSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github released v1.1 at github/github
        }
        Event.FORK_APPLY_EVENT -> {
            actionBuilder.append(context.getString(R.string.event_fork_apply))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github applied a patch at github/github
        }
        Event.ORG_BLOCK_EVENT -> {
            // event.payload.action -> The action performed. Can be "blocked" or "unblocked".
            val blockType = when (event.payload?.action) {
                "blocked" -> R.string.event_org_block_type_block
                // including "unblocked"
                else -> R.string.event_org_block_type_unblock
            }
            actionBuilder.append(context.getString(blockType))

            // event.payload.blockedUser -> Information about the user that was blocked or unblocked.
            event.payload?.blockedUser?.let {
                appendActorSpan(it, ProfileType.USER, actionBuilder, resources, viewModel)
            }

            // final string example: github blocked octocat
        }
        Event.PROJECT_CARD_EVENT -> {
            // event.payload.action -> The action performed on the project card.
            // Can be "created", "updated", "moved", "converted", or "deleted".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_project_card_action_created
                    "updated" -> R.string.event_project_card_action_updated
                    "moved" -> R.string.event_project_card_action_moved
                    "converted" -> R.string.event_project_card_action_converted
                    // including "deleted"
                    else -> R.string.event_project_card_action_deleted
                }
            )
            actionBuilder.append(context.getString(R.string.event_project_card, action))

            actionBuilder.append(event.payload?.projectCard?.note ?: "")

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created a project card to-do at github/github
        }
        Event.PROJECT_COLUMN_EVENT -> {
            // event.payload.action -> The action that was performed on the project column.
            // Can be one of "created", "edited", "moved" or "deleted".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_project_column_created
                    "updated" -> R.string.event_project_column_updated
                    "moved" -> R.string.event_project_column_moved
                    // including "deleted"
                    else -> R.string.event_project_column_deleted
                }
            )
            actionBuilder.append(context.getString(R.string.event_project_column, action))

            event.payload?.projectColumn?.let {
                appendProjectColumnSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created a project column Small bugfixes at github/github
        }
        Event.ORGANIZATION_EVENT -> {
            val actionStringResId: Int
            val toOrFromId: Int

            // event.payload.action -> The action that was performed.
            // Can be one of: "member_added", "member_removed", or "member_invited".
            when (event.payload?.action) {
                "member_added" -> {
                    actionStringResId = R.string.event_organization_member_added
                    toOrFromId = R.string.event_to
                }
                "member_removed" -> {
                    actionStringResId = R.string.event_organization_member_removed
                    toOrFromId = R.string.event_from
                }
                // including "member_invited"
                else -> {
                    actionStringResId = R.string.event_organization_member_invited
                    toOrFromId = R.string.event_to
                }
            }
            actionBuilder.append(context.getString(actionStringResId))

            // event.payload.membership -> The membership between the user and the organization.
            // Not present when the action is "member_invited".
            event.payload?.membership?.user?.let {
                appendActorSpan(it, ProfileType.USER, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(toOrFromId))

            // event.payload.organization -> The organization in question.
            event.payload?.organization?.let {
                appendActorSpan(it, ProfileType.ORGANIZATION, actionBuilder, resources, viewModel)
            }

            // final string example: octocat invited tonnyl to github
        }
        Event.PROJECT_EVENT -> {
            // event.payload.action -> The action that was performed on the project.
            // Can be one of "created", "edited", "closed", "reopened", or "deleted".
            val action = context.getString(
                when (event.payload?.action) {
                    "created" -> R.string.event_project_created
                    "edited" -> R.string.event_project_edited
                    "closed" -> R.string.event_project_closed
                    "reopened" -> R.string.event_project_reopened
                    // including "deleted"
                    else -> R.string.event_project_deleted
                }
            )

            actionBuilder.append(action)

            event.payload?.project?.let {
                appendProjectSpan(it, actionBuilder, resources, viewModel)
            }

            actionBuilder.append(context.getString(R.string.event_at))

            event.repo?.let {
                appendRepositorySpan(it, false, actionBuilder, resources, viewModel, event.org)
            }

            // final string example: github created a project Space 2.0 at github/github
        }
    }

    textFuture(actionBuilder)
}

@BindingAdapter("eventContentVisibilityOrTextFuture")
fun AppCompatTextView.eventContentVisibilityOrTextFuture(event: Event?) {
    val content: String? = when (event?.type) {
        Event.COMMIT_COMMENT_EVENT -> {
            event.payload?.commitComment?.body
        }
        Event.ISSUE_COMMENT_EVENT -> {
            event.payload?.comment?.body
        }
        Event.ISSUES_EVENT -> {
            event.payload?.issue?.title
        }
        Event.PULL_REQUEST_EVENT -> {
            event.payload?.pullRequest?.title
        }
        Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
            event.payload?.comment?.body
        }
        Event.PULL_REQUEST_REVIEW_EVENT -> {
            event.payload?.review?.body
        }
        else -> {
            null
        }
    }

    content?.let {
        visibility = View.VISIBLE
        textFuture(content)
    } ?: run {
        visibility = View.GONE
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendActorSpan(
    actor: EventActor,
    type: ProfileType,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    actionBuilder.run {
        append(actor.login)
        setSpan(
            ActorClickableSpan(actor, type, resources, viewModel),
            length - actor.login.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendRepositorySpan(
    repository: EventRepository,
    fullName: Boolean = false,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel,
    org: EventOrg?
) {
    actionBuilder.run {
        val repoFullName = if (fullName) repository.fullName else repository.name
        repoFullName?.let {
            append(it)
            setSpan(
                RepositoryClickableSpan(it, org, resources, viewModel),
                length - (if (fullName) {
                    repository.fullName?.length ?: 0
                } else {
                    repository.name.length
                }),
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendIssueNumberSpan(
    issue: EventIssue,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    val text = "#${issue.number}"
    actionBuilder.run {
        append(text)
        setSpan(
            IssueNumberClickableSpan(issue, resources, viewModel),
            length - text.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendPullRequestNumberSpan(
    pullRequest: EventPullRequest,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    val text = "#${pullRequest.number}"
    actionBuilder.run {
        append(text)
        setSpan(
            PullRequestNumberClickableSpan(pullRequest, resources, viewModel),
            length - text.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendProjectSpan(
    project: EventProject,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    actionBuilder.run {
        append(project.name)
        setSpan(
            ProjectClickableSpan(project, resources, viewModel),
            length - project.name.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendProjectColumnSpan(
    projectColumn: EventProjectColumn,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    actionBuilder.run {
        append(projectColumn.name)
        setSpan(
            ProjectColumnClickableSpan(projectColumn, resources, viewModel),
            length - projectColumn.name.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@ExperimentalPagingApi
private fun appendReleaseSpan(
    release: EventRelease,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    actionBuilder.run {
        append(release.tagName)
        setSpan(
            ReleaseClickableSpan(release, resources, viewModel),
            length - release.tagName.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private fun appendGistSpan(
    gist: Gist,
    actionBuilder: SpannableStringBuilder,
    resources: Resources,
    viewModel: TimelineViewModel
) {
    actionBuilder.run {
        append(gist.description)
        setSpan(
            GistClickableSpan(gist, resources, viewModel),
            length - gist.description.length,
            length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}