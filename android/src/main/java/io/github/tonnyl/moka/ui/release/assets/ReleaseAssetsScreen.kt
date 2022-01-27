package io.github.tonnyl.moka.ui.release.assets

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
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
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.ReleaseAssetProvider
import io.tonnyl.moka.graphql.fragment.ReleaseAsset
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun ReleaseAssetsScreen(
    login: String,
    repoName: String,
    tagName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<ReleaseAssetsViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[ReleaseAssetsViewModel.RELEASES_VIEW_MODEL_EXTRA_KEY] =
                ReleaseAssetsViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repoName = repoName,
                    tagName = tagName
                )
        }
    )

    val assets = viewModel.assets.collectAsLazyPagingItems()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            content = {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = assets.loadState.refresh is LoadState.Loading),
                    onRefresh = assets::refresh,
                    indicatorPadding = contentPadding,
                    indicator = { state, refreshTriggerDistance ->
                        DefaultSwipeRefreshIndicator(
                            state = state,
                            refreshTriggerDistance = refreshTriggerDistance
                        )
                    }
                ) {
                    when {
                        assets.loadState.refresh is LoadState.NotLoading
                                && assets.loadState.append is LoadState.NotLoading
                                && assets.loadState.prepend is LoadState.NotLoading
                                && assets.itemCount == 0 -> {

                        }
                        assets.loadState.refresh is LoadState.NotLoading
                                && assets.itemCount == 0 -> {
                            EmptyScreenContent(
                                icon = R.drawable.ic_menu_timeline_24,
                                title = R.string.timeline_content_empty_title,
                                retry = R.string.common_retry,
                                action = R.string.timeline_content_empty_action
                            )
                        }
                        assets.loadState.refresh is LoadState.Error
                                && assets.itemCount == 0 -> {
                            EmptyScreenContent(
                                icon = R.drawable.ic_menu_inbox_24,
                                title = R.string.common_error_requesting_data,
                                retry = R.string.common_retry,
                                action = R.string.notification_content_empty_action
                            )
                        }
                        else -> {
                            ReleaseAssetsScreenContent(
                                contentTopPadding = contentPadding.calculateTopPadding(),
                                assets = assets
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Snackbar(snackbarData = data)
                }
            },
            scaffoldState = scaffoldState
        )

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.release_assets)) },
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

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
private fun ReleaseAssetsScreenContent(
    contentTopPadding: Dp,
    assets: LazyPagingItems<ReleaseAsset>,
) {
    val assetPlaceholder = remember {
        ReleaseAssetProvider().values.elementAt(0)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

        item {
            ItemLoadingState(loadState = assets.loadState.prepend)
        }

        if (assets.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemReleaseAsset(
                    asset = assetPlaceholder,
                    enablePlaceholder = true
                )
            }
        } else {
            itemsIndexed(
                items = assets,
                key = { _, item ->
                    item.id
                }
            ) { _, item ->
                if (item != null) {
                    ItemReleaseAsset(
                        asset = item,
                        enablePlaceholder = false
                    )
                }
            }
        }

        item {
            ItemLoadingState(loadState = assets.loadState.append)
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ItemReleaseAsset(
    asset: ReleaseAsset,
    enablePlaceholder: Boolean
) {
    val context = LocalContext.current

    ListItem(
        trailing = {
            IconButton(
                enabled = !enablePlaceholder,
                onClick = {
                    try {
                        context.startActivity(
                            Intent.makeMainSelectorActivity(
                                Intent.ACTION_MAIN,
                                Intent.CATEGORY_APP_BROWSER
                            ).apply {
                                data = Uri.parse(asset.downloadUrl)
                                flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                            }
                        )
                    } catch (e: Exception) {
                        logcat(
                            priority = LogPriority.ERROR,
                            tag = "ItemReleaseAsset"
                        ) { "failed to open browser\n${e.asLog()}" }
                    }
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_file_download_24),
                        contentDescription = stringResource(id = R.string.download_release_asset_file),
                        tint = MaterialTheme.colors.primary,
                    )
                },
                modifier = Modifier
                    .padding(end = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        },
        secondaryText = {
            Column {
                Spacer(
                    modifier = Modifier.height(
                        height = if (enablePlaceholder) {
                            ContentPaddingSmallSize
                        } else {
                            0.dp
                        }
                    )
                )
                val sizeInB = (asset.size / 1024.0f).toInt()
                Text(
                    text = when { // B
                        asset.size < 1024 -> { // [0, 1024B)
                            stringResource(id = R.string.release_asset_size_in_bytes, asset.size)
                        }
                        sizeInB in (1 until 1024) -> { // [1KB, 1024KB)
                            stringResource(
                                id = R.string.release_asset_size_kilobytes,
                                asset.size / (1024.0f)
                            )
                        }
                        sizeInB in (1024 until 1024 * 1024) -> {
                            stringResource(
                                id = R.string.release_asset_size_megabytes,
                                asset.size / (1024 * 1024.0f)
                            )
                        }
                        else -> {
                            stringResource(
                                id = R.string.release_asset_size_gigabytes,
                                asset.size / (1024 * 1024 * 1024.0f)
                            )
                        }
                    },
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .wrapContentWidth(align = Alignment.Start)
                        .padding(end = ContentPaddingLargeSize)
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = asset.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(name = "ItemReleaseAssetPreview")
private fun ItemReleaseAssetPreview(
    @PreviewParameter(
        provider = ReleaseAssetProvider::class,
        limit = 1
    )
    asset: ReleaseAsset
) {
    ItemReleaseAsset(
        asset = asset,
        enablePlaceholder = false
    )
}