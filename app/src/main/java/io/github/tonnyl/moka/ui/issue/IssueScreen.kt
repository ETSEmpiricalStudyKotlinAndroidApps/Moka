package io.github.tonnyl.moka.ui.issue

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.data.item.*
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.type.CommentAuthorAssociation
import io.github.tonnyl.moka.type.LockReason
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.reaction.AddReactionDialogScreen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.IssueTimelineEventProvider
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import kotlinx.datetime.Instant

data class IssuePullRequestEventData(
    val iconResId: Int,
    val backgroundColor: Color,
    val avatarUri: String?,
    val login: String,
    val createdAt: Instant,
    val content: AnnotatedString
)

@Composable
fun IssueScreen(
    navController: NavController,
    owner: String,
    name: String,
    number: Int
) {
    val viewModel = viewModel<IssueViewModel>(
        factory = ViewModelFactory(owner = owner, name = name, number = number)
    )

    val issueTimelineItemsPager = remember {
        viewModel.issueTimelineFlow
    }
    val issueTimelineItems = issueTimelineItemsPager.collectAsLazyPagingItems()

    val issue by viewModel.issueLiveData.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SwipeToRefreshLayout(
            refreshingState = issueTimelineItems.loadState.refresh is LoadState.Loading,
            onRefresh = issueTimelineItems::refresh,
            refreshIndicator = {
                Surface(
                    elevation = 10.dp,
                    shape = CircleShape
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(size = 36.dp)
                            .padding(all = ContentPaddingSmallSize)
                    )
                }
            }
        ) {
            when {
                issueTimelineItems.loadState.refresh is LoadState.NotLoading
                        && issueTimelineItems.loadState.append is LoadState.NotLoading
                        && issueTimelineItems.loadState.prepend is LoadState.NotLoading
                        && issueTimelineItems.itemCount == 0 -> {

                }
                issueTimelineItems.loadState.refresh is LoadState.NotLoading
                        && issueTimelineItems.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                issueTimelineItems.loadState.refresh is LoadState.Error
                        && issueTimelineItems.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    IssueScreenContent(
                        topAppBarSize = topAppBarSize,
                        owner = owner,
                        name = name,
                        issue = issue,
                        timelineItems = issueTimelineItems
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.issue)) },
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
private fun IssueScreenContent(
    topAppBarSize: Int,
    owner: String,
    name: String,
    issue: Issue?,
    timelineItems: LazyPagingItems<IssueTimelineItem>
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        if (issue != null) {
            item {
                IssueOrPullRequestHeader(
                    repoOwner = owner,
                    repoName = name,
                    number = issue.number,
                    title = issue.title,
                    caption = stringResource(
                        id = R.string.issue_pr_info_format,
                        stringResource(
                            id = if (issue.closed) {
                                R.string.issue_pr_status_closed
                            } else {
                                R.string.issue_pr_status_open
                            }
                        ),
                        stringResource(
                            id = R.string.issue_pr_created_by,
                            issue.author?.login ?: "ghost"
                        ),
                        DateUtils.getRelativeTimeSpanString(
                            issue.createdAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ) as String
                    ),
                    avatarUrl = issue.author?.avatarUrl,
                    viewerCanReact = issue.viewerCanReact,
                    reactionGroups = issue.reactionGroups,
                    authorLogin = issue.author?.login,
                    authorAssociation = issue.authorAssociation,
                    displayHtml = issue.bodyHTML.takeIf { it.isNotEmpty() }
                        ?: stringResource(id = R.string.no_description_provided),
                    commentCreatedAt = issue.createdAt
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
                    ItemIssueTimelineEvent(event = item)
                }
            }
        }

        item {
            ItemLoadingState(loadState = timelineItems.loadState.append)
        }
    }
}

