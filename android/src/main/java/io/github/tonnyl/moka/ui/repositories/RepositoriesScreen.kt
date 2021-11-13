package io.github.tonnyl.moka.ui.repositories

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
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
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.RepositoryItemProvider
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun RepositoriesScreen(
    login: String,
    repositoryType: RepositoryType
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<RepositoriesViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            login = login,
            repositoryType = repositoryType
        )
    )

    val repositoriesPager = remember {
        viewModel.repositoriesFlow
    }
    val repositories = repositoriesPager.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = repositories.loadState.refresh is LoadState.Loading),
            onRefresh = repositories::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                repositories.loadState.refresh is LoadState.NotLoading
                        && repositories.loadState.append is LoadState.NotLoading
                        && repositories.loadState.prepend is LoadState.NotLoading
                        && repositories.itemCount == 0 -> {

                }
                repositories.loadState.refresh is LoadState.NotLoading
                        && repositories.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                repositories.loadState.refresh is LoadState.Error
                        && repositories.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    RepositoriesScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        repositories = repositories
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(
                    text = stringResource(
                        id = when (repositoryType) {
                            RepositoryType.STARRED -> {
                                R.string.repositories_stars
                            }
                            RepositoryType.OWNED -> {
                                R.string.repositories_owned
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
private fun RepositoriesScreenContent(
    contentTopPadding: Dp,
    repositories: LazyPagingItems<RepositoryItem>
) {
    val repoPlaceholder = remember {
        RepositoryItemProvider().values.first()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = repositories.loadState.prepend)
        }

        if (repositories.loadState.refresh is LoadState.Loading) {
            items(count = MokaApp.defaultPagingConfig.initialLoadSize) {
                ItemRepository(
                    repo = repoPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = repositories,
                key = { _, item ->
                    item.id
                }
            ) { _, item ->
                if (item != null) {
                    ItemRepository(
                        repo = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = repositories.loadState.append)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun ItemRepository(
    repo: RepositoryItem,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                navController.navigate(
                    route = Screen.Repository.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", repo.owner?.login ?: "ghost")
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repo.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = repo.owner?.avatarUrl,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    navController.navigate(
                        route = Screen.Profile.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", repo.owner?.login ?: "ghost")
                            .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.NOT_SPECIFIED.name)
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
                    text = repo.nameWithOwner,
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                if (enablePlaceholder) {
                    Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
                }
                Text(
                    text = repo.description ?: stringResource(id = R.string.no_description_provided),
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
            if (enablePlaceholder) {
                Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(size = RepositoryCardLanguageDotSize)
                            .background(
                                color = repo.primaryLanguage?.color
                                    ?.toColor()
                                    ?.let { Color(it) }
                                    ?: MaterialTheme.colors.onBackground,
                                shape = CircleShape
                            )
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.primaryLanguage?.name
                            ?: stringResource(id = R.string.programming_language_unknown),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Icon(
                        contentDescription = stringResource(id = R.string.repository_stargazers),
                        painter = painterResource(id = R.drawable.ic_star_secondary_text_color_18),
                        modifier = Modifier
                            .size(size = RepositoryCardIconSize)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.stargazersCount.formatWithSuffix(),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Icon(
                        contentDescription = stringResource(id = R.string.repository_forks),
                        painter = painterResource(id = R.drawable.ic_code_fork_secondary_text_color_18),
                        modifier = Modifier
                            .size(size = RepositoryCardIconSize)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.forksCount.formatWithSuffix(),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Preview(
    name = "ItemRepositoryPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ItemRepositoryPreview(
    @PreviewParameter(
        provider = RepositoryItemProvider::class,
        limit = 1
    )
    repo: RepositoryItem
) {
    ItemRepository(
        repo = repo,
        enablePlaceholder = false
    )
}