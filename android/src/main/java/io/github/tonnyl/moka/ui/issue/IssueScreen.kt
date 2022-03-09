package io.github.tonnyl.moka.ui.issue

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.reaction.AddReactionDialogScreen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.IssueTimelineItem
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.extension.assigneeLogin
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.IssueProvider
import io.tonnyl.moka.common.util.IssueTimelineEventProvider
import io.tonnyl.moka.graphql.IssueQuery.Issue
import io.tonnyl.moka.graphql.fragment.ReactionGroup
import io.tonnyl.moka.graphql.type.CommentAuthorAssociation
import io.tonnyl.moka.graphql.type.LockReason
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

data class IssuePullRequestEventData(
    val iconResId: Int?,
    val iconVector: ImageVector?,
    val backgroundColor: Color,
    val avatarUri: String?,
    val login: String,
    val createdAt: Instant,
    val content: AnnotatedString,
    val nodeId: String? = null
)

@Composable
fun IssueScreen(
    owner: String,
    name: String,
    number: Int
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            IssueViewModel(
                extra = IssueViewModelExtra(
                    accountInstance = currentAccount,
                    owner = owner,
                    name = name,
                    number = number
                )
            )
        }
    )

    val issueTimelineItems = viewModel.issueTimelineFlow.collectAsLazyPagingItems()
    val addedComments = viewModel.addedTimelineComments.observeAsState()

    val issue by viewModel.issueLiveData.observeAsState()

    val addCommentResource by viewModel.addCommentResource.observeAsState()

    val lazyListState = rememberLazyListState()

    val scope = rememberCoroutineScope()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val scaffoldState = rememberScaffoldState()

        val softKeyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        Scaffold(
            content = {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = issueTimelineItems.loadState.refresh is LoadState.Loading),
                    onRefresh = issueTimelineItems::refresh,
                    indicatorPadding = contentPaddings,
                    indicator = { state, refreshTriggerDistance ->
                        DefaultSwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = refreshTriggerDistance
                        )
                    }
                ) {
                    when {
                        issueTimelineItems.loadState.refresh is LoadState.NotLoading
                                && issueTimelineItems.loadState.append is LoadState.NotLoading
                                && issueTimelineItems.loadState.prepend is LoadState.NotLoading
                                && issueTimelineItems.itemCount == 0
                                && issue == null -> {

                        }
                        issueTimelineItems.loadState.refresh is LoadState.NotLoading
                                && issueTimelineItems.itemCount == 0
                                && issue == null -> {
                            EmptyScreenContent(
                                titleId = R.string.common_no_data_found,
                                action = issueTimelineItems::retry
                            )
                        }
                        issueTimelineItems.loadState.refresh is LoadState.Error
                                && issueTimelineItems.itemCount == 0
                                && issue == null -> {
                            EmptyScreenContent(
                                action = issueTimelineItems::retry,
                                throwable = (issueTimelineItems.loadState.refresh as LoadState.Error).error
                            )
                        }
                        else -> {
                            IssueScreenContent(
                                contentPaddings = contentPaddings,
                                owner = owner,
                                name = name,
                                issue = issue,
                                timelineItems = issueTimelineItems,
                                textState = viewModel.commentText,
                                onSend = {
                                    viewModel.addComment()
                                    softKeyboardController?.hide()
                                    focusManager.clearFocus(force = true)
                                },
                                isSending = addCommentResource?.status == Status.LOADING,
                                addedComments = addedComments.value?.second.orEmpty(),
                                lazyListState = lazyListState
                            )
                        }
                    }
                }

                LaunchedEffect(key1 = addCommentResource?.status) {
                    if (addCommentResource?.status == Status.SUCCESS
                        && lazyListState.layoutInfo.totalItemsCount > 0
                    ) {
                        scope.launch {
                            lazyListState.scrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
                        }
                    }
                }

                if (addCommentResource?.status == Status.ERROR) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        messageId = R.string.issue_pr_failed_to_add_comment,
                        action = viewModel::addComment,
                        actionId = R.string.common_retry,
                        dismissAction = viewModel::onErrorDismissed
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.issue)) },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            actions = {
                ShareAndOpenInBrowserMenu(
                    showMenuState = remember { mutableStateOf(false) },
                    text = "https://github.com/${owner}/${name}/issues/${number}"
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
    contentPaddings: PaddingValues,
    owner: String,
    name: String,
    issue: Issue?,
    timelineItems: LazyPagingItems<IssueTimelineItem>,
    addedComments: List<IssueTimelineItem>,
    textState: MutableState<String>,
    onSend: () -> Unit,
    isSending: Boolean,
    lazyListState: LazyListState
) {
    val timelinePlaceholder = remember {
        IssueTimelineEventProvider().values.first()
    }
    val issuePlaceholder = remember {
        IssueProvider().values.first()
    }

    val enablePlaceholder = timelineItems.loadState.refresh is LoadState.Loading

    Column {
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPaddings,
            modifier = Modifier.weight(weight = 1f)
        ) {
            if (enablePlaceholder) {
                item {
                    IssueOrPullRequestHeader(
                        repoOwner = owner,
                        repoName = name,
                        number = issuePlaceholder.number,
                        title = issuePlaceholder.title,
                        caption = stringResource(
                            id = R.string.issue_pr_info_format,
                            stringResource(
                                id = if (issuePlaceholder.closed) {
                                    R.string.issue_pr_status_closed
                                } else {
                                    R.string.issue_pr_status_open
                                }
                            ),
                            stringResource(
                                id = R.string.issue_pr_created_by,
                                issuePlaceholder.author?.actor?.login ?: "ghost"
                            ),
                            DateUtils.getRelativeTimeSpanString(
                                issuePlaceholder.createdAt.toEpochMilliseconds(),
                                System.currentTimeMillis(),
                                DateUtils.MINUTE_IN_MILLIS
                            ) as String
                        ),
                        avatarUrl = issuePlaceholder.author?.actor?.avatarUrl,
                        viewerCanReact = issuePlaceholder.viewerCanReact,
                        reactionGroups = issuePlaceholder.reactionGroups?.map { it.reactionGroup },
                        authorLogin = issuePlaceholder.author?.actor?.login,
                        authorAssociation = issuePlaceholder.authorAssociation,
                        displayHtml = issuePlaceholder.bodyHTML.takeIf { it.isNotEmpty() }
                            ?: stringResource(id = R.string.no_description_provided),
                        commentCreatedAt = issuePlaceholder.createdAt,
                        enablePlaceholder = true
                    )
                }
            } else if (issue != null) {
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
                                issue.author?.actor?.login ?: "ghost"
                            ),
                            DateUtils.getRelativeTimeSpanString(
                                issue.createdAt.toEpochMilliseconds(),
                                System.currentTimeMillis(),
                                DateUtils.MINUTE_IN_MILLIS
                            ) as String
                        ),
                        avatarUrl = issue.author?.actor?.avatarUrl,
                        viewerCanReact = issue.viewerCanReact,
                        reactionGroups = issue.reactionGroups?.map { it.reactionGroup },
                        authorLogin = issue.author?.actor?.login,
                        authorAssociation = issue.authorAssociation,
                        displayHtml = issue.bodyHTML.takeIf { it.isNotEmpty() }
                            ?: stringResource(id = R.string.no_description_provided),
                        commentCreatedAt = issue.createdAt,
                        enablePlaceholder = false
                    )
                }
            }

            ItemLoadingState(loadState = timelineItems.loadState.prepend)

            if (enablePlaceholder) {
                items(count = defaultPagingConfig.initialLoadSize) {
                    ItemIssueTimelineEvent(
                        event = timelinePlaceholder,
                        enablePlaceholder = enablePlaceholder
                    )
                }
            } else {
                itemsIndexed(
                    items = timelineItems,
                    key = { _, item ->
                        item.hashCode()
                    }
                ) { _, item ->
                    if (item != null) {
                        if (item.issueComment != null) {
                            IssueTimelineCommentItem(
                                avatarUrl = item.issueComment!!.author?.actor?.avatarUrl,
                                viewerCanReact = item.issueComment!!.viewerCanReact,
                                reactionGroups = item.issueComment!!.reactionGroups?.map { it.reactionGroup },
                                authorLogin = item.issueComment!!.author?.actor?.login,
                                authorAssociation = item.issueComment!!.authorAssociation,
                                displayHtml = item.issueComment!!.body,
                                commentCreatedAt = item.issueComment!!.createdAt,
                                enablePlaceholder = enablePlaceholder
                            )
                        } else {
                            ItemIssueTimelineEvent(
                                event = item,
                                enablePlaceholder = enablePlaceholder
                            )
                        }
                    }
                }

                items(count = addedComments.size) {
                    val item = addedComments[it]
                    IssueTimelineCommentItem(
                        avatarUrl = item.issueComment!!.author?.actor?.avatarUrl,
                        viewerCanReact = item.issueComment!!.viewerCanReact,
                        reactionGroups = item.issueComment!!.reactionGroups?.map { it.reactionGroup },
                        authorLogin = item.issueComment!!.author?.actor?.login,
                        authorAssociation = item.issueComment!!.authorAssociation,
                        displayHtml = item.issueComment!!.body,
                        commentCreatedAt = item.issueComment!!.createdAt,
                        enablePlaceholder = enablePlaceholder
                    )
                }
            }

            ItemLoadingState(loadState = timelineItems.loadState.append)
        }

        if (issue != null) {
            BottomInputBar(
                textState = textState,
                onSend = onSend,
                isSending = isSending,
                lockReason = issue.activeLockReason,
                viewerCanEdit = issue.activeLockReason == null
                        || issue.authorAssociation == CommentAuthorAssociation.COLLABORATOR
                        || issue.authorAssociation == CommentAuthorAssociation.OWNER
            )
        }
    }
}

