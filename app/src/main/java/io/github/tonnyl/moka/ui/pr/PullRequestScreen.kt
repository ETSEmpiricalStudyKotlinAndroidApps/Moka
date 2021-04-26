package io.github.tonnyl.moka.ui.pr

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.type.LockReason
import io.github.tonnyl.moka.type.PullRequestReviewState
import io.github.tonnyl.moka.ui.issue.IssueOrPullRequestHeader
import io.github.tonnyl.moka.ui.issue.IssuePullRequestEventData
import io.github.tonnyl.moka.ui.issue.IssueTimelineCommentItem
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.PullRequestTimelineItemProvider
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.util.toShortOid
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import kotlinx.datetime.Instant

@Composable
fun PullRequestScreen(
    navController: NavController,
    owner: String,
    name: String,
    number: Int
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<PullRequestViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            owner = owner,
            name = name,
            number = number
        )
    )

    val pullRequest by viewModel.pullRequest.observeAsState()
    val prTimelineItemsPager = remember {
        viewModel.prTimelineFlow
    }
    val prTimelineItems = prTimelineItemsPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = prTimelineItems.loadState.refresh is LoadState.Loading),
            onRefresh = prTimelineItems::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance,
                    scale = true,
                    contentColor = MaterialTheme.colors.secondary
                )
            }
        ) {
            when {
                prTimelineItems.loadState.refresh is LoadState.NotLoading
                        && prTimelineItems.loadState.append is LoadState.NotLoading
                        && prTimelineItems.loadState.prepend is LoadState.NotLoading
                        && prTimelineItems.itemCount == 0 -> {

                }
                prTimelineItems.loadState.refresh is LoadState.NotLoading
                        && prTimelineItems.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                prTimelineItems.loadState.refresh is LoadState.Error
                        && prTimelineItems.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    PullRequestScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        pullRequest = pullRequest,
                        owner = owner,
                        name = name,
                        timelineItems = prTimelineItems
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.pull_request)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_arrow_back_24)
                        )
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun PullRequestScreenContent(
    contentTopPadding: Dp,
    owner: String,
    name: String,
    pullRequest: PullRequest?,
    timelineItems: LazyPagingItems<PullRequestTimelineItem>
) {
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        if (pullRequest != null) {
            item {
                IssueOrPullRequestHeader(
                    repoOwner = owner,
                    repoName = name,
                    number = pullRequest.number,
                    title = pullRequest.title,
                    caption = stringResource(
                        id = R.string.issue_pr_info_format,
                        stringResource(
                            id = when {
                                pullRequest.closed -> {
                                    R.string.issue_pr_status_closed
                                }
                                pullRequest.merged -> {
                                    R.string.issue_pr_status_merged
                                }
                                else -> {
                                    R.string.issue_pr_status_open
                                }
                            }
                        ),
                        stringResource(
                            id = R.string.issue_pr_created_by,
                            pullRequest.author?.login ?: "ghost"
                        ),
                        DateUtils.getRelativeTimeSpanString(
                            pullRequest.createdAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ) as String
                    ),
                    avatarUrl = pullRequest.author?.avatarUrl,
                    viewerCanReact = pullRequest.viewerCanReact,
                    reactionGroups = pullRequest.reactionGroups,
                    authorLogin = pullRequest.author?.login,
                    authorAssociation = pullRequest.authorAssociation,
                    displayHtml = pullRequest.bodyHTML.takeIf { it.isNotEmpty() }
                        ?: stringResource(id = R.string.no_description_provided),
                    commentCreatedAt = pullRequest.createdAt
                )
            }
        }

        item {
            ItemLoadingState(loadState = timelineItems.loadState.prepend)
        }

        itemsIndexed(lazyPagingItems = timelineItems) { _, item ->
            if (item != null) {
                if (item is IssueComment) {
                    IssueTimelineCommentItem(
                        avatarUrl = item.author?.avatarUrl,
                        viewerCanReact = item.viewerCanReact,
                        reactionGroups = item.reactionGroups,
                        authorLogin = item.author?.login,
                        authorAssociation = item.authorAssociation,
                        displayHtml = item.displayHtml,
                        commentCreatedAt = item.createdAt
                    )
                } else {
                    ItemPullRequestTimelineEvent(timelineItem = item)
                }
            }
        }

        item {
            ItemLoadingState(loadState = timelineItems.loadState.append)
        }
    }
}

