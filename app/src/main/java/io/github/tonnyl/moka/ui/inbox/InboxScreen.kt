package io.github.tonnyl.moka.ui.inbox

import android.text.format.DateUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.NotificationReasons
import io.github.tonnyl.moka.data.NotificationRepositoryOwner
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.NotificationProvider
import io.github.tonnyl.moka.widget.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalComposeUiApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun InboxScreen(openDrawer: () -> Unit) {
    val currentAccount = LocalAccountInstance.current ?: return
    val inboxViewModel = viewModel<InboxViewModel>(
        key = currentAccount.toString(),
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            app = LocalMainViewModel.current.getApplication()
        )
    )

    val isNeedDisplayPlaceholder by inboxViewModel.isNeedDisplayPlaceholderLiveData.observeAsState()

    val notificationsPager = remember(key1 = currentAccount) {
        inboxViewModel.notificationsFlow
    }
    val notifications = notificationsPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = notifications.loadState.refresh is LoadState.Loading),
            onRefresh = notifications::refresh,
            indicatorPadding = contentPadding,
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
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.notification_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                notifications.loadState.refresh is LoadState.Error
                        && notifications.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    InboxScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        notifications = notifications,
                        enablePlaceholder = isNeedDisplayPlaceholder == true
                    )
                }
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

@ExperimentalSerializationApi
@Composable
private fun InboxScreenContent(
    contentTopPadding: Dp,
    notifications: LazyPagingItems<Notification>,
    enablePlaceholder: Boolean
) {
    val notificationPlaceholder = remember {
        NotificationProvider().values.elementAt(0)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = notifications.loadState.prepend)
        }

        if (enablePlaceholder) {
            items(count = MokaApp.defaultPagingConfig.initialLoadSize) { index ->
                if (index == 0) {
                    ListSubheader(
                        text = stringResource(id = R.string.navigation_menu_inbox),
                        enablePlaceholder = true
                    )
                }

                ItemNotification(
                    item = notificationPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(lazyPagingItems = notifications) { index, item ->
                if (index == 0) {
                    ListSubheader(
                        text = stringResource(id = R.string.navigation_menu_inbox),
                        enablePlaceholder = false
                    )
                }

                if (item != null) {
                    ItemNotification(
                        item = item,
                        enablePlaceholder = false
                    )
                }
            }
        }


        item {
            ItemLoadingState(loadState = notifications.loadState.append)
        }
    }
}

@Composable
private fun ItemNotification(
    item: Notification,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {

            }
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        Image(
            painter = rememberCoilPainter(
                request = item.repository.owner.avatarUrl,
                requestBuilder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable {
                    navController.navigate(
                        route = Screen.Profile.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", item.repository.owner.login)
                            .replace(
                                "{${Screen.ARG_PROFILE_TYPE}}",
                                getRepositoryOwnerType(item.repository.owner).name
                            )
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
                            navController.navigate(
                                route = Screen.Repository.route
                                    .replace(
                                        "{${Screen.ARG_PROFILE_LOGIN}}",
                                        item.repository.owner.login
                                    )
                                    .replace(
                                        "{${Screen.ARG_REPOSITORY_NAME}}",
                                        item.repository.name
                                    )
                                    .replace(
                                        "{${Screen.ARG_PROFILE_TYPE}}",
                                        getRepositoryOwnerType(item.repository.owner).name
                                    )
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

@Preview(showBackground = true, name = "NotificationItemPreview")
@Composable
private fun NotificationItemPreview(
    @PreviewParameter(
        provider = NotificationProvider::class,
        limit = 1
    )
    notification: Notification
) {
    ItemNotification(
        item = notification,
        enablePlaceholder = false
    )
}