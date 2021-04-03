package io.github.tonnyl.moka.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.util.UserItemProvider
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.github.tonnyl.moka.widget.SwipeToRefreshLayout

@Composable
fun UsersScreen(
    navController: NavController,
    login: String,
    usersType: UsersType
) {
    val viewModel = viewModel<UsersViewModel>(
        factory = ViewModelFactory(login, usersType)
    )

    val usersPager by remember {
        mutableStateOf(viewModel.usersFlow)
    }
    val users = usersPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SwipeToRefreshLayout(
            refreshingState = users.loadState.refresh is LoadState.Loading,
            onRefresh = users::refresh,
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
                        topAppBarSize = topAppBarSize,
                        navController = navController,
                        users = users
                    )
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(
                    text = stringResource(
                        id = when (usersType) {
                            UsersType.FOLLOWER -> {
                                R.string.users_followers_title
                            }
                            UsersType.FOLLOWING -> {
                                R.string.users_following_title
                            }
                        },
                        login
                    )
                )
            },
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
private fun UsersScreenScreen(
    topAppBarSize: Int,
    navController: NavController,
    users: LazyPagingItems<UserItem>
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            ItemLoadingState(loadState = users.loadState.prepend)
        }

        itemsIndexed(lazyPagingItems = users) { _, item ->
            if (item != null) {
                ItemUser(
                    user = item,
                    navController = navController
                )
            }
        }

        item {
            ItemLoadingState(loadState = users.loadState.append)
        }
    }
}

@Composable
fun ItemUser(
    user: UserItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.navigate(
                    route = Screen.Profile.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                        .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.USER.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            request = createAvatarLoadRequest(url = user.avatarUrl),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            if (!user.name.isNullOrEmpty()) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = user.login,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.fillMaxWidth()
                )
                (user.bio ?: user.bioHTML).let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "ItemUserPreview", showBackground = true)
@Composable
private fun ItemUserPreview(
    @PreviewParameter(
        provider = UserItemProvider::class,
        limit = 1
    )
    user: UserItem
) {
    ItemUser(
        user = user,
        navController = rememberNavController()
    )
}