@Composable
private fun ItemPullRequestTimelineEvent(
    timelineItem: PullRequestTimelineItem
) {
    val data = eventData(timelineItem) ?: return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                contentDescription = stringResource(id = R.string.issue_pr_timeline_event_image_content_description),
                painter = painterResource(id = data.iconResId),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
                    .background(color = data.backgroundColor)
                    .padding(all = ContentPaddingMediumSize)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Image(
                painter = rememberCoilPainter(
                    request = data.avatarUri,
                    requestBuilder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                modifier = Modifier
                    .size(size = IssueTimelineEventAuthorAvatarSize)
                    .clip(shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Text(
                text = data.login,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(weight = 1f)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        data.createdAt.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ) as String,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        Text(text = data.content)
    }
}

@Composable
private fun eventData(event: PullRequestTimelineItem): IssuePullRequestEventData? {
    val iconResId: Int?
    val backgroundColor: Color?
    val avatarUri: String?
    val login: String?
    val createdAt: Instant?
    val content: AnnotatedString?

    when (event) {
        is AddedToProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_added_to_project))
        }
        is AssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                if (event.actor?.login == event.assigneeLogin) {
                    append(text = stringResource(id = R.string.issue_timeline_assigned_event_self_assigned))
                } else {
                    append(text = stringResource(id = R.string.issue_timeline_assigned_event_assigned_someone))
                    append(
                        text = AnnotatedString(
                            text = event.assigneeLogin ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        is BaseRefChangedEvent -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.pull_request_base_ref_changed))
        }
        is BaseRefForcePushedEvent -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_force_pushed_branch,
                    event.ref?.name ?: stringResource(id = R.string.pull_request_unknown),
                    event.beforeCommit?.oid?.toShortOid()
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.afterCommit?.oid?.toShortOid()
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        is ClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundColor = MaterialTheme.colors.error
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_closed_event_closed))
        }
        is ConvertedNoteToIssueEvent -> {
            iconResId = R.drawable.ic_issue_open_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_converted_note_to_issue))
        }
        is CrossReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_cross_referenced_event_cross_referenced))
                append(
                    AnnotatedString(
                        text = event.issue?.title ?: event.pullRequest?.title ?: "",
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
                append(
                    stringResource(
                        R.string.issue_timeline_issue_pr_number_with_blank_prefix,
                        event.issue?.number ?: event.pullRequest?.number ?: 0
                    )
                )
            }
        }
        is DemilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_demilestoned_event_demilestoned))
                append(event.milestoneTitle)
                append(stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        is DeployedEvent -> {
            iconResId = R.drawable.ic_rocket_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_deployed_to_branch,
                    event.deploymentEnvironment
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        is DeploymentEnvironmentChangedEvent -> {
            iconResId = R.drawable.ic_rocket_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_deployed_to_branch,
                    event.deployment?.environment
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        is HeadRefDeletedEvent -> {
            iconResId = R.drawable.ic_delete_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_deleted_branch,
                    event.headRefName
                )
            )
        }
        is HeadRefForcePushedEvent -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_force_pushed_branch,
                    event.ref?.name ?: stringResource(id = R.string.pull_request_unknown),
                    event.beforeCommitOid?.toShortOid()
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.afterCommitOid?.toShortOid()
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        is HeadRefRestoredEvent -> {
            iconResId = R.drawable.ic_restore_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_restore_branch,
                    event.pullRequestHeadRefName
                )
            )
        }
        is LabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_labeled_event_labeled))
                event.label?.color?.toColor()?.let {
                    Color(it)
                }?.let { bgColor ->
                    append(
                        AnnotatedString(
                            text = event.label.name,
                            spanStyle = SpanStyle(
                                background = bgColor,
                                color = MaterialTheme.colors.onBackground
                            )
                        )
                    )
                }
                append(text = stringResource(id = R.string.issue_timeline_label))
            }
        }
        is LockedEvent -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_1))
                append(
                    text = stringResource(
                        id = when (event.lockReason) {
                            LockReason.OFF_TOPIC -> {
                                R.string.issue_lock_reason_off_topic
                            }
                            LockReason.RESOLVED -> {
                                R.string.issue_lock_reason_resolved
                            }
                            LockReason.SPAM -> {
                                R.string.issue_lock_reason_spam
                            }
                            LockReason.TOO_HEATED -> {
                                R.string.issue_lock_reason_too_heated
                            }
                            is LockReason.UNKNOWN__,
                            null -> {
                                R.string.issue_lock_reason_unknown
                            }
                        }
                    )
                )
                append(stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_2))
            }
        }
        is MarkedAsDuplicateEvent -> {
            iconResId = R.drawable.ic_copy_24dp
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_marked_as_duplicate))
        }
        is MergedEvent -> {
            iconResId = R.drawable.ic_pr_merged
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_merged_commit,
                    event.commitOid.toShortOid(),
                    event.mergeRefName
                )
            )
        }
        is MilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_milestoned_event_milestoned))
                append(
                    text = AnnotatedString(
                        text = event.milestoneTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
                append(text = stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        is MovedColumnsInProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_moved_columns_in_project)
            )
        }
        is PinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_pinned))
        }
        is PullRequestCommit -> {
            iconResId = R.drawable.ic_commit_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.commit?.author?.avatarUrl ?: event.commit?.committer?.avatarUrl
            login = event.commit?.author?.user?.login
                ?: event.commit?.author?.name
                        ?: event.commit?.committer?.user?.login
                        ?: event.commit?.committer?.name
                        ?: "ghost"
            createdAt = null
            content = AnnotatedString(
                text = event.commit?.message
                    ?: stringResource(id = R.string.no_description_provided)
            )
        }
        is PullRequestReview -> {
            avatarUri = event.author?.avatarUrl
            createdAt = event.createdAt
            login = event.author?.login
            val stateString: String

            when (event.state) {
                PullRequestReviewState.APPROVED -> {
                    iconResId = R.drawable.ic_check_24
                    backgroundColor = issuePrGreen
                    stateString =
                        stringResource(id = R.string.pull_request_review_approved_changes)
                }
                PullRequestReviewState.CHANGES_REQUESTED -> {
                    iconResId = R.drawable.ic_close_24
                    backgroundColor = MaterialTheme.colors.primary
                    stateString =
                        stringResource(id = R.string.pull_request_review_request_changes)
                }
                // PENDING, DISMISSED, COMMENTED
                else -> {
                    iconResId = R.drawable.ic_eye_24
                    backgroundColor = MaterialTheme.colors.primary
                    stateString = stringResource(id = R.string.pull_request_reviewed)
                }
            }

            content = buildAnnotatedString {
                append(text = stateString)
                append(text = stringResource(id = R.string.pull_request_and_left_a_comment))
                append(
                    text = AnnotatedString(
                        text = event.body,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
            }
        }
        is ReadyForReviewEvent -> {
            iconResId = R.drawable.ic_eye_24
            backgroundColor = issuePrGreen
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.pull_request_marked_as_ready_for_review))
        }
        is ReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_referenced_event_referenced))
                append(text = event.issue?.title ?: event.pullRequest?.title ?: "")
                append(
                    text = stringResource(
                        id = R.string.issue_timeline_issue_pr_number_with_blank_prefix,
                        event.issue?.number ?: event.pullRequest?.number ?: 0
                    )
                )
            }
        }
        is RemovedFromProjectEvent -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_removed_from_project)
            )
        }
        is RenamedTitleEvent -> {
            iconResId = R.drawable.ic_edit_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_renamed_title_event_change_title_part_1))
                append(
                    AnnotatedString(
                        text = event.previousTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                )
                append(text = stringResource(id = R.string.issue_timeline_renamed_title_event_change_title_part_2))
                append(
                    AnnotatedString(
                        text = event.currentTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
            }
        }
        is ReopenedEvent -> {
            iconResId = R.drawable.ic_dot_24
            backgroundColor = issuePrGreen
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_reopened_event_reopened)
            )
        }
        is ReviewDismissedEvent -> {
            iconResId = R.drawable.ic_close_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_part_1))
                append(text = event.review?.author?.login ?: "ghost")
                append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_part_2))

                if (!event.dismissalMessage.isNullOrEmpty()) {
                    append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_and_left_a_comment))
                    append(text = event.dismissalMessage)
                }
            }
        }
        is ReviewRequestRemovedEvent -> {
            iconResId = R.drawable.ic_close_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.pull_request_removed_someones_review_request_part_1))
                append(
                    text = event.requestedReviewerUser?.login
                        ?: event.requestedReviewerTeam?.combinedSlug
                        ?: event.requestedReviewerMannequin?.login
                        ?: "ghost"
                )
                append(text = stringResource(id = R.string.pull_request_removed_someones_review_request_part_2))
            }
        }
        is ReviewRequestedEvent -> {
            iconResId = R.drawable.ic_eye_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = if (event.actor?.login == event.requestedReviewerLogin) {
                AnnotatedString(text = stringResource(id = R.string.pull_request_self_requested_a_review))
            } else {
                buildAnnotatedString {
                    append(text = stringResource(id = R.string.pull_request_requested_review_from))
                    append(
                        text = AnnotatedString(
                            text = event.requestedReviewerLogin ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        is UnassignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                if (event.actor?.login == event.assignee.assigneeLogin) {
                    append(stringResource(id = R.string.issue_timeline_unassigned_event_self_unassigned))
                } else {
                    append(stringResource(id = R.string.issue_timeline_unassigned_event_unassigned_someone))
                    append(
                        AnnotatedString(
                            text = event.assignee.assigneeLogin ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        is UnlabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_labeled_event_labeled))
                event.label?.color?.toColor()?.let {
                    Color(it)
                }?.let { bgColor ->
                    append(
                        AnnotatedString(
                            text = event.label.name,
                            spanStyle = SpanStyle(
                                background = bgColor,
                                color = MaterialTheme.colors.onBackground
                            )
                        )
                    )
                }
                append(stringResource(id = R.string.issue_timeline_label))
            }
        }
        is UnlockedEvent -> {
            iconResId = R.drawable.ic_key_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_unlocked_event_unlocked))
        }
        is UnpinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.actor?.avatarUrl
            login = event.actor?.login
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_unpinned))
        }
        else -> {
            iconResId = null
            backgroundColor = null
            avatarUri = null
            login = null
            createdAt = null
            content = null
        }
    }

    return if (iconResId == null
        || backgroundColor == null
        || createdAt == null
        || content == null
    ) {
        null
    } else {
        IssuePullRequestEventData(
            iconResId,
            backgroundColor,
            avatarUri,
            login ?: "ghost",
            createdAt,
            content
        )
    }
}

@Preview(name = "PullRequestTimelineEventItemPreview", showBackground = true)
@Composable
private fun PullRequestTimelineEventItemPreview(
    @PreviewParameter(
        PullRequestTimelineItemProvider::class,
        limit = 1
    )
    pullRequest: PullRequestTimelineItem
) {
    ItemPullRequestTimelineEvent(timelineItem = pullRequest)
}