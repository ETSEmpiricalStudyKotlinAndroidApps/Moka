package io.github.tonnyl.moka.ui.repositories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
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
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repositories.RepositoriesQueryOption.*
import io.github.tonnyl.moka.ui.repositories.RepositoriesViewModel.Companion.REPOSITORIES_VIEW_MODEL_EXTRA_KEY
import io.github.tonnyl.moka.ui.repositories.filters.RepositoryFiltersSheet
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.RepositoryItemProvider
import io.tonnyl.moka.common.util.formatWithSuffix
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import io.tonnyl.moka.graphql.type.*
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun RepositoriesScreen(
    login: String,
    repoName: String?,
    repositoryType: RepositoryType
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val queryOptionState = remember {
        mutableStateOf(
            when (repositoryType) {
                RepositoryType.STARRED -> {
                    Starred(
                        order = StarOrder(
                            direction = OrderDirection.DESC,
                            field = StarOrderField.STARRED_AT
                        )
                    )
                }
                RepositoryType.OWNED -> {
                    Owned(
                        isAffiliationCollaborator = false,
                        isAffiliationOwner = true,
                        order = RepositoryOrder(
                            direction = OrderDirection.DESC,
                            field = RepositoryOrderField.PUSHED_AT
                        ),
                        privacy = null
                    )
                }
                RepositoryType.FORKS -> {
                    Forks(
                        order = RepositoryOrder(
                            direction = OrderDirection.DESC,
                            field = RepositoryOrderField.PUSHED_AT
                        )
                    )
                }
            }
        )
    }
    val queryOption by queryOptionState

    val viewModel = viewModel<RepositoriesViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[REPOSITORIES_VIEW_MODEL_EXTRA_KEY] = RepositoriesViewModelExtra(
                accountInstance = currentAccount,
                login = login,
                repoName = repoName,
                queryOption = queryOption
            )
        },
        key = queryOption.toString()
    )

    val repositories = viewModel.repositoriesFlow.collectAsLazyPagingItems()

    // only for ui
    val affiliationCollaboratorState =
        remember { mutableStateOf((queryOption as? Owned)?.isAffiliationCollaborator == true) }
    val affiliationOwnerState =
        remember { mutableStateOf((queryOption as? Owned)?.isAffiliationOwner == true) }
    val repositoryOrderDirectionState =
        remember {
            mutableStateOf(
                (queryOption as? Owned)?.order?.direction
                    ?: (queryOption as? Forks)?.order?.direction
            )
        }
    val orderFieldState =
        remember {
            mutableStateOf(
                (queryOption as? Owned)?.order?.field ?: (queryOption as? Forks)?.order?.field
            )
        }
    val privacyState =
        remember { mutableStateOf((queryOption as? Owned)?.privacy) }
    val starOrderDirectionState =
        remember { mutableStateOf((queryOption as? Starred)?.order?.direction) }

    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
        confirmStateChange = {
            affiliationCollaboratorState.value =
                (queryOption as? Owned)?.isAffiliationCollaborator == true
            affiliationOwnerState.value =
                (queryOption as? Owned)?.isAffiliationOwner == true
            repositoryOrderDirectionState.value =
                (queryOption as? Owned)?.order?.direction
                    ?: (queryOption as? Forks)?.order?.direction
            orderFieldState.value =
                (queryOption as? Owned)?.order?.field ?: (queryOption as? Forks)?.order?.field
            privacyState.value = (queryOption as? Owned)?.privacy
            starOrderDirectionState.value = (queryOption as? Starred)?.order?.direction

            true
        }
    )
    val coroutineScope = rememberCoroutineScope()

    var topAppBarSize by remember { mutableStateOf(0) }

    val contentPaddings = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.systemBars,
        applyTop = false,
        additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
    )

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            RepositoryFiltersSheet(
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                queryOptionState = queryOptionState,
                affiliationCollaboratorState = affiliationCollaboratorState,
                affiliationOwnerState = affiliationOwnerState,
                ownedOrderDirectionState = repositoryOrderDirectionState,
                orderFieldState = orderFieldState,
                privacyState = privacyState,
                starOrderDirectionState = starOrderDirectionState
            )
        }
    ) {
        Box {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = repositories.loadState.refresh is LoadState.Loading),
                onRefresh = repositories::refresh,
                indicatorPadding = contentPaddings,
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
                            contentPaddings = contentPaddings,
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
                                    R.string.profile_stars
                                }
                                RepositoryType.OWNED -> {
                                    R.string.profile_repositories
                                }
                                RepositoryType.FORKS -> {
                                    R.string.repository_forks
                                }
                            }
                        )
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
                actions = {
                    if (repositories.itemCount > 0) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.show()
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter_24),
                                contentDescription = stringResource(id = R.string.notification_filters)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { topAppBarSize = it.height }
            )
        }

        if (bottomSheetState.isVisible) {
            BackHandler {
                coroutineScope.launch {
                    bottomSheetState.hide()
                }
            }
        }
    }
}

@ExperimentalSerializationApi
@Composable
private fun RepositoriesScreenContent(
    contentPaddings: PaddingValues,
    repositories: LazyPagingItems<RepositoryListItemFragment>
) {
    val repoPlaceholder = remember {
        RepositoryItemProvider().values.first()
    }
    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = repositories.loadState.prepend)

        if (repositories.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
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

        ItemLoadingState(loadState = repositories.loadState.append)
    }
}

@Composable
fun ItemRepository(
    repo: RepositoryListItemFragment,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                Screen.Repository.navigate(
                    navController = navController,
                    login = repo.repositoryOwner.repositoryOwner.login,
                    repoName = repo.name
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = repo.repositoryOwner.repositoryOwner.avatarUrl,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    Screen.Profile.navigate(
                        navController = navController,
                        login = repo.repositoryOwner.repositoryOwner.login,
                        type = ProfileType.NOT_SPECIFIED
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
                    text = repo.description
                        ?: stringResource(id = R.string.no_description_provided),
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
                                color = repo.primaryLanguage?.language?.color
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
                        text = repo.primaryLanguage?.language?.name
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
                        text = repo.stargazers.totalCount.formatWithSuffix(),
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
                        text = repo.forks.totalCount.formatWithSuffix(),
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
    repo: RepositoryListItemFragment
) {
    ItemRepository(
        repo = repo,
        enablePlaceholder = false
    )
}