@Composable
fun ItemIssueTimelineEvent(
    event: IssueTimelineItem,
    enablePlaceholder: Boolean
) {
    val data = eventData(event) ?: return

    val navController = LocalNavController.current
    val navigateToProfile = {
        Screen.Profile.navigate(
            navController = navController,
            login = data.login,
            type = ProfileType.NOT_SPECIFIED
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ContentPaddingLargeSize)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
                .background(color = data.backgroundColor)
                .padding(all = ContentPaddingMediumSize)
            if (data.iconResId != null) {
                Icon(
                    contentDescription = stringResource(id = R.string.issue_pr_timeline_event_image_content_description),
                    painter = painterResource(id = data.iconResId),
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = modifier
                )
            } else if (data.iconVector != null) {
                Icon(
                    contentDescription = stringResource(id = R.string.issue_pr_timeline_event_image_content_description),
                    imageVector = data.iconVector,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = modifier
                )
            }
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            AvatarImage(
                url = data.avatarUri,
                modifier = Modifier
                    .size(size = IssueTimelineEventAuthorAvatarSize)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Text(
                text = data.login,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(weight = 1f)
                    .clickable(enabled = !enablePlaceholder) {
                        navigateToProfile.invoke()
                    }
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
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
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clickable(enabled = !enablePlaceholder) {
                            navigateToProfile.invoke()
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        Text(
            text = data.content,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@Composable
private fun eventData(event: IssueTimelineItem): IssuePullRequestEventData? {
    var iconResId: Int? = null
    var iconVector: ImageVector? = null
    val backgroundColor: Color?
    val avatarUri: String?
    val login: String?
    val createdAt: Instant?
    val content: AnnotatedString?

    when {
        event.addedToProjectEvent != null -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            login = event.addedToProjectEvent!!.actor?.actor?.login
            avatarUri = event.addedToProjectEvent!!.actor?.actor?.avatarUrl
            createdAt = event.addedToProjectEvent!!.createdAt
            content = AnnotatedString(stringResource(id = R.string.issue_timeline_added_to_project))
        }
        event.assignedEvent != null -> {
            iconVector = Icons.Outlined.Person
            backgroundColor = MaterialTheme.colors.primary
            login = event.assignedEvent!!.actor?.actor?.login
            avatarUri = event.assignedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.assignedEvent!!.createdAt
            content = buildAnnotatedString {
                if (event.assignedEvent!!.actor?.actor?.login == event.assignedEvent!!.assignee?.issuePullRequestTimelineItemAssigneeFragment?.assigneeLogin) {
                    append(stringResource(id = R.string.issue_timeline_assigned_event_self_assigned))
                } else {
                    append(stringResource(id = R.string.issue_timeline_assigned_event_assigned_someone))
                    append(
                        AnnotatedString(
                            text = event.assignedEvent!!.assignee?.issuePullRequestTimelineItemAssigneeFragment?.assigneeLogin
                                ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        event.closedEvent != null -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundColor = MaterialTheme.colors.error
            login = event.closedEvent!!.actor?.actor?.login
            avatarUri = event.closedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.closedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_closed_event_closed))
        }
        event.convertedNoteToIssueEvent != null -> {
            iconResId = R.drawable.ic_issue_open_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.convertedNoteToIssueEvent!!.actor?.actor?.login
            avatarUri = event.convertedNoteToIssueEvent!!.actor?.actor?.avatarUrl
            createdAt = event.convertedNoteToIssueEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_converted_note_to_issue))
        }
        event.crossReferencedEvent != null -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.crossReferencedEvent!!.actor?.actor?.login
            avatarUri = event.crossReferencedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.crossReferencedEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_cross_referenced_event_cross_referenced))
                append(
                    AnnotatedString(
                        text = event.crossReferencedEvent!!.source.referencedEventIssueFragment?.title
                            ?: event.crossReferencedEvent!!.source.referencedEventPullRequestFragment?.title
                            ?: "",
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
                append(
                    stringResource(
                        R.string.issue_timeline_issue_pr_number_with_blank_prefix,
                        event.crossReferencedEvent!!.source.referencedEventIssueFragment?.number
                            ?: event.crossReferencedEvent!!.source.referencedEventPullRequestFragment?.number
                            ?: 0
                    )
                )
            }
        }
        event.demilestonedEvent != null -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.demilestonedEvent!!.actor?.actor?.login
            avatarUri = event.demilestonedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.demilestonedEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_demilestoned_event_demilestoned))
                append(event.demilestonedEvent!!.milestoneTitle)
                append(stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        event.labeledEvent != null -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.labeledEvent!!.actor?.actor?.login
            avatarUri = event.labeledEvent!!.actor?.actor?.avatarUrl
            createdAt = event.labeledEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_labeled_event_labeled))
                event.labeledEvent!!.label.issuePrLabelFragment.color.toColor()?.let {
                    Color(it)
                }?.let { bgColor ->
                    append(
                        AnnotatedString(
                            text = event.labeledEvent!!.label.issuePrLabelFragment.name,
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
        event.lockedEvent != null -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            backgroundColor = MaterialTheme.colors.error
            login = event.lockedEvent!!.actor?.actor?.login
            avatarUri = event.lockedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.lockedEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_1))
                append(
                    stringResource(
                        when (event.lockedEvent!!.lockReason) {
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
                            else -> {
                                R.string.issue_lock_reason_unknown
                            }
                        }
                    )
                )
                append(stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_2))
            }
        }
        event.markedAsDuplicateEvent != null -> {
            iconResId = R.drawable.ic_copy_24dp
            backgroundColor = MaterialTheme.colors.primary
            login = event.markedAsDuplicateEvent!!.actor?.actor?.login
            avatarUri = event.markedAsDuplicateEvent!!.actor?.actor?.avatarUrl
            createdAt = event.markedAsDuplicateEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_marked_as_duplicate))
        }
        event.milestonedEvent != null -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.milestonedEvent!!.actor?.actor?.login
            avatarUri = event.milestonedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.milestonedEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_milestoned_event_milestoned))
                append(
                    AnnotatedString(
                        text = event.milestonedEvent!!.milestoneTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
                append(text = stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        event.movedColumnsInProjectEvent != null -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            login = event.movedColumnsInProjectEvent!!.actor?.actor?.login
            avatarUri = event.movedColumnsInProjectEvent!!.actor?.actor?.avatarUrl
            createdAt = event.movedColumnsInProjectEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_moved_columns_in_project)
            )
        }
        event.pinnedEvent != null -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.pinnedEvent!!.actor?.actor?.login
            avatarUri = event.pinnedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.pinnedEvent!!.createdAt
            content = AnnotatedString(text = stringResource(id = R.string.issue_timeline_pinned))
        }
        event.referencedEvent != null -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.referencedEvent!!.actor?.actor?.login
            avatarUri = event.referencedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.referencedEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_referenced_event_referenced))
                append(
                    text = event.referencedEvent!!.subject.referencedEventIssueFragment?.title
                        ?: event.referencedEvent!!.subject.referencedEventPullRequestFragment?.title
                        ?: ""
                )
                append(
                    text = stringResource(
                        id = R.string.issue_timeline_issue_pr_number_with_blank_prefix,
                        event.referencedEvent!!.subject.referencedEventIssueFragment?.number
                            ?: event.referencedEvent!!.subject.referencedEventPullRequestFragment?.number
                            ?: 0
                    )
                )
            }
        }
        event.removedFromProjectEvent != null -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            login = event.removedFromProjectEvent!!.actor?.actor?.login
            avatarUri = event.removedFromProjectEvent!!.actor?.actor?.avatarUrl
            createdAt = event.removedFromProjectEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_removed_from_project)
            )
        }
        event.renamedTitleEvent != null -> {
            iconVector = Icons.Outlined.Edit
            backgroundColor = MaterialTheme.colors.primary
            login = event.renamedTitleEvent!!.actor?.actor?.login
            avatarUri = event.renamedTitleEvent!!.actor?.actor?.avatarUrl
            createdAt = event.renamedTitleEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_renamed_title_event_change_title_part_1))
                append(
                    AnnotatedString(
                        text = event.renamedTitleEvent!!.previousTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.LineThrough
                        )
                    )
                )
                append(text = stringResource(id = R.string.issue_timeline_renamed_title_event_change_title_part_2))
                append(
                    AnnotatedString(
                        text = event.renamedTitleEvent!!.currentTitle,
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                )
            }
        }
        event.reopenedEvent != null -> {
            iconResId = R.drawable.ic_dot_24
            backgroundColor = issuePrGreen
            login = event.reopenedEvent!!.actor?.actor?.login
            avatarUri = event.reopenedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.reopenedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_reopened_event_reopened)
            )
        }
        event.transferredEvent != null -> {
            iconResId = R.drawable.ic_dot_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.transferredEvent!!.actor?.actor?.login
            avatarUri = event.transferredEvent!!.actor?.actor?.avatarUrl
            createdAt = event.transferredEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.issue_timeline_transferred_event_transferred,
                    event.transferredEvent!!.fromRepository?.nameWithOwner ?: ""
                )
            )
        }
        event.unassignedEvent != null -> {
            iconVector = Icons.Outlined.Person
            backgroundColor = MaterialTheme.colors.primary
            login = event.unassignedEvent!!.actor?.actor?.login
            avatarUri = event.unassignedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.unassignedEvent!!.createdAt
            content = buildAnnotatedString {
                if (event.unassignedEvent!!.actor?.actor?.login == event.unassignedEvent!!.assignee?.issuePullRequestTimelineItemAssigneeFragment?.assigneeLogin) {
                    append(stringResource(id = R.string.issue_timeline_unassigned_event_self_unassigned))
                } else {
                    append(stringResource(id = R.string.issue_timeline_unassigned_event_unassigned_someone))
                    append(
                        AnnotatedString(
                            text = event.unassignedEvent!!.assignee?.issuePullRequestTimelineItemAssigneeFragment?.assigneeLogin
                                ?: "ghost",
                            spanStyle = SpanStyle(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    )
                }
            }
        }
        event.unlabeledEvent != null -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.unlabeledEvent!!.actor?.actor?.login
            avatarUri = event.unlabeledEvent!!.actor?.actor?.avatarUrl
            createdAt = event.unlabeledEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_labeled_event_labeled))
                event.unlabeledEvent!!.label.issuePrLabelFragment.color.toColor()?.let {
                    Color(it)
                }?.let { bgColor ->
                    append(
                        AnnotatedString(
                            text = event.unlabeledEvent!!.label.issuePrLabelFragment.name,
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
        event.unlockedEvent != null -> {
            iconResId = R.drawable.ic_key_24
            backgroundColor = MaterialTheme.colors.error
            login = event.unlockedEvent!!.actor?.actor?.login
            avatarUri = event.unlockedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.unlockedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_unlocked_event_unlocked))
        }
        event.unpinnedEvent != null -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            login = event.unpinnedEvent!!.actor?.actor?.login
            avatarUri = event.unpinnedEvent!!.actor?.actor?.avatarUrl
            createdAt = event.unpinnedEvent!!.createdAt
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
            iconVector = iconVector,
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
    reactionGroups: List<ReactionGroup>?,
    authorLogin: String?,
    authorAssociation: CommentAuthorAssociation,
    displayHtml: String,
    commentCreatedAt: Instant,
    enablePlaceholder: Boolean
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
                .padding(all = ContentPaddingLargeSize)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Text(
            text = stringResource(id = R.string.issue_pr_title_format, number, title),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(horizontal = ContentPaddingLargeSize)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = caption,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(all = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }
        IssueTimelineCommentItem(
            avatarUrl = avatarUrl,
            viewerCanReact = viewerCanReact,
            reactionGroups = reactionGroups,
            authorLogin = authorLogin,
            authorAssociation = authorAssociation,
            displayHtml = displayHtml,
            commentCreatedAt = commentCreatedAt,
            enablePlaceholder = enablePlaceholder
        )
    }
}