@Composable
private fun ItemIssueTimelineEvent(event: IssueTimelineItem) {
    val data = eventData(event) ?: return
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
            CoilImage(
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                request = createAvatarLoadRequest(url = data.avatarUri),
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
private fun eventData(event: IssueTimelineItem): IssuePullRequestEventData? {
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(stringResource(id = R.string.issue_timeline_added_to_project))
        }
        is AssignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = buildAnnotatedString {
                if (event.actor?.login == event.assigneeLogin) {
                    append(stringResource(id = R.string.issue_timeline_assigned_event_self_assigned))
                } else {
                    append(stringResource(id = R.string.issue_timeline_assigned_event_assigned_someone))
                    append(
                        AnnotatedString(
                            text = event.assigneeLogin ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        is ClosedEvent -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundColor = MaterialTheme.colors.error
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_closed_event_closed))
        }
        is ConvertedNoteToIssueEvent -> {
            iconResId = R.drawable.ic_issue_open_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_converted_note_to_issue))
        }
        is CrossReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_demilestoned_event_demilestoned))
                append(event.milestoneTitle)
                append(stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        is LabeledEvent -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
        is LockedEvent -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            backgroundColor = MaterialTheme.colors.error
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_1))
                append(
                    stringResource(
                        when (event.lockReason) {
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
                            LockReason.UNKNOWN__,
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_marked_as_duplicate))
        }
        is MilestonedEvent -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_milestoned_event_milestoned))
                append(
                    AnnotatedString(
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_moved_columns_in_project)
            )
        }
        is PinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(text = stringResource(id = R.string.issue_timeline_pinned))
        }
        is ReferencedEvent -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_removed_from_project)
            )
        }
        is RenamedTitleEvent -> {
            iconResId = R.drawable.ic_edit_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
            backgroundColor = Color(0x388E3C)
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_reopened_event_reopened)
            )
        }
        is TransferredEvent -> {
            iconResId = R.drawable.ic_dot_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.issue_timeline_transferred_event_transferred,
                    event.nameWithOwnerOfFromRepository ?: ""
                )
            )
        }
        is UnassignedEvent -> {
            iconResId = R.drawable.ic_person_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
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
            backgroundColor = MaterialTheme.colors.error
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_unlocked_event_unlocked))
        }
        is UnpinnedEvent -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.actor?.login
            avatarUri = event.actor?.avatarUrl
            createdAt = event.createdAt
            content = AnnotatedString(text = stringResource(id = R.string.issue_timeline_unpinned))
        }
        else -> {
            iconResId = null
            backgroundColor = null
            login = null
            avatarUri = null
            createdAt = null
            content = null
        }
    }

    return if (iconResId == null
        || backgroundColor == null
        || avatarUri == null
        || login == null
        || createdAt == null
        || content == null
    ) {
        null
    } else {
        IssuePullRequestEventData(
            iconResId,
            backgroundColor,
            avatarUri,
            login,
            createdAt,
            content
        )
    }
}

@Composable
fun IssueOrPullRequestHeader(
    repoOwner: String,
    repoName: String,
    number: Int,
    title: String,
    caption: String,
    avatarUrl: String?,
    viewerCanReact: Boolean,
    reactionGroups: MutableList<ReactionGroup>?,
    authorLogin: String?,
    authorAssociation: CommentAuthorAssociation,
    displayHtml: String,
    commentCreatedAt: Instant
) {
    Column {
        Text(
            text = stringResource(
                id = R.string.repository_name_with_username,
                repoOwner,
                repoName
            ),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ContentPaddingLargeSize)
        )
        Text(
            text = stringResource(id = R.string.issue_pr_title_format, number, title),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentPaddingLargeSize)
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = caption,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = ContentPaddingLargeSize)
            )
        }
        IssueTimelineCommentItem(
            avatarUrl = avatarUrl,
            viewerCanReact = viewerCanReact,
            reactionGroups = reactionGroups,
            authorLogin = authorLogin,
            authorAssociation = authorAssociation,
            displayHtml = displayHtml,
            commentCreatedAt = commentCreatedAt
        )
    }
}

