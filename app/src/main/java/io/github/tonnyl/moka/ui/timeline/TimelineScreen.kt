package io.github.tonnyl.moka.ui.timeline

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.util.TimelineEventProvider
import io.github.tonnyl.moka.widget.*

@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun TimelineScreen(
    openDrawer: () -> Unit,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val currentUser by mainViewModel.currentUser.observeAsState()
    val authedUser = currentUser ?: return

    val timelineViewModel = viewModel<TimelineViewModel>(
        key = currentUser?.id?.toString(),
        factory = ViewModelFactory(authedUser.login, authedUser.id, mainViewModel.getApplication())
    )

    val eventsPager = remember {
        timelineViewModel.eventsFlow
    }
    val events = eventsPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SwipeToRefreshLayout(
            refreshingState = events.loadState.refresh is LoadState.Loading,
            onRefresh = events::refresh,
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
                events.loadState.refresh is LoadState.NotLoading
                        && events.loadState.append is LoadState.NotLoading
                        && events.loadState.prepend is LoadState.NotLoading
                        && events.itemCount == 0 -> {

                }
                events.loadState.refresh is LoadState.NotLoading
                        && events.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                events.loadState.refresh is LoadState.Error
                        && events.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    TimelineScreenContent(
                        topAppBarSize = topAppBarSize,
                        events = events,
                        navController = navController
                    )
                }
            }
        }

        MainSearchBar(
            openDrawer = openDrawer,
            mainViewModel = mainViewModel,
            navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }

}

@Composable
fun TimelineScreenContent(
    topAppBarSize: Int,
    navController: NavController,
    events: LazyPagingItems<Event>
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            ItemLoadingState(loadState = events.loadState.prepend)
        }

        itemsIndexed(lazyPagingItems = events) { index, item ->
            if (index == 0) {
                ListSubheader(text = stringResource(id = R.string.navigation_menu_timeline))
            }

            if (item != null) {
                ItemTimelineEvent(
                    event = item,
                    navController = navController
                )
            }
        }

        item {
            ItemLoadingState(loadState = events.loadState.append)
        }
    }
}

