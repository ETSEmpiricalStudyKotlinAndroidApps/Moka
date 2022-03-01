package io.github.tonnyl.moka.ui.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
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
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.UsersType
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.UserItemProvider
import io.tonnyl.moka.graphql.fragment.UserListItemFragment
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun UsersScreen(
    login: String,
    repoName: String?,
    usersType: UsersType
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            UsersViewModel(
                extra = UsersViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repoName = repoName,
                    usersType = usersType
                )
            )
        }
    )

    val users = viewModel.usersFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = users.loadState.refresh is LoadState.Loading),
            onRefresh = users::refresh,
            indicatorPadding = contentPaddings,
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
                        titleId = R.string.common_no_data_found,
                        action = users::retry
                    )
                }
                users.loadState.refresh is LoadState.Error
                        && users.itemCount == 0 -> {
                    EmptyScreenContent(
                        action = users::retry,
                        throwable = (users.loadState.refresh as LoadState.Error).error
                    )
                }
                else -> {
                    UsersScreenScreen(
                        contentPaddings = contentPaddings,
                        users = users
                    )
                }
            }
        }

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
                AppBarNavigationIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun UsersScreenScreen(
    contentPaddings: PaddingValues,
    users: LazyPagingItems<UserListItemFragment>
) {
    val userPlaceholder = remember {
        UserItemProvider().values.last()
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = users.loadState.prepend)

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

        ItemLoadingState(loadState = users.loadState.append)
    }
}

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
                Screen.Profile.navigate(
                    navController = navController,
                    login = user.login,
                    type = ProfileType.USER
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        AvatarImage(
            url = user.avatarUrl,
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