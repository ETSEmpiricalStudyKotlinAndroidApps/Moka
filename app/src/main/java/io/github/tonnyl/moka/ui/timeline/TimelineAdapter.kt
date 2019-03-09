package io.github.tonnyl.moka.ui.timeline

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.GlideApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import kotlinx.android.synthetic.main.item_event.view.*

class TimelineAdapter(val context: Context) : PagedListAdapter<Event, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {

            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean = oldItem == newItem

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        if (holder is EventViewHolder) {
            holder.bind(item, position)
        }
    }

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: Event, position: Int) {
            with(itemView) {
                if (position == 0) {
                    event_overline.visibility = View.VISIBLE
                    event_space_between_overline_and_avatar.visibility = View.VISIBLE
                } else {
                    event_overline.visibility = View.GONE
                    event_space_between_overline_and_avatar.visibility = View.GONE
                }

                GlideApp.with(context)
                        .load(data.actor.avatarUrl)
                        .circleCrop()
                        .into(event_user_avatar)

                var content = ""
                val actionBuilder = SpannableStringBuilder(data.actor.login)

                fun setPrimaryTextSpan(text: String) {
                    actionBuilder.append(text)
                    actionBuilder.setSpan(ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.colorTextPrimary, null)), actionBuilder.length - text.length + 1, actionBuilder.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                when (data.type) {
                    Event.WATCH_EVENT -> {
                        // Currently, data.payload.action can only be "started".

                        setPrimaryTextSpan(context.getString(R.string.event_star))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github starred github/github
                    }
                    Event.CREATE_EVENT -> {
                        // data.payload.refType -> The object that was created. Can be one of "repository", "branch", or "tag"
                        val action = context.getString(when (data.payload.refType) {
                            "repository" -> R.string.event_create_type_repository
                            "branch" -> R.string.event_create_type_branch
                            // including "tag"
                            else -> R.string.event_create_type_tag
                        })
                        setPrimaryTextSpan(context.getString(R.string.event_create, action))

                        // data.payload.ref -> The git ref (or null if only a repository was created).
                        data.payload.ref?.let {
                            actionBuilder.append(it)
                            setPrimaryTextSpan(context.getString(R.string.event_at))
                        }

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created repository github/github
                    }
                    Event.COMMIT_COMMENT_EVENT -> {
                        data.payload.commitComment?.body?.let {
                            content = it
                        }

                        setPrimaryTextSpan(context.getString(R.string.event_comment_on_commit))

                        data.payload.commitComment?.commitId?.let {
                            actionBuilder.append(it.substring(0, 8))
                        }

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github commented on commit ec7a2824 at github/github
                    }
                    Event.DOWNLOAD_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_download))

                        actionBuilder.append(data.payload.download?.name)

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github downloaded logo.jpe at github/github
                    }
                    Event.FOLLOW_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_follow))

                        actionBuilder.append(data.payload.target?.login)

                        // final string example: github followed octocat
                    }
                    Event.FORK_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_fork))

                        data.repo?.name?.let {
                            actionBuilder.append(it)
                        }

                        setPrimaryTextSpan(context.getString(R.string.event_to))

                        // data.payload.forkee -> The created repository.
                        actionBuilder.append(data.payload.forkee?.fullName)

                        // final string example: github forked actocat/Hello-World to github/Hello-World
                    }
                    Event.GIST_EVENT -> {
                        // data.payload.action -> The action that was performed. Can be "create" or "update".
                        val action = context.getString(when (data.payload.action) {
                            "create" -> R.string.event_gist_action_created
                            // including "update".
                            else -> R.string.event_gist_action_updated
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_gist, action))

                        actionBuilder.append(data.payload.gist?.description)

                        // final string example: github created Gist Hello World Examples
                    }
                    Event.GOLLUM_EVENT -> {
                        // data.payload.pages[][action] -> The action that was performed on the page. Can be "created" or "edited".
                        val action: String = context.getString(when (data.payload.pages?.firstOrNull()?.action) {
                            "created" -> R.string.event_gollum_event_action_created
                            // including "edited"
                            else -> R.string.event_gollum_event_action_edited
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_gollum_event, action))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github edit a wiki page at github/github
                    }
                    Event.ISSUE_COMMENT_EVENT -> {
                        data.payload.comment?.body?.let {
                            content = it
                        }

                        // data.payload.action -> The action that was performed on the comment.
                        // Can be one of "created", "edited", or "deleted".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_issue_comment_action_created
                            "edited" -> R.string.event_issue_comment_action_edited
                            else -> R.string.event_issue_comment_action_deleted
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_issue_comment, action))

                        actionBuilder.append("#${data.payload.issue?.number}")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github commented on issue #1 at github/github
                    }
                    Event.ISSUES_EVENT -> {
                        data.payload.issue?.title?.let {
                            content = it
                        }

                        // data.payload.action -> The action that was performed. Can be one of "opened",
                        // "edited", "deleted", "transferred", "pinned", "unpinned", "closed", "reopened",
                        // "assigned", "unassigned", "labeled", "unlabeled", "milestoned", or "demilestoned".
                        val action = context.getString(when (data.payload.action) {
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
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_issue, action))

                        actionBuilder.append("#${data.payload.issue?.number}")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created issue #1 at github/github
                    }
                    Event.MEMBER_EVENT -> {
                        val actionStringResId: Int
                        val toOrFromStringResId: Int

                        when (data.payload.action) {
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

                        setPrimaryTextSpan(context.getString(actionStringResId))

                        actionBuilder.append(data.payload.member?.login ?: "")

                        setPrimaryTextSpan(context.getString(toOrFromStringResId))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github added octocat at github/github
                    }
                    Event.PUBLIC_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_publicized))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github open-sourced github/github
                    }
                    Event.PULL_REQUEST_EVENT -> {
                        data.payload.pullRequest?.title?.let {
                            content = it
                        }

                        // data.payload.action -> The action that was performed. Can be one of "assigned",
                        // "unassigned", "review_requested", "review_request_removed", "labeled", "unlabeled",
                        // "opened", "edited", "closed", or "reopened".
                        val action = context.getString(when (data.payload.action) {
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
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_pull_request, action))

                        actionBuilder.append("#${data.payload.pullRequest?.number}")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github opened a pull request #1 at github/github
                    }
                    Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
                        data.payload.comment?.body?.let {
                            content = it
                        }

                        // data.payload.action -> The action that was performed on the comment.
                        // Can be one of "created", "edited", or "deleted".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_pull_request_review_comment_action_created
                            "edited" -> R.string.event_pull_request_review_comment_action_edited
                            // including "deleted"
                            else -> R.string.event_pull_request_review_comment_action_deleted
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_pull_request_review_comment, action))

                        actionBuilder.append("#${data.payload.pullRequest?.number}")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github commented on pull request #1 at github/github
                    }
                    Event.PULL_REQUEST_REVIEW_EVENT -> {
                        data.payload.review?.body?.let {
                            content = it
                        }

                        // data.payload.action -> The action that was performed.
                        // Can be "submitted", "edited", or "dismissed".
                        val action = context.getString(when (data.payload.action) {
                            "submitted" -> R.string.event_pull_request_review_action_submitted
                            "edited" -> R.string.event_pull_request_review_action_edited
                            // including "dismissed"
                            else -> R.string.event_pull_request_review_action_dismissed
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_pull_request_review, action))

                        actionBuilder.append("#${data.payload.pullRequest?.number}")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github reviewed pull request #1 at github/github
                    }
                    Event.REPOSITORY_EVENT -> {
                        // data.payload.action -> The action that was performed.
                        // This can be one of "created", "deleted" (organization hooks only), "archived", "unarchived", "publicized", or "privatized".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_repository_action_created
                            "deleted" -> R.string.event_repository_action_deleted
                            "archived" -> R.string.event_repository_action_archived
                            "unarchived" -> R.string.event_repository_action_unarchived
                            "publicized" -> R.string.event_repository_action_publicized
                            // including "privatized"
                            else -> R.string.event_repository_action_privatized
                        })
                        setPrimaryTextSpan(context.getString(R.string.event_repository, action))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created repository github/github
                    }
                    Event.PUSH_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_push))

                        // data.payload.ref -> The full Git ref that was pushed. Example: refs/heads/master.
                        actionBuilder.append(when {
                            data.payload.ref?.startsWith("refs/heads/") == true -> {
                                data.payload.ref.substring(11)
                            }
                            data.payload.ref?.startsWith("refs/tags/") == true -> {
                                data.payload.ref.substring(10)
                            }
                            else -> {
                                data.payload.ref
                            }
                        })

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github pushed 1 commit(s) to github/github
                    }
                    Event.TEAM_ADD_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_team_add))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github added repository github/github
                    }
                    Event.DELETE_EVENT -> {
                        // data.payload.refType -> The object that was deleted. Can be "branch" or "tag".
                        val deleteRefType = context.getString(when (data.payload.refType) {
                            "branch" -> R.string.event_delete_type_branch
                            // including "tag"
                            else -> R.string.event_delete_type_tag
                        })
                        setPrimaryTextSpan(context.getString(R.string.event_delete, deleteRefType))

                        // data.payload.ref -> The full git ref.
                        actionBuilder.append(data.payload.ref)

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github deleted branch dev at github/github
                    }
                    Event.RELEASE_EVENT -> {
                        // data.payload.action -> The action that was performed. Currently, can only be "published".

                        setPrimaryTextSpan(context.getString(R.string.event_release))

                        actionBuilder.append(data.payload.release?.name ?: "")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github released v1.1 at github/github
                    }
                    Event.FORK_APPLY_EVENT -> {
                        setPrimaryTextSpan(context.getString(R.string.event_fork_apply))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github applied a patch at github/github
                    }
                    Event.ORG_BLOCK_EVENT -> {
                        // data.payload.action -> The action performed. Can be "blocked" or "unblocked".
                        val blockType = when (data.payload.action) {
                            "blocked" -> R.string.event_org_block_type_block
                            // including "unblocked"
                            else -> R.string.event_org_block_type_unblock
                        }
                        setPrimaryTextSpan(context.getString(blockType))

                        // data.payload.blockedUser -> Information about the user that was blocked or unblocked.
                        actionBuilder.append(data.payload.blockedUser?.login)

                        // final string example: github blocked octocat
                    }
                    Event.PROJECT_CARD_EVENT -> {
                        // data.payload.action -> The action performed on the project card.
                        // Can be "created", "updated", "moved", "converted", or "deleted".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_project_card_action_created
                            "updated" -> R.string.event_project_card_action_updated
                            "moved" -> R.string.event_project_card_action_moved
                            "converted" -> R.string.event_project_card_action_converted
                            // including "deleted"
                            else -> R.string.event_project_card_action_deleted
                        })
                        setPrimaryTextSpan(context.getString(R.string.event_project_card, action))

                        actionBuilder.append(data.payload.projectCard?.note ?: "")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created a project card to-do at github/github
                    }
                    Event.PROJECT_COLUMN_EVENT -> {
                        // data.payload.action -> The action that was performed on the project column.
                        // Can be one of "created", "edited", "moved" or "deleted".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_project_column_created
                            "updated" -> R.string.event_project_column_updated
                            "moved" -> R.string.event_project_column_moved
                            // including "deleted"
                            else -> R.string.event_project_column_deleted
                        })
                        setPrimaryTextSpan(context.getString(R.string.event_project_column, action))

                        actionBuilder.append(data.payload.projectColumn?.name ?: "")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created a project column Small bugfixes at github/github
                    }
                    Event.ORGANIZATION_EVENT -> {
                        val actionStringResId: Int
                        val toOrFromId: Int

                        // data.payload.action -> The action that was performed.
                        // Can be one of: "member_added", "member_removed", or "member_invited".
                        when (data.payload.action) {
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
                        setPrimaryTextSpan(context.getString(actionStringResId))

                        // data.payload.membership -> The membership between the user and the organization.
                        // Not present when the action is "member_invited".
                        actionBuilder.append(data.payload.membership?.user?.login)

                        setPrimaryTextSpan(context.getString(toOrFromId))

                        // data.payload.organization -> The organization in question.
                        actionBuilder.append(data.payload.organization?.login)

                        // final string example: octocat invited tonnyl to github
                    }
                    Event.PROJECT_EVENT -> {
                        // data.payload.action -> The action that was performed on the project.
                        // Can be one of "created", "edited", "closed", "reopened", or "deleted".
                        val action = context.getString(when (data.payload.action) {
                            "created" -> R.string.event_project_created
                            "edited" -> R.string.event_project_edited
                            "closed" -> R.string.event_project_closed
                            "reopened" -> R.string.event_project_reopened
                            // including "deleted"
                            else -> R.string.event_project_deleted
                        })

                        setPrimaryTextSpan(action)

                        actionBuilder.append(data.payload.project?.name ?: "")

                        setPrimaryTextSpan(context.getString(R.string.event_at))

                        actionBuilder.append(data.repo?.name)

                        // final string example: github created a project Space 2.0 at github/github
                    }
                }

                event_action.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        actionBuilder,
                        TextViewCompat.getTextMetricsParams(event_action),
                        null
                ))

                if (content.isEmpty()) {
                    event_content.visibility = View.GONE
                } else {
                    event_content.setTextFuture(PrecomputedTextCompat.getTextFuture(
                            content,
                            TextViewCompat.getTextMetricsParams(event_content),
                            null
                    ))
                    event_content.visibility = View.VISIBLE
                }

                event_create_time.setTextFuture(PrecomputedTextCompat.getTextFuture(
                        DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS),
                        TextViewCompat.getTextMetricsParams(event_create_time),
                        null
                ))
            }
        }

    }

}