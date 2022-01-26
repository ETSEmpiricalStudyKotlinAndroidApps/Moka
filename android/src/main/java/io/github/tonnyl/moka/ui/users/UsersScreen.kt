package io.github.tonnyl.moka.ui.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.UserItemProvider
import io.tonnyl.moka.graphql.fragment.UserListItemFragment
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun UsersScreen(
    login: String,
    repoName: String?,
    usersType: UsersType
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<UsersViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            login = login,
            repoName = repoName,
            usersType = usersType
        )
    )

    val users = viewModel.usersFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = users.loadState.refresh is LoadState.Loading),
            onRefresh = users::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                users.loadState.refresh is LoadState.NotLoading
                        && users.loadState.append is LoadState.NotLoading
                        && users.loadState.prepend is LoadState.NotLoading
                        && users.itemCount == 0 -> {

                }
                users.loadState.refresh is LoadState.NotLoading
                        && users.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                users.loadState.refresh is LoadState.Error
                        && users.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    UsersScreenScreen(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        users = users
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(
                    text =
                    when (usersType) {
                        UsersType.FOLLOWER -> {
                            stringResource(id = R.string.users_followers_title, login)
                        }
                        UsersType.FOLLOWING -> {
                            stringResource(id = R.string.users_following_title, login)
                        }
                        UsersType.REPOSITORY_STARGAZERS -> {
                            stringResource(id = R.string.repository_stargazers)
                        }
                        UsersType.REPOSITORY_WATCHERS -> {
                            stringResource(id = R.string.repository_watchers)
                        }
                    }
                )
            },
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
private fun UsersScreenScreen(
    contentTopPadding: Dp,
    users: LazyPagingItems<UserListItemFragment>
) {
    val userPlaceholder = remember {
        UserItemProvider().values.last()
    }
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = users.loadState.prepend)
        }

        if (users.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemUser(
                    user = userPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = users,
                key = { _, item ->
                    item.id
                }
            ) { _, item ->
                if (item != null) {
                    ItemUser(
                        user = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = users.loadState.append)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ItemUser(
    user: UserListItemFragment,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                navController.navigate(
                    route = Screen.Profile.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                        .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.USER.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = user.avatarUrl,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            if (!user.name.isNullOrEmpty()) {
                Text(
                    text = user.name!!,
                    style = MaterialTheme.typography.body1,
                    modifier = if (enablePlaceholder) {
                        Modifier
                            .height(height = ContentPaddingLargeSize)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    } else {
                        Modifier.wrapContentHeight()
                    }
                )
            }
            if (enablePlaceholder) {
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.body2,
                    modifier = if (enablePlaceholder) {
                        Modifier
                            .height(height = ContentPaddingLargeSize)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    } else {
                        Modifier.wrapContentHeight()
                    }
                )
                if (enablePlaceholder) {
                    Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                }
                if (!user.bio.isNullOrEmpty()) {
                    Text(
                        text = user.bio!!,
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = if (enablePlaceholder) {
                            Modifier
                                .height(height = ContentPaddingLargeSize)
                                .placeholder(
                                    visible = enablePlaceholder,
                                    highlight = PlaceholderHighlight.fade()
                                )
                        } else {
                            Modifier.wrapContentHeight()
                        }
                    )
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Preview(
    name = "ItemUserPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ItemUserPreview(
    @PreviewParameter(
        provider = UserItemProvider::class,
        limit = 1
    )
    user: UserListItemFragment
) {
    ItemUser(
        user = user,
        enablePlaceholder = false
    )
}