@Composable
private fun ItemTimelineEvent(
    event: Event,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                when (event.type) {
                    Event.WATCH_EVENT,
                    Event.PUBLIC_EVENT,
                    Event.CREATE_EVENT,
                    Event.TEAM_ADD_EVENT,
                    Event.DELETE_EVENT -> {
                        navigateToRepositoryScreen(
                            navController = navController,
                            fullName = event.repo?.name ?: return@clickable,
                            org = event.org
                        )
                    }
                    Event.COMMIT_COMMENT_EVENT -> {

                    }
                    Event.FORK_EVENT -> {
                        navigateToRepositoryScreen(
                            navController = navController,
                            fullName = event.payload?.forkee?.fullName ?: return@clickable,
                            org = event.org
                        )
                    }
                    Event.GOLLUM_EVENT -> {

                    }
                    Event.ISSUE_COMMENT_EVENT,
                    Event.ISSUES_EVENT -> {
                        val issue = event.payload?.issue ?: return@clickable
                        val repoFullName = (event.repo?.fullName
                            ?: event.repo?.name
                            ?: return@clickable
                                ).split("/")

                        if (repoFullName.size < 2) {
                            return@clickable
                        }

                        if (event.payload?.comment?.htmlUrl?.contains("pull") == true) {
                            navController.navigate(
                                route = Screen.Issue.route
                                    .replace(
                                        "{${Screen.ARG_PROFILE_LOGIN}}",
                                        repoFullName[0]
                                    )
                                    .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoFullName[1])
                                    .replace(
                                        "{${Screen.ARG_ISSUE_PR_NUMBER}}",
                                        issue.number.toString()
                                    )
                            )
                        }
                    }
                    Event.MEMBER_EVENT -> {
                        navigateToProfileScreen(
                            navController = navController,
                            login = event.payload?.member?.login ?: return@clickable,
                        )
                    }
                    Event.PULL_REQUEST_EVENT -> {

                    }
                    Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {

                    }
                    Event.PULL_REQUEST_REVIEW_EVENT -> {

                    }
                    Event.REPOSITORY_EVENT -> {
                        when (event.payload?.action) {
                            "created",
                            "archived",
                            "publicized",
                            "unarchived" -> {
                                navigateToRepositoryScreen(
                                    navController = navController,
                                    fullName = event.repo?.name ?: return@clickable,
                                    org = event.org
                                )
                            }
                            "privatized",
                            "deleted" -> {
                                // ignore
                            }
                            else -> {
                                // do nothing
                            }
                        }
                    }
                    Event.PUSH_EVENT -> {

                    }
                    Event.RELEASE_EVENT -> {

                    }
                    Event.ORG_BLOCK_EVENT -> {
                        navigateToProfileScreen(
                            navController = navController,
                            login = event.payload?.blockedUser?.login ?: return@clickable
                        )
                    }
                    Event.PROJECT_CARD_EVENT -> {

                    }
                    Event.PROJECT_COLUMN_EVENT -> {

                    }
                    Event.ORGANIZATION_EVENT -> {
                        navigateToProfileScreen(
                            navController = navController,
                            login = event.payload?.organization?.login ?: return@clickable,
                            profileType = ProfileType.ORGANIZATION
                        )
                    }
                    Event.PROJECT_EVENT -> {

                    }
                    Event.DOWNLOAD_EVENT,
                    Event.FOLLOW_EVENT,
                    Event.GIST_EVENT,
                    Event.FORK_APPLY_EVENT -> {
                        // Events of these types are no longer delivered, just ignore them.
                    }
                }
            }
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            request = createAvatarLoadRequest(url = event.actor.avatarUrl),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable {
                    navigateToProfileScreen(
                        navController = navController,
                        login = event.actor.login
                    )
                }
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = eventContent(event = event),
                    style = MaterialTheme.typography.subtitle1
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                EventDetailedText(event)
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        event.createdAt.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString(),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
private fun eventContent(event: Event): AnnotatedString {
    return buildAnnotatedString {
        AppendPrimaryColoredAnnotatedString(
            builder = this,
            textToAppend = event.actor.login
        )

        when (event.type) {
            Event.WATCH_EVENT -> {
                // Currently, event.payload.action can only be "started".

                append(stringResource(id = R.string.event_star))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github starred github/github
            }
            Event.CREATE_EVENT -> {
                // event.payload.refType -> The object that was created. Can be one of "repository", "branch", or "tag"
                append(
                    stringResource(
                        R.string.event_create,
                        stringResource(
                            when (event.payload?.refType) {
                                "repository" -> {
                                    R.string.event_create_type_repository
                                }
                                "branch" -> {
                                    R.string.event_create_type_branch
                                }
                                // including "tag"
                                else -> {
                                    R.string.event_create_type_tag
                                }
                            }
                        )
                    )
                )

                // event.payload.ref -> The git ref (or null if only a repository was created).
                event.payload?.ref?.let {
                    append(it)
                    append(stringResource(id = R.string.event_at))
                }

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github created repository github/github
            }
            Event.COMMIT_COMMENT_EVENT -> {
                append(stringResource(id = R.string.event_comment_on_commit))

                event.payload?.commitComment?.commitId?.let {
                    append(it.substring(0, 8))
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github commented on commit ec7a2824 at github/github
            }
            Event.DOWNLOAD_EVENT -> {
                append(stringResource(id = R.string.event_download))

                event.payload?.download?.name?.let {
                    append(it)
                }

                event.repo?.let {
                    append(stringResource(id = R.string.event_at))

                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github downloaded logo.jpe at github/github
            }
            Event.FOLLOW_EVENT -> {
                append(stringResource(id = R.string.event_follow))

                event.payload?.target?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.login
                    )
                }

                // final string example: github followed octocat
            }
            Event.FORK_EVENT -> {
                append(stringResource(id = R.string.event_fork))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                append(stringResource(id = R.string.event_to))

                // event.payload.forkee -> The created repository.
                event.payload?.forkee?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.fullName ?: it.name
                    )
                }

                // final string example: github forked actocat/Hello-World to github/Hello-World
            }
            Event.GIST_EVENT -> {
                // event.payload.action -> The action that was performed. Can be "create" or "update".

                append(
                    stringResource(
                        id = R.string.event_gist,
                        stringResource(
                            id = when (event.payload?.action) {
                                "create" -> {
                                    R.string.event_gist_action_created
                                }
                                // including "update".
                                else -> {
                                    R.string.event_gist_action_updated
                                }
                            }
                        )
                    )
                )

                event.payload?.gist?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.description
                    )
                }

                // final string example: github created Gist Hello World Examples
            }
            Event.GOLLUM_EVENT -> {
                // event.payload.pages[][action] -> The action that was performed on the page. Can be "created" or "edited".

                append(
                    stringResource(
                        id = R.string.event_gollum_event,
                        stringResource(
                            id = when (event.payload?.pages?.firstOrNull()?.action) {
                                "created" -> {
                                    R.string.event_gollum_event_action_created
                                }
                                // including "edited"
                                else -> {
                                    R.string.event_gollum_event_action_edited
                                }
                            }
                        )
                    )
                )

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github edit a wiki page at github/github
            }
            Event.ISSUE_COMMENT_EVENT -> {
                // event.payload.action -> The action that was performed on the comment.

                append(
                    stringResource(
                        id = R.string.event_issue_comment,
                        stringResource(
                            // Can be one of "created", "edited", or "deleted".
                            id = when (event.payload?.action) {
                                "created" -> {
                                    R.string.event_issue_comment_action_created
                                }
                                "edited" -> {
                                    R.string.event_issue_comment_action_edited
                                }
                                else -> {
                                    R.string.event_issue_comment_action_deleted
                                }
                            }
                        )
                    )
                )

                event.payload?.issue?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = stringResource(id = R.string.issue_pr_number, it.number)
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github commented on issue #1 at github/github
            }
            Event.ISSUES_EVENT -> {
                // event.payload.action -> The action that was performed. Can be one of "opened",
                // "edited", "deleted", "transferred", "pinned", "unpinned", "closed", "reopened",
                // "assigned", "unassigned", "labeled", "unlabeled", "milestoned", or "demilestoned".

                append(
                    stringResource(
                        id = R.string.event_issue,
                        stringResource(
                            id = when (event.payload?.action) {
                                "opened" -> {
                                    R.string.event_issue_action_opened
                                }
                                "edited" -> {
                                    R.string.event_issue_action_edited
                                }
                                "deleted" -> {
                                    R.string.event_issue_action_deleted
                                }
                                "transferred" -> {
                                    R.string.event_issue_action_transferred
                                }
                                "pinned" -> {
                                    R.string.event_issue_action_pinned
                                }
                                "unpinned" -> {
                                    R.string.event_issue_action_unpinned
                                }
                                "closed" -> {
                                    R.string.event_issue_action_closed
                                }
                                "reopened" -> {
                                    R.string.event_issue_action_reopened
                                }
                                "assigned" -> {
                                    R.string.event_issue_action_assigned
                                }
                                "unassigned" -> {
                                    R.string.event_issue_action_unassigned
                                }
                                "labeled" -> {
                                    R.string.event_issue_action_labeled
                                }
                                "unlabeled" -> {
                                    R.string.event_issue_action_unlabeled
                                }
                                "milestoned" -> {
                                    R.string.event_issue_action_milestoned
                                }
                                // including "demilestoned"
                                else -> {
                                    R.string.event_issue_action_demilestoned
                                }
                            }
                        )
                    )
                )

                event.payload?.issue?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = stringResource(id = R.string.issue_pr_number, it.number)
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
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

                append(stringResource(id = actionStringResId))

                event.payload?.member?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.login
                    )
                }

                append(stringResource(id = toOrFromStringResId))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github added octocat at github/github
            }
            Event.PUBLIC_EVENT -> {
                append(stringResource(id = R.string.event_publicized))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github open-sourced github/github
            }
            Event.PULL_REQUEST_EVENT -> {
                append(
                    stringResource(
                        id = R.string.event_pull_request,
                        stringResource(
                            // event.payload.action -> The action that was performed. Can be one of "assigned",
                            // "unassigned", "review_requested", "review_request_removed", "labeled", "unlabeled",
                            // "opened", "edited", "closed", or "reopened".
                            id = when (event.payload?.action) {
                                "assigned" -> {
                                    R.string.event_pull_request_action_assigned
                                }
                                "unassigned" -> {
                                    R.string.event_pull_request_action_unassigned
                                }
                                "review_requested" -> {
                                    R.string.event_pull_request_action_review_requested
                                }
                                "review_request_removed" -> {
                                    R.string.event_pull_request_action_review_request_removed
                                }
                                "labeled" -> {
                                    R.string.event_pull_request_action_labeled
                                }
                                "unlabeled" -> {
                                    R.string.event_pull_request_action_unlabeled
                                }
                                "opened" -> {
                                    R.string.event_pull_request_action_opened
                                }
                                "edited" -> {
                                    R.string.event_pull_request_action_edited
                                }
                                "closed" -> {
                                    R.string.event_pull_request_action_closed
                                }
                                "reopened" -> {
                                    R.string.event_pull_request_action_reopened
                                }
                                // including "synchronize"
                                else -> {
                                    R.string.event_pull_request_action_synchronized
                                }
                            }
                        )
                    )
                )

                event.payload?.pullRequest?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = stringResource(id = R.string.issue_pr_number, it.number)
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github opened a pull request #1 at github/github
            }
            Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {
                append(
                    stringResource(
                        id = R.string.event_pull_request_review_comment,
                        stringResource(
                            // event.payload.action -> The action that was performed on the comment.
                            // Can be one of "created", "edited", or "deleted".
                            id = when (event.payload?.action) {
                                "created" -> {
                                    R.string.event_pull_request_review_comment_action_created
                                }
                                "edited" -> {
                                    R.string.event_pull_request_review_comment_action_edited
                                }
                                // including "deleted"
                                else -> {
                                    R.string.event_pull_request_review_comment_action_deleted
                                }
                            }
                        )
                    )
                )

                event.payload?.pullRequest?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = stringResource(id = R.string.issue_pr_number, it.number)
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github commented on pull request #1 at github/github
            }
            Event.PULL_REQUEST_REVIEW_EVENT -> {
                // event.payload.action -> The action that was performed.
                // Can be "submitted", "edited", or "dismissed".

                append(
                    stringResource(
                        id = R.string.event_pull_request_review,
                        stringResource(
                            id = when (event.payload?.action) {
                                "submitted" -> {
                                    R.string.event_pull_request_review_action_submitted
                                }
                                "edited" -> {
                                    R.string.event_pull_request_review_action_edited
                                }
                                // including "dismissed"
                                else -> {
                                    R.string.event_pull_request_review_action_dismissed
                                }
                            }
                        )
                    )
                )

                event.payload?.pullRequest?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = stringResource(id = R.string.issue_pr_number, it.number)
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github reviewed pull request #1 at github/github
            }
            Event.REPOSITORY_EVENT -> {
                // event.payload.action -> The action that was performed.
                // This can be one of "created", "deleted" (organization hooks only), "archived", "unarchived", "publicized", or "privatized".

                append(
                    stringResource(
                        id = R.string.event_repository,
                        stringResource(
                            id = when (event.payload?.action) {
                                "created" -> {
                                    R.string.event_repository_action_created
                                }
                                "deleted" -> {
                                    R.string.event_repository_action_deleted
                                }
                                "archived" -> {
                                    R.string.event_repository_action_archived
                                }
                                "unarchived" -> {
                                    R.string.event_repository_action_unarchived
                                }
                                "publicized" -> {
                                    R.string.event_repository_action_publicized
                                }
                                // including "privatized"
                                else -> {
                                    R.string.event_repository_action_privatized
                                }
                            }
                        )
                    )
                )

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github created repository github/github
            }
            Event.PUSH_EVENT -> {
                append(stringResource(id = R.string.event_push))

                // event.payload.ref -> The full Git ref that was pushed. Example: refs/heads/master.
                val ref = event.payload?.ref
                append(
                    when {
                        ref?.startsWith("refs/heads/") == true -> {
                            ref.substring(11)
                        }
                        ref?.startsWith("refs/tags/") == true -> {
                            ref.substring(10)
                        }
                        else -> {
                            ref ?: ""
                        }
                    }
                )

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github pushed 1 commit(s) to github/github
            }
            Event.TEAM_ADD_EVENT -> {
                append(stringResource(id = R.string.event_team_add))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github added repository github/github
            }
            Event.DELETE_EVENT -> {
                // event.payload.refType -> The object that was deleted. Can be "branch" or "tag".

                append(
                    stringResource(
                        id = R.string.event_delete,
                        stringResource(
                            id = when (event.payload?.refType) {
                                "branch" -> {
                                    R.string.event_delete_type_branch
                                }
                                // including "tag"
                                else -> {
                                    R.string.event_delete_type_tag
                                }
                            }
                        )
                    )
                )

                // event.payload?.ref -> The full git ref.
                event.payload?.ref?.let {
                    append(it)
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github deleted branch dev at github/github
            }
            Event.RELEASE_EVENT -> {
                // event.payload.action -> The action that was performed. Currently, can only be "published".

                append(stringResource(id = R.string.event_release))

                event.payload?.release?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.tagName
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github released v1.1 at github/github
            }
            Event.FORK_APPLY_EVENT -> {
                append(stringResource(id = R.string.event_fork_apply))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github applied a patch at github/github
            }
            Event.ORG_BLOCK_EVENT -> {
                // event.payload.action -> The action performed. Can be "blocked" or "unblocked".
                append(
                    stringResource(
                        id = when (event.payload?.action) {
                            "blocked" -> {
                                R.string.event_org_block_type_block
                            }
                            // including "unblocked"
                            else -> {
                                R.string.event_org_block_type_unblock
                            }
                        }
                    )
                )

                // event.payload.blockedUser -> Information about the user that was blocked or unblocked.
                event.payload?.blockedUser?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.login
                    )
                }

                // final string example: github blocked octocat
            }
            Event.PROJECT_CARD_EVENT -> {
                // event.payload.action -> The action performed on the project card.
                // Can be "created", "updated", "moved", "converted", or "deleted".
                append(
                    stringResource(
                        id = R.string.event_project_card,
                        stringResource(
                            id = when (event.payload?.action) {
                                "created" -> {
                                    R.string.event_project_card_action_created
                                }
                                "updated" -> {
                                    R.string.event_project_card_action_updated
                                }
                                "moved" -> {
                                    R.string.event_project_card_action_moved
                                }
                                "converted" -> {
                                    R.string.event_project_card_action_converted
                                }
                                // including "deleted"
                                else -> {
                                    R.string.event_project_card_action_deleted
                                }
                            }
                        )
                    )
                )

                append(event.payload?.projectCard?.note ?: "")

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github created a project card to-do at github/github
            }
            Event.PROJECT_COLUMN_EVENT -> {
                // event.payload.action -> The action that was performed on the project column.
                // Can be one of "created", "edited", "moved" or "deleted".
                append(
                    stringResource(
                        id = R.string.event_project_column,
                        stringResource(
                            id = when (event.payload?.action) {
                                "created" -> {
                                    R.string.event_project_column_created
                                }
                                "updated" -> {
                                    R.string.event_project_column_updated
                                }
                                "moved" -> {
                                    R.string.event_project_column_moved
                                }
                                // including "deleted"
                                else -> {
                                    R.string.event_project_column_deleted
                                }
                            }
                        )
                    )
                )

                event.payload?.projectColumn?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
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

                append(stringResource(id = actionStringResId))

                // event.payload.membership -> The membership between the user and the organization.
                // Not present when the action is "member_invited".
                event.payload?.membership?.user?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.login
                    )
                }

                append(stringResource(id = toOrFromId))

                // event.payload.organization -> The organization in question.
                event.payload?.organization?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.login
                    )
                }

                // final string example: octocat invited tonnyl to github
            }
            Event.PROJECT_EVENT -> {
                // event.payload.action -> The action that was performed on the project.
                // Can be one of "created", "edited", "closed", "reopened", or "deleted".

                append(
                    stringResource(
                        id = when (event.payload?.action) {
                            "created" -> {
                                R.string.event_project_created
                            }
                            "edited" -> {
                                R.string.event_project_edited
                            }
                            "closed" -> {
                                R.string.event_project_closed
                            }
                            "reopened" -> {
                                R.string.event_project_reopened
                            }
                            // including "deleted"
                            else -> {
                                R.string.event_project_deleted
                            }
                        }
                    )
                )

                event.payload?.project?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                append(stringResource(id = R.string.event_at))

                event.repo?.let {
                    AppendPrimaryColoredAnnotatedString(
                        builder = this,
                        textToAppend = it.name
                    )
                }

                // final string example: github created a project Space 2.0 at github/github
            }
        }
    }
}

@Composable
private fun AppendPrimaryColoredAnnotatedString(
    builder: AnnotatedString.Builder,
    textToAppend: String
) {
    builder.append(
        AnnotatedString(
            text = textToAppend,
            spanStyle = SpanStyle(
                color = MaterialTheme.colors.primary
            )
        )
    )
}

@Composable
private fun EventDetailedText(event: Event) {
    when (event.type) {
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
    }?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.caption
        )
    }
}

private fun navigateToRepositoryScreen(
    navController: NavController,
    fullName: String,
    org: EventOrg?
) {
    val loginAndRepoName = fullName.split("/")
    val login = loginAndRepoName.getOrNull(0) ?: return
    val repoName = loginAndRepoName.getOrNull(1) ?: return

    navController.navigate(
        route = Screen.Repository.route
            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
            .replace(
                "{${Screen.ARG_PROFILE_TYPE}}",
                getUserProfileType(
                    org = org,
                    login = login
                ).name
            )
    )
}

private fun navigateToProfileScreen(
    navController: NavController,
    login: String,
    profileType: ProfileType = ProfileType.USER
) {
    navController.navigate(
        route = Screen.Profile.route
            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
            .replace("{${Screen.ARG_PROFILE_TYPE}}", profileType.name)
    )
}

private fun getUserProfileType(org: EventOrg?, login: String): ProfileType {
    if (org?.login == login) {
        return ProfileType.ORGANIZATION
    }

    return ProfileType.USER
}

@Preview(showBackground = true, name = "TimelineItemPreview")
@Composable
private fun TimelineItemPreview(
    @PreviewParameter(
        provider = TimelineEventProvider::class,
        limit = 1
    )
    event: Event
) {
    ItemTimelineEvent(
        event = event,
        navController = rememberNavController()
    )
}