@Composable
fun IssueTimelineCommentItem(
    avatarUrl: String?,
    viewerCanReact: Boolean,
    reactionGroups: MutableList<ReactionGroup>?,
    authorLogin: String?,
    authorAssociation: CommentAuthorAssociation,
    displayHtml: String,
    commentCreatedAt: Instant
) {
    val reactionDialogState = remember { mutableStateOf(false) }
    if (viewerCanReact) {
        AddReactionDialogScreen(
            showState = reactionDialogState,
            reactionGroups = reactionGroups,
            react = { _, _ ->

            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CoilImage(
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                request = createAvatarLoadRequest(url = avatarUrl),
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
            )
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Text(
                    text = authorLogin ?: "ghost",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1
                )
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(
                            commentCreatedAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ) as String,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                if (authorAssociation != CommentAuthorAssociation.NONE
                    && authorAssociation != CommentAuthorAssociation.UNKNOWN__
                ) {
                    Text(
                        text = stringResource(
                            when (authorAssociation) {
                                CommentAuthorAssociation.COLLABORATOR -> {
                                    R.string.author_association_collaborator
                                }
                                CommentAuthorAssociation.CONTRIBUTOR -> {
                                    R.string.author_association_contributor
                                }
                                CommentAuthorAssociation.FIRST_TIMER -> {
                                    R.string.author_association_first_timer
                                }
                                CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> {
                                    R.string.author_association_first_timer_contributor
                                }
                                CommentAuthorAssociation.MEMBER -> {
                                    R.string.author_association_member
                                }
                                // CommentAuthorAssociation.OWNER
                                else -> {
                                    R.string.author_association_owner
                                }
                            }
                        ),
                        style = MaterialTheme.typography.body2
                    )
                }
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                Icon(
                    contentDescription = stringResource(id = R.string.add_reaction_image_content_description),
                    painter = painterResource(id = R.drawable.ic_emoji_emotions_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .clickable {
                            reactionDialogState.value = true
                        }
                        .padding(all = ContentPaddingMediumSize)
                )
                Icon(
                    contentDescription = stringResource(id = R.string.more_actions_image_content_description),
                    painter = painterResource(id = R.drawable.ic_more_vert_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .clickable {

                        }
                        .padding(all = ContentPaddingMediumSize)
                )
            }
        }
        var webView by remember { mutableStateOf<ThemedWebView?>(null) }
        DisposableEffect(key1 = webView) {
            webView?.loadData(displayHtml)
            onDispose {
                webView?.stopLoading()
            }
        }
        AndroidView(factory = { context ->
            ThemedWebView(context = context)
        }) {
            webView = it
        }
        reactionGroups?.let { reactions ->
            LazyRow {
                items(count = reactions.size) { index ->
                    if (reactions[index].usersTotalCount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(enabled = viewerCanReact) {

                                }
                                .clip(shape = RoundedCornerShape(percent = 50))
                                .background(
                                    color = if (reactions[index].viewerHasReacted) {
                                        MaterialTheme.colors.primary
                                    } else {
                                        MaterialTheme.colors.onSurface
                                    }.copy(alpha = .12f)
                                )
                                .padding(horizontal = 12.dp, vertical = ContentPaddingSmallSize)
                        ) {
                            Text(
                                text = stringResource(
                                    when (reactions[index].content) {
                                        ReactionContent.CONFUSED -> {
                                            R.string.emoji_confused
                                        }
                                        ReactionContent.EYES -> {
                                            R.string.emoji_eyes
                                        }
                                        ReactionContent.HEART -> {
                                            R.string.emoji_heart
                                        }
                                        ReactionContent.HOORAY -> {
                                            R.string.emoji_hooray
                                        }
                                        ReactionContent.LAUGH -> {
                                            R.string.emoji_laugh
                                        }
                                        ReactionContent.ROCKET -> {
                                            R.string.emoji_rocket
                                        }
                                        ReactionContent.THUMBS_DOWN -> {
                                            R.string.emoji_thumbs_down
                                        }
                                        ReactionContent.THUMBS_UP -> {
                                            R.string.emoji_thumbs_up
                                        }
                                        ReactionContent.UNKNOWN__ -> {
                                            R.string.emoji_unknown
                                        }
                                    }
                                ),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                            Text(
                                text = reactions[index].usersTotalCount.toString(),
                                style = MaterialTheme.typography.body2
                            )
                        }
                        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                    }
                }
            }
        }
    }
}

@Preview(name = "IssueTimelineEventItemPreview", showBackground = true)
@Composable
private fun IssueTimelineEventItemPreview(
    @PreviewParameter(
        provider = IssueTimelineEventProvider::class,
        limit = 1
    )
    event: IssueTimelineItem
) {
    ItemIssueTimelineEvent(event)
}