@Composable
fun IssueTimelineCommentItem(
    avatarUrl: String?,
    viewerCanReact: Boolean,
    reactionGroups: List<ReactionGroup>?,
    authorLogin: String?,
    authorAssociation: CommentAuthorAssociation,
    displayHtml: String,
    commentCreatedAt: Instant,
    enablePlaceholder: Boolean
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
        val navController = LocalNavController.current
        val navigateToProfile = {
            Screen.Profile.navigate(
                navController = navController,
                login = authorLogin ?: "ghost",
                type = ProfileType.NOT_SPECIFIED
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AvatarImage(
                url = avatarUrl,
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
                    .clickable(enabled = !enablePlaceholder) {
                        navigateToProfile.invoke()
                    }
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Text(
                    text = authorLogin ?: "ghost",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .clickable(enabled = !enablePlaceholder) {
                            navigateToProfile.invoke()
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
                if (enablePlaceholder) {
                    Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
                }
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(
                            commentCreatedAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ) as String,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
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
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                Icon(
                    contentDescription = stringResource(id = R.string.add_reaction_image_content_description),
                    painter = painterResource(id = R.drawable.ic_emoji_emotions_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .clickable(enabled = !enablePlaceholder) {
                            reactionDialogState.value = true
                        }
                        .padding(all = ContentPaddingMediumSize)
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
                Icon(
                    contentDescription = stringResource(id = R.string.more_actions_image_content_description),
                    imageVector = Icons.Outlined.MoreVert,
                    modifier = Modifier
                        .size(size = IconSize)
                        .clickable(enabled = !enablePlaceholder) {

                        }
                        .padding(all = ContentPaddingMediumSize)
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
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
        if (enablePlaceholder) {
            Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        }
        AndroidView(
            factory = { context ->
                ThemedWebView(context = context)
            },
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        ) {
            webView = it
        }
        ReactionGroupComponent(
            groups = reactionGroups.orEmpty(),
            tailingReactButton = false,
            viewerCanReact = viewerCanReact,
            react = {},
            enablePlaceholder = enablePlaceholder
        )
    }
}

@Preview(
    name = "IssueTimelineEventItemPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun IssueTimelineEventItemPreview(
    @PreviewParameter(
        provider = IssueTimelineEventProvider::class,
        limit = 1
    )
    event: IssueTimelineItem
) {
    ItemIssueTimelineEvent(
        event = event,
        enablePlaceholder = false
    )
}