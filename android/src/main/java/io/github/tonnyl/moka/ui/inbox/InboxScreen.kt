package io.github.tonnyl.moka.ui.inbox

import android.app.Application
import android.text.format.DateUtils
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.NotificationReasons
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.SubjectType
import io.tonnyl.moka.common.db.data.Notification
import io.tonnyl.moka.common.db.data.NotificationRepositoryOwner
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.NotificationProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
fun InboxScreen(openDrawer: (() -> Unit)?) {
    val currentAccount = LocalAccountInstance.current ?: return
    val app = LocalContext.current.applicationContext as Application
    val inboxViewModel = viewModel(
        key = currentAccount.toString(),
        initializer = {
            InboxViewModel(
                extra = InboxViewModelExtra(accountInstance = currentAccount),
                app = app
            )
        }
    )

    val navController = LocalNavController.current

    val isNeedDisplayPlaceholder by inboxViewModel.isNeedDisplayPlaceholderLiveData.observeAsState()

    val notifications = inboxViewModel.notificationsFlow.collectAsLazyPagingItems()

    var pendingJumpAction by inboxViewModel.pendingJumpState
    pendingJumpAction?.let { (login, repoName, tagName) ->
        Screen.Release.navigate(
            navController = navController,
            login = login,
            repoName = repoName,
            tagName = tagName
        )

        pendingJumpAction = null
    }

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        ) {
            val contentPaddings = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = notifications.loadState.refresh is LoadState.Loading),
                onRefresh = notifications::refresh,
                indicatorPadding = contentPaddings,
                indicator = { state, refreshTriggerDistance ->
                    DefaultSwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance
                    )
                }
            ) {
                when {
                    notifications.loadState.refresh is LoadState.NotLoading
                            && notifications.loadState.append is LoadState.NotLoading
                            && notifications.loadState.prepend is LoadState.NotLoading
                            && notifications.itemCount == 0 -> {

                    }
                    notifications.loadState.refresh is LoadState.NotLoading
                            && notifications.itemCount == 0 -> {
                        EmptyScreenContent(
                            titleId = R.string.common_no_data_found,
                            action = notifications::retry
                        )
                    }
                    notifications.loadState.refresh is LoadState.Error
                            && notifications.itemCount == 0 -> {
                        EmptyScreenContent(action = notifications::retry)
                    }
                    else -> {
                        InboxScreenContent(
                            contentPaddings = contentPaddings,
                            notifications = notifications,
                            enablePlaceholder = isNeedDisplayPlaceholder == true,
                            viewModel = inboxViewModel
                        )
                    }
                }
            }

            val releaseData by inboxViewModel.releaseData.observeAsState()
            if (releaseData?.status == Status.ERROR) {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    dismissAction = inboxViewModel::onReleaseDataErrorDismissed
                )
            }
        }

        MainSearchBar(
            openDrawer = openDrawer,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }
}

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
private fun InboxScreenContent(
    contentPaddings: PaddingValues,
    notifications: LazyPagingItems<Notification>,
    enablePlaceholder: Boolean,
    viewModel: InboxViewModel
) {
    val notificationPlaceholder = remember {
        NotificationProvider().values.elementAt(0)
    }

    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = notifications.loadState.prepend)

        if (enablePlaceholder) {
            items(count = defaultPagingConfig.initialLoadSize) { index ->
                if (index == 0) {
                    ListSubheader(
                        text = stringResource(id = R.string.navigation_menu_inbox),
                        enablePlaceholder = true
                    )
                }

                ItemNotification(
                    item = notificationPlaceholder,
                    enablePlaceholder = true,
                    viewModel = viewModel
                )
            }
        } else {
            itemsIndexed(
                items = notifications,
                key = { _, item ->
                    item.id
                }
            ) { index, item ->
                if (index == 0) {
                    ListSubheader(
                        text = stringResource(id = R.string.navigation_menu_inbox),
                        enablePlaceholder = false
                    )
                }

                if (item != null) {
                    ItemNotification(
                        item = item,
                        enablePlaceholder = false,
                        viewModel = viewModel
                    )
                }
            }
        }

        ItemLoadingState(loadState = notifications.loadState.append)
    }
}

@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
private fun ItemNotification(
    item: Notification,
    enablePlaceholder: Boolean,
    viewModel: InboxViewModel
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                val repoFullName = item.repository.fullName.split("/")
                if (repoFullName.size < 2) {
                    return@clickable
                }
                val login = repoFullName[0]
                val repoName = repoFullName[1]

                when (item.subject.type) {
                    SubjectType.PullRequest.toString(),
                    SubjectType.Issue.toString() -> {
                        // url example: https://api.github.com/repos/google/accompanist/pulls/1036
                        val number = item.subject.url.split("/").lastOrNull()?.toIntOrNull() ?: return@clickable
                        if (item.subject.type == SubjectType.PullRequest.toString()) {
                            Screen.PullRequest.navigate(
                                navController = navController,
                                login = login,
                                repoName = repoName,
                                number = number
                            )
                        } else {
                            Screen.Issue.navigate(
                                navController = navController,
                                login = login,
                                repoName = repoName,
                                number = number
                            )
                        }
                    }
                    SubjectType.Release.toString() -> {
                        viewModel.fetchReleaseData(
                            url = item.subject.url,
                            login = login,
                            repoName = repoName
                        )
                    }
                    else -> {
                        Screen.Repository.navigate(
                            navController = navController,
                            login = login,
                            repoName = repoName
                        )
                    }
                }
            }
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberImagePainter(
                data = item.repository.owner.avatarUrl,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    Screen.Profile.navigate(
                        navController = navController,
                        login = item.repository.owner.login,
                        type = getRepositoryOwnerType(item.repository.owner)
                    )
                }
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = item.repository.fullName,
                    style = MaterialTheme.typography.subtitle1,
                    color = MaterialTheme.colors.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontWeight = if (item.unread) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier
                        .clickable {
                            Screen.Repository.navigate(
                                navController = navController,
                                login = item.repository.owner.login,
                                repoName = item.repository.name
                            )
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
            }
            if (enablePlaceholder) {
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = notificationText(item),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                if (enablePlaceholder) {
                    Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                }
                Text(
                    text = DateUtils.getRelativeTimeSpanString(
                        item.updatedAt.toEpochMilliseconds(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                    ).toString(),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
        }
    }
}

@Composable
private fun notificationText(notification: Notification): AnnotatedString {
    val reasonText = stringResource(
        id = when (notification.reason) {
            NotificationReasons.ASSIGN -> {
                R.string.notification_reason_assign
            }
            NotificationReasons.AUTHOR -> {
                R.string.notification_reason_author
            }
            NotificationReasons.COMMENT -> {
                R.string.notification_reason_comment
            }
            NotificationReasons.INVITATION -> {
                R.string.notification_reason_invitation
            }
            NotificationReasons.MANUAL -> {
                R.string.notification_reason_manual
            }
            NotificationReasons.MENTION -> {
                R.string.notification_reason_mention
            }
            NotificationReasons.REVIEW_REQUESTED -> {
                R.string.notification_reason_review_requested
            }
            NotificationReasons.STATE_CHANGE -> {
                R.string.notification_reason_state_change
            }
            NotificationReasons.SUBSCRIBED -> {
                R.string.notification_reason_subscribed
            }
            NotificationReasons.TEAM_MENTION -> {
                R.string.notification_reason_team_mention
            }
            else -> {
                R.string.notification_reason_other
            }
        }
    )

    val notificationReasonPlusHyphen = stringResource(
        id = R.string.notification_caption_formatted,
        reasonText,
        notification.subject.title
    )

    return AnnotatedString(
        text = notificationReasonPlusHyphen,
        spanStyles = listOf(
            AnnotatedString.Range(
                SpanStyle(color = MaterialTheme.colors.primary),
                0,
                reasonText.length
            )
        )
    )
}

private fun getRepositoryOwnerType(owner: NotificationRepositoryOwner): ProfileType {
    return when (owner.type) {
        "Organization" -> {
            ProfileType.ORGANIZATION
        }
        "User" -> {
            ProfileType.USER
        }
        else -> {
            ProfileType.NOT_SPECIFIED
        }
    }
}