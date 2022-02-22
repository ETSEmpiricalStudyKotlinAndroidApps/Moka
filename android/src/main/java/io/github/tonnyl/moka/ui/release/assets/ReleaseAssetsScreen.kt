package io.github.tonnyl.moka.ui.release.assets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
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
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.util.downloadFileViaDownloadManager
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.ReleaseAssetProvider
import io.tonnyl.moka.graphql.fragment.ReleaseAsset
import kotlinx.serialization.ExperimentalSerializationApi

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

        val contentPaddings = rememberInsetsPaddingValues(
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
                    indicatorPadding = contentPaddings,
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
                                titleId = R.string.common_no_data_found,
                                action = assets::retry
                            )
                        }
                        assets.loadState.refresh is LoadState.Error
                                && assets.itemCount == 0 -> {
                            EmptyScreenContent(action = assets::retry)
                        }
                        else -> {
                            ReleaseAssetsScreenContent(
                                contentPaddings = contentPaddings,
                                assets = assets,
                                accessToken = currentAccount.signedInAccount.accessToken.accessToken
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.release_assets)) },
            navigationIcon = {
                AppBarNavigationIcon()
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
    contentPaddings: PaddingValues,
    assets: LazyPagingItems<ReleaseAsset>,
    accessToken: String
) {
    val assetPlaceholder = remember {
        ReleaseAssetProvider().values.elementAt(0)
    }

    LazyColumn(contentPadding = contentPaddings) {
        ItemLoadingState(loadState = assets.loadState.prepend)

        if (assets.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemReleaseAsset(
                    asset = assetPlaceholder,
                    enablePlaceholder = true,
                    accessToken = accessToken
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
                        enablePlaceholder = false,
                        accessToken = accessToken
                    )
                }
            }
        }

        ItemLoadingState(loadState = assets.loadState.append)
    }
}

@ExperimentalMaterialApi
@Composable
private fun ItemReleaseAsset(
    asset: ReleaseAsset,
    enablePlaceholder: Boolean,
    accessToken: String
) {
    val context = LocalContext.current

    ListItem(
        trailing = {
            IconButton(
                enabled = !enablePlaceholder,
                onClick = {
                    context.downloadFileViaDownloadManager(
                        url = asset.downloadUrl,
                        accessToken = accessToken
                    )
                },
                content = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_file_download_24),
                        contentDescription = stringResource(id = R.string.download_release_asset_file),
                        tint = MaterialTheme.colors.primary,
                    )
                },
                modifier = Modifier
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
        enablePlaceholder = false,
        accessToken = ""
    )
}