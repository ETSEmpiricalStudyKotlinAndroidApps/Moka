package io.github.tonnyl.moka.ui.repositories

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
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.RepositoryItemProvider
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.github.tonnyl.moka.widget.SwipeToRefreshLayout

@Composable
fun RepositoriesScreen(
    navController: NavController,
    login: String,
    repositoryType: RepositoryType,
    profileType: ProfileType
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

        SwipeToRefreshLayout(
            refreshingState = repositories.loadState.refresh is LoadState.Loading,
            onRefresh = repositories::refresh,
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
                        topAppBarSize = topAppBarSize,
                        navController = navController,
                        repositories = repositories,
                        profileType = profileType
                    )
                }
            }
        }

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

@Composable
private fun RepositoriesScreenContent(
    topAppBarSize: Int,
    navController: NavController,
    repositories: LazyPagingItems<RepositoryItem>,
    profileType: ProfileType
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            ItemLoadingState(loadState = repositories.loadState.prepend)
        }

        itemsIndexed(lazyPagingItems = repositories) { _, item ->
            if (item != null) {
                ItemRepository(
                    repo = item,
                    profileType = profileType,
                    navController = navController
                )
            }
        }

        item {
            ItemLoadingState(loadState = repositories.loadState.append)
        }
    }
}

@Composable
fun ItemRepository(
    repo: RepositoryItem,
    profileType: ProfileType,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.navigate(
                    route = Screen.Repository.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", repo.owner?.login ?: "ghost")
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repo.name)
                        .replace(
                            "{${Screen.ARG_PROFILE_TYPE}}",
                            profileType.name
                        )
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            request = createAvatarLoadRequest(url = repo.owner?.avatarUrl),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
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
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = repo.description ?: repo.descriptionHTML,
                    style = MaterialTheme.typography.body2,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
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
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.primaryLanguage?.name
                            ?: stringResource(id = R.string.programming_language_unknown),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Icon(
                        contentDescription = stringResource(id = R.string.repository_stargazers),
                        painter = painterResource(id = R.drawable.ic_star_secondary_text_color_18),
                        modifier = Modifier.size(size = RepositoryCardIconSize)
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.stargazersCount.formatWithSuffix(),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Icon(
                        contentDescription = stringResource(id = R.string.repository_forks),
                        painter = painterResource(id = R.drawable.ic_code_fork_secondary_text_color_18),
                        modifier = Modifier.size(size = RepositoryCardIconSize)
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                    Text(
                        text = repo.forksCount.formatWithSuffix(),
                        maxLines = 1,
                        style = MaterialTheme.typography.body2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview(name = "ItemRepositoryPreview", showBackground = true)
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
        profileType = ProfileType.USER,
        navController = rememberNavController()
    )
}