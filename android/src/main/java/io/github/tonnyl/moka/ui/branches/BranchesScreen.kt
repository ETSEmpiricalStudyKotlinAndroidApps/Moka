package io.github.tonnyl.moka.ui.branches

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.BranchProvider
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.graphql.fragment.Ref
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun BranchesScreen(
    login: String,
    repoName: String,
    refPrefix: String,
    defaultBranchName: String,
    selectedBranchName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<BranchesViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            login = login,
            repoName = repoName,
            refPrefix = refPrefix
        )
    )

    val branches = viewModel.branchesFlow.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = branches.loadState.refresh is LoadState.Loading),
            onRefresh = branches::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                branches.loadState.refresh is LoadState.NotLoading
                        && branches.loadState.append is LoadState.NotLoading
                        && branches.loadState.prepend is LoadState.NotLoading
                        && branches.itemCount == 0 -> {

                }
                branches.loadState.refresh is LoadState.NotLoading
                        && branches.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                branches.loadState.refresh is LoadState.Error
                        && branches.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    BranchesScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        contentBottomPadding = contentPadding.calculateBottomPadding(),
                        branches = branches,
                        defaultBranchName = defaultBranchName,
                        selectedBranchName = selectedBranchName
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.branches))
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

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
private fun BranchesScreenContent(
    contentTopPadding: Dp,
    contentBottomPadding: Dp,
    branches: LazyPagingItems<Ref>,
    defaultBranchName: String,
    selectedBranchName: String
) {
    val branchPlaceholder = remember {
        BranchProvider().values.first()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = branches.loadState.prepend)
        }

        val isInitialLoading = branches.loadState.refresh is LoadState.Loading
        if (isInitialLoading) {
            items(count = MokaApp.defaultPagingConfig.initialLoadSize) {
                ItemBranch(
                    ref = branchPlaceholder,
                    isDefault = false,
                    isSelected = false,
                    enablePlaceholder = true
                )
            }
        } else {
            items(
                items = branches,
                key = { it.id }
            ) { item ->
                if (item != null) {
                    ItemBranch(
                        ref = item,
                        isDefault = defaultBranchName == item.name,
                        isSelected = selectedBranchName == item.name,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = branches.loadState.append)
        }

        item {
            Spacer(modifier = Modifier.height(height = contentBottomPadding))
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ItemBranch(
    ref: Ref,
    isDefault: Boolean,
    isSelected: Boolean,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    ListItem(
        trailing = {
            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_24),
                    contentDescription = stringResource(
                        id = R.string.branch_selected
                    )
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(Screen.Branches.RESULT_BRANCH_NAME, ref.name)
                navController.navigateUp()
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ref.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
            if (isDefault) {
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Text(
                    text = stringResource(id = R.string.branch_default),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .clip(shape = MaterialTheme.shapes.medium)
                        .background(color = MaterialTheme.colors.onBackground.copy(alpha = .1f))
                        .padding(horizontal = ContentPaddingMediumSize)
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(name = "ItemBranchPreview", showBackground = true, backgroundColor = 0xFFFFFF)
private fun ItemBranchPreview(
    @PreviewParameter(
        provider = BranchProvider::class,
        limit = 1
    )
    ref: Ref,
) {
    ItemBranch(
        ref = ref,
        isDefault = true,
        isSelected = true,
        enablePlaceholder = false
    )
}