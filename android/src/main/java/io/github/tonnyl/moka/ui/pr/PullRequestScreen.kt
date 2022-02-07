package io.github.tonnyl.moka.ui.pr

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.issue.IssueOrPullRequestHeader
import io.github.tonnyl.moka.ui.issue.IssuePullRequestEventData
import io.github.tonnyl.moka.ui.issue.IssueTimelineCommentItem
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.data.PullRequestTimelineItem
import io.tonnyl.moka.common.data.extension.assigneeLogin
import io.tonnyl.moka.common.data.extension.requestedReviewerLogin
import io.tonnyl.moka.common.extensions.shortOid
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.PullRequestProvider
import io.tonnyl.moka.common.util.PullRequestTimelineItemProvider
import io.tonnyl.moka.graphql.PullRequestQuery.PullRequest
import io.tonnyl.moka.graphql.type.LockReason
import io.tonnyl.moka.graphql.type.PullRequestReviewState
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun PullRequestScreen(
    owner: String,
    name: String,
    number: Int
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<PullRequestViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[PullRequestViewModel.PULL_REQUEST_VIEW_MODEL_EXTRA_KEY] =
                PullRequestViewModelExtra(
                    accountInstance = currentAccount,
                    owner = owner,
                    name = name,
                    number = number
                )
        }
    )

    val pullRequest by viewModel.pullRequest.observeAsState()
    val prTimelineItems = viewModel.prTimelineFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = prTimelineItems.loadState.refresh is LoadState.Loading),
            onRefresh = prTimelineItems::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                prTimelineItems.loadState.refresh is LoadState.NotLoading
                        && prTimelineItems.loadState.append is LoadState.NotLoading
                        && prTimelineItems.loadState.prepend is LoadState.NotLoading
                        && prTimelineItems.itemCount == 0
                        && pullRequest == null -> {

                }
                prTimelineItems.loadState.refresh is LoadState.NotLoading
                        && prTimelineItems.itemCount == 0
                        && pullRequest == null -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                prTimelineItems.loadState.refresh is LoadState.Error
                        && prTimelineItems.itemCount == 0
                        && pullRequest == null -> {
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

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.pull_request)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            imageVector = Icons.Outlined.ArrowBack
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
private fun PullRequestScreenContent(
    contentTopPadding: Dp,
    owner: String,
    name: String,
    pullRequest: PullRequest?,
    timelineItems: LazyPagingItems<PullRequestTimelineItem>
) {
    val pullRequestPlaceholder = remember {
        PullRequestProvider().values.first()
    }
    val timelinePlaceholder = remember {
        PullRequestTimelineItemProvider().values.first()
    }

    val enablePlaceholder = timelineItems.loadState.refresh is LoadState.Loading

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        if (enablePlaceholder) {
            item {
                IssueOrPullRequestHeader(
                    repoOwner = owner,
                    repoName = name,
                    number = pullRequestPlaceholder.number,
                    title = pullRequestPlaceholder.title,
                    caption = stringResource(
                        id = R.string.issue_pr_info_format,
                        stringResource(
                            id = when {
                                pullRequestPlaceholder.closed -> {
                                    R.string.issue_pr_status_closed
                                }
                                pullRequestPlaceholder.merged -> {
                                    R.string.issue_pr_status_merged
                                }
                                else -> {
                                    R.string.issue_pr_status_open
                                }
                            }
                        ),
                        stringResource(
                            id = R.string.issue_pr_created_by,
                            pullRequestPlaceholder.author?.actor?.login ?: "ghost"
                        ),
                        DateUtils.getRelativeTimeSpanString(
                            pullRequestPlaceholder.createdAt.toEpochMilliseconds(),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ) as String
                    ),
                    avatarUrl = pullRequestPlaceholder.author?.actor?.avatarUrl,
                    viewerCanReact = pullRequestPlaceholder.viewerCanReact,
                    reactionGroups = pullRequestPlaceholder.reactionGroups?.map { it.reactionGroup },
                    authorLogin = pullRequestPlaceholder.author?.actor?.login,
                    authorAssociation = pullRequestPlaceholder.authorAssociation,
                    displayHtml = pullRequestPlaceholder.bodyHTML.takeIf { it.isNotEmpty() }
                        ?: stringResource(id = R.string.no_description_provided),
                    commentCreatedAt = pullRequestPlaceholder.createdAt,
                    enablePlaceholder = true
                )
            }
        } else {
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
                                pullRequest.author?.actor?.login ?: "ghost"
                            ),
                            DateUtils.getRelativeTimeSpanString(
                                pullRequest.createdAt.toEpochMilliseconds(),
                                System.currentTimeMillis(),
                                DateUtils.MINUTE_IN_MILLIS
                            ) as String
                        ),
                        avatarUrl = pullRequest.author?.actor?.avatarUrl,
                        viewerCanReact = pullRequest.viewerCanReact,
                        reactionGroups = pullRequest.reactionGroups?.map { it.reactionGroup },
                        authorLogin = pullRequest.author?.actor?.login,
                        authorAssociation = pullRequest.authorAssociation,
                        displayHtml = pullRequest.bodyHTML.takeIf { it.isNotEmpty() }
                            ?: stringResource(id = R.string.no_description_provided),
                        commentCreatedAt = pullRequest.createdAt,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = timelineItems.loadState.prepend)
        }

        if (enablePlaceholder) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemPullRequestTimelineEvent(
                    timelineItem = timelinePlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = timelineItems,
                key = { index, _ ->
                    index
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
                            enablePlaceholder = false
                        )
                    } else {
                        ItemPullRequestTimelineEvent(
                            timelineItem = item,
                            enablePlaceholder = false
                        )
                    }
                }
            }
        }

        item {
            ItemLoadingState(loadState = timelineItems.loadState.append)
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun ItemPullRequestTimelineEvent(
    timelineItem: PullRequestTimelineItem,
    enablePlaceholder: Boolean
) {
    val data = eventData(timelineItem) ?: return

    val navController = LocalNavController.current
    val navigateToProfile = {
        navController.navigate(
            route = Screen.Profile.route
                .replace("{${Screen.ARG_PROFILE_LOGIN}}", data.login)
                .replace(
                    "{${Screen.ARG_PROFILE_TYPE}}",
                    ProfileType.NOT_SPECIFIED.name
                )
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
                .background(color = data.backgroundColor)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
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
            Image(
                painter = rememberImagePainter(
                    data = data.avatarUri,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                modifier = Modifier
                    .size(size = IssueTimelineEventAuthorAvatarSize)
                    .clip(shape = CircleShape)
                    .clickable(enabled = !enablePlaceholder) {
                        navigateToProfile.invoke()
                    }
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
                    modifier = Modifier.placeholder(
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
        if (!data.nodeId.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(alignment = Alignment.End)
                    .clip(shape = MaterialTheme.shapes.medium)
                    .clickable {
                        navController.navigate(
                            route = Screen.CommentThread.route
                                .replace("{${Screen.ARG_NODE_ID}}", data.nodeId)
                        )
                    }
                    .padding(all = ContentPaddingMediumSize)
            ) {
                Text(
                    text = stringResource(id = R.string.thread),
                    style = MaterialTheme.typography.button,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowRight,
                    contentDescription = stringResource(id = R.string.thread),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
private fun eventData(event: PullRequestTimelineItem): IssuePullRequestEventData? {
    var iconResId: Int? = null
    var iconVector: ImageVector? = null
    val backgroundColor: Color?
    val avatarUri: String?
    val login: String?
    val createdAt: Instant?
    val content: AnnotatedString?
    var nodeId: String? = null

    when {
        event.addedToProjectEvent != null -> {
            iconResId = R.drawable.ic_dashboard_outline
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.addedToProjectEvent!!.actor?.actor?.avatarUrl
            login = event.addedToProjectEvent!!.actor?.actor?.login
            createdAt = event.addedToProjectEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_added_to_project))
        }
        event.assignedEvent != null -> {
            iconVector = Icons.Outlined.Person
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.assignedEvent!!.actor?.actor?.avatarUrl
            login = event.assignedEvent!!.actor?.actor?.login
            createdAt = event.assignedEvent!!.createdAt
            content = buildAnnotatedString {
                if (event.assignedEvent!!.actor?.actor?.login == event.assignedEvent!!.assignee?.issuePullRequestTimelineItemAssigneeFragment?.assigneeLogin) {
                    append(text = stringResource(id = R.string.issue_timeline_assigned_event_self_assigned))
                } else {
                    append(text = stringResource(id = R.string.issue_timeline_assigned_event_assigned_someone))
                    append(
                        text = AnnotatedString(
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
        event.baseRefChangedEvent != null -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.baseRefChangedEvent!!.actor?.actor?.avatarUrl
            login = event.baseRefChangedEvent!!.actor?.actor?.login
            createdAt = event.baseRefChangedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.pull_request_base_ref_changed))
        }
        event.baseRefForcePushedEvent != null -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.baseRefForcePushedEvent!!.actor?.actor?.avatarUrl
            login = event.baseRefForcePushedEvent!!.actor?.actor?.login
            createdAt = event.baseRefForcePushedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_force_pushed_branch,
                    event.baseRefForcePushedEvent!!.ref?.pullRequestTimelineItemRefFragment?.name
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.baseRefForcePushedEvent!!.beforeCommit?.pullRequestTimelineItemCommitFragment?.oid?.shortOid
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.baseRefForcePushedEvent!!.afterCommit?.pullRequestTimelineItemCommitFragment?.oid?.shortOid
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        event.closedEvent != null -> {
            iconResId = R.drawable.ic_pr_issue_close_24
            backgroundColor = MaterialTheme.colors.error
            avatarUri = event.closedEvent!!.actor?.actor?.avatarUrl
            login = event.closedEvent!!.actor?.actor?.login
            createdAt = event.closedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_closed_event_closed))
        }
        event.convertedNoteToIssueEvent != null -> {
            iconResId = R.drawable.ic_issue_open_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.convertedNoteToIssueEvent!!.actor?.actor?.avatarUrl
            login = event.convertedNoteToIssueEvent!!.actor?.actor?.login
            createdAt = event.convertedNoteToIssueEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_converted_note_to_issue))
        }
        event.crossReferencedEvent != null -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.crossReferencedEvent!!.actor?.actor?.avatarUrl
            login = event.crossReferencedEvent!!.actor?.actor?.login
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
            avatarUri = event.demilestonedEvent!!.actor?.actor?.avatarUrl
            login = event.demilestonedEvent!!.actor?.actor?.login
            createdAt = event.demilestonedEvent!!.createdAt
            content = buildAnnotatedString {
                append(stringResource(id = R.string.issue_timeline_demilestoned_event_demilestoned))
                append(event.demilestonedEvent!!.milestoneTitle)
                append(stringResource(id = R.string.issue_timeline_milestone_suffix))
            }
        }
        event.deployedEvent != null -> {
            iconResId = R.drawable.ic_rocket_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.deployedEvent!!.actor?.actor?.avatarUrl
            login = event.deployedEvent!!.actor?.actor?.login
            createdAt = event.deployedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_deployed_to_branch,
                    event.deployedEvent!!.deployment.pullRequestTimelineItemDeploymentFragment.environment
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        event.deploymentEnvironmentChangedEvent != null -> {
            iconResId = R.drawable.ic_rocket_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.deploymentEnvironmentChangedEvent!!.actor?.actor?.avatarUrl
            login = event.deploymentEnvironmentChangedEvent!!.actor?.actor?.login
            createdAt = event.deploymentEnvironmentChangedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_deployed_to_branch,
                    event.deploymentEnvironmentChangedEvent!!.deploymentStatus.deployment.pullRequestTimelineItemDeploymentFragment.environment
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        event.headRefDeletedEvent != null -> {
            iconResId = R.drawable.ic_delete_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.headRefDeletedEvent!!.actor?.actor?.avatarUrl
            login = event.headRefDeletedEvent!!.actor?.actor?.login
            createdAt = event.headRefDeletedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_deleted_branch,
                    event.headRefDeletedEvent!!.headRefName
                )
            )
        }
        event.headRefForcePushedEvent != null -> {
            iconResId = R.drawable.ic_book_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.headRefForcePushedEvent!!.actor?.actor?.avatarUrl
            login = event.headRefForcePushedEvent!!.actor?.actor?.login
            createdAt = event.headRefForcePushedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    R.string.pull_request_force_pushed_branch,
                    event.headRefForcePushedEvent!!.ref?.pullRequestTimelineItemRefFragment?.name
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.headRefForcePushedEvent!!.beforeCommit?.pullRequestTimelineItemCommitFragment?.oid?.shortOid
                        ?: stringResource(id = R.string.pull_request_unknown),
                    event.headRefForcePushedEvent!!.afterCommit?.pullRequestTimelineItemCommitFragment?.oid?.shortOid
                        ?: stringResource(id = R.string.pull_request_unknown)
                )
            )
        }
        event.headRefRestoredEvent != null -> {
            iconResId = R.drawable.ic_restore_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.headRefRestoredEvent!!.actor?.actor?.avatarUrl
            login = event.headRefRestoredEvent!!.actor?.actor?.login
            createdAt = event.headRefRestoredEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_restore_branch,
                    event.headRefRestoredEvent!!.pullRequest.headRefName
                )
            )
        }
        event.labeledEvent != null -> {
            iconResId = R.drawable.ic_label_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.labeledEvent!!.actor?.actor?.avatarUrl
            login = event.labeledEvent!!.actor?.actor?.login
            createdAt = event.labeledEvent!!.createdAt
            content = buildAnnotatedString {
                event.labeledEvent!!.label.issuePrLabelFragment.color.toColor()?.let {
                    append(text = stringResource(id = R.string.issue_timeline_labeled_event_labeled))
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
                append(text = stringResource(id = R.string.issue_timeline_label))
            }
        }
        event.lockedEvent != null -> {
            iconResId = R.drawable.ic_lock_outline_24dp
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.lockedEvent!!.actor?.actor?.avatarUrl
            login = event.lockedEvent!!.actor?.actor?.login
            createdAt = event.lockedEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_locked_event_locked_as_part_1))
                append(
                    text = stringResource(
                        id = when (event.lockedEvent!!.lockReason) {
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
            avatarUri = event.markedAsDuplicateEvent!!.actor?.actor?.avatarUrl
            login = event.markedAsDuplicateEvent!!.actor?.actor?.login
            createdAt = event.markedAsDuplicateEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_marked_as_duplicate))
        }
        event.mergedEvent != null -> {
            iconResId = R.drawable.ic_pr_merged
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.mergedEvent!!.actor?.actor?.avatarUrl
            login = event.mergedEvent!!.actor?.actor?.login
            createdAt = event.mergedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(
                    id = R.string.pull_request_merged_commit,
                    event.mergedEvent!!.commit?.pullRequestTimelineItemCommitFragment?.oid.shortOid,
                    event.mergedEvent!!.mergeRefName
                )
            )
        }
        event.milestonedEvent != null -> {
            iconResId = R.drawable.ic_milestone_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.milestonedEvent!!.actor?.actor?.avatarUrl
            login = event.milestonedEvent!!.actor?.actor?.login
            createdAt = event.milestonedEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.issue_timeline_milestoned_event_milestoned))
                append(
                    text = AnnotatedString(
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
            avatarUri = event.movedColumnsInProjectEvent!!.actor?.actor?.avatarUrl
            login = event.movedColumnsInProjectEvent!!.actor?.actor?.login
            createdAt = event.movedColumnsInProjectEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_moved_columns_in_project)
            )
        }
        event.pinnedEvent != null -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.pinnedEvent!!.actor?.actor?.avatarUrl
            login = event.pinnedEvent!!.actor?.actor?.login
            createdAt = event.pinnedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_pinned))
        }
        event.pullRequestCommit != null -> {
            iconResId = R.drawable.ic_commit_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri =
                event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.author?.gitActorFragment?.avatarUrl
                    ?: event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.committer?.gitActorFragment?.avatarUrl
            login =
                event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.author?.gitActorFragment?.user?.userListItemFragment?.login
                    ?: event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.author?.gitActorFragment?.name
                            ?: event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.committer?.gitActorFragment?.user?.userListItemFragment?.login
                            ?: event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.committer?.gitActorFragment?.user?.userListItemFragment?.name
                            ?: "ghost"
            createdAt = null
            content =
                AnnotatedString(text = event.pullRequestCommit!!.commit.pullRequestTimelineItemCommitFragment.message)
        }
        event.pullRequestReview != null -> {
            avatarUri = event.pullRequestReview!!.author?.actor?.avatarUrl
            createdAt = event.pullRequestReview!!.createdAt
            login = event.pullRequestReview!!.author?.actor?.login
            val stateString: String
            nodeId =
                event.pullRequestReview!!.id.takeIf { event.pullRequestReview!!.comments.totalCount > 0 }

            when (event.pullRequestReview!!.state) {
                PullRequestReviewState.APPROVED -> {
                    iconVector = Icons.Outlined.Check
                    backgroundColor = issuePrGreen
                    stateString =
                        stringResource(id = R.string.pull_request_review_approved_changes)
                }
                PullRequestReviewState.CHANGES_REQUESTED -> {
                    iconVector = Icons.Outlined.Close
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
            }
        }
        event.readyForReviewEvent != null -> {
            iconResId = R.drawable.ic_eye_24
            backgroundColor = issuePrGreen
            avatarUri = event.readyForReviewEvent!!.actor?.actor?.avatarUrl
            login = event.readyForReviewEvent!!.actor?.actor?.login
            createdAt = event.readyForReviewEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.pull_request_marked_as_ready_for_review))
        }
        event.referencedEvent != null -> {
            iconResId = R.drawable.ic_bookmark_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.referencedEvent!!.actor?.actor?.avatarUrl
            login = event.referencedEvent!!.actor?.actor?.login
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
            avatarUri = event.removedFromProjectEvent!!.actor?.actor?.avatarUrl
            login = event.removedFromProjectEvent!!.actor?.actor?.login
            createdAt = event.removedFromProjectEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_removed_from_project)
            )
        }
        event.renamedTitleEvent != null -> {
            iconVector = Icons.Outlined.Edit
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.renamedTitleEvent!!.actor?.actor?.avatarUrl
            login = event.renamedTitleEvent!!.actor?.actor?.login
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
            avatarUri = event.reopenedEvent!!.actor?.actor?.avatarUrl
            login = event.reopenedEvent!!.actor?.actor?.login
            createdAt = event.reopenedEvent!!.createdAt
            content = AnnotatedString(
                text = stringResource(id = R.string.issue_timeline_reopened_event_reopened)
            )
        }
        event.reviewDismissedEvent != null -> {
            iconVector = Icons.Outlined.Close
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.reviewDismissedEvent!!.actor?.actor?.avatarUrl
            login = event.reviewDismissedEvent!!.actor?.actor?.login
            createdAt = event.reviewDismissedEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_part_1))
                append(
                    text = event.reviewDismissedEvent!!.review?.pullRequestReviewFragment?.author?.actor?.login
                        ?: "ghost"
                )
                append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_part_2))

                if (!event.reviewDismissedEvent!!.dismissalMessage.isNullOrEmpty()) {
                    append(text = stringResource(id = R.string.pull_request_dismiss_someones_review_and_left_a_comment))
                    append(text = event.reviewDismissedEvent!!.dismissalMessage ?: "")
                }
            }
        }
        event.reviewRequestRemovedEvent != null -> {
            iconVector = Icons.Outlined.Close
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.reviewRequestRemovedEvent!!.actor?.actor?.avatarUrl
            login = event.reviewRequestRemovedEvent!!.actor?.actor?.login
            createdAt = event.reviewRequestRemovedEvent!!.createdAt
            content = buildAnnotatedString {
                append(text = stringResource(id = R.string.pull_request_removed_someones_review_request_part_1))
                append(
                    text = event.reviewRequestRemovedEvent!!.requestedReviewer?.requestedReviewerLogin
                        ?: "ghost"
                )
                append(text = stringResource(id = R.string.pull_request_removed_someones_review_request_part_2))
            }
        }
        event.reviewRequestedEvent != null -> {
            iconResId = R.drawable.ic_eye_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.reviewRequestedEvent!!.actor?.actor?.avatarUrl
            login = event.reviewRequestedEvent!!.actor?.actor?.login
            createdAt = event.reviewRequestedEvent!!.createdAt
            content =
                if (event.reviewRequestedEvent!!.actor?.actor?.login == event.reviewRequestedEvent!!.requestedReviewer?.requestedReviewerLogin) {
                    AnnotatedString(text = stringResource(id = R.string.pull_request_self_requested_a_review))
                } else {
                    buildAnnotatedString {
                        append(text = stringResource(id = R.string.pull_request_requested_review_from))
                        append(
                            text = AnnotatedString(
                                text = event.reviewRequestedEvent!!.requestedReviewer?.requestedReviewerLogin
                                    ?: "ghost",
                                spanStyle = SpanStyle(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        )
                    }
                }
        }
        event.unassignedEvent != null -> {
            iconVector = Icons.Outlined.Person
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.unassignedEvent!!.actor?.actor?.avatarUrl
            login = event.unassignedEvent!!.actor?.actor?.login
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
            avatarUri = event.unlabeledEvent!!.actor?.actor?.avatarUrl
            login = event.unlabeledEvent!!.actor?.actor?.login
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
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.unlockedEvent!!.actor?.actor?.avatarUrl
            login = event.unlockedEvent!!.actor?.actor?.login
            createdAt = event.unlockedEvent!!.createdAt
            content =
                AnnotatedString(text = stringResource(id = R.string.issue_timeline_unlocked_event_unlocked))
        }
        event.unpinnedEvent != null -> {
            iconResId = R.drawable.ic_pin_24
            backgroundColor = MaterialTheme.colors.primary
            avatarUri = event.unpinnedEvent!!.actor?.actor?.avatarUrl
            login = event.unpinnedEvent!!.actor?.actor?.login
            createdAt = event.unpinnedEvent!!.createdAt
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

    return if ((iconResId == null && iconVector == null)
        || backgroundColor == null
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
            login ?: "ghost",
            createdAt,
            content,
            nodeId = nodeId
        )
    }
}

@ExperimentalCoilApi
@Preview(
    name = "PullRequestTimelineEventItemPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun PullRequestTimelineEventItemPreview(
    @PreviewParameter(
        PullRequestTimelineItemProvider::class,
        limit = 1
    )
    pullRequest: PullRequestTimelineItem
) {
    ItemPullRequestTimelineEvent(
        timelineItem = pullRequest,
        enablePlaceholder = false
    )
}