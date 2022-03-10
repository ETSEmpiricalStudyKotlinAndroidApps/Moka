package io.github.tonnyl.moka.ui.repository.files

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.defaultPagingConfig
import io.github.tonnyl.moka.ui.media.MediaActivity
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.FileUtils
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.MediaType
import io.tonnyl.moka.common.data.TreeEntryType
import io.tonnyl.moka.common.data.treeEntryType
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.util.TreeEntryProvider
import io.tonnyl.moka.graphql.fragment.TreeEntry

@Composable
fun RepositoryFilesScreen(
    login: String,
    repoName: String,
    expression: String,
    refPrefix: String,
    defaultBranchName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            RepositoryFilesViewModel(
                extra = RepositoryFilesViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repositoryName = repoName,
                    expression = expression
                )
            )
        },
        key = expression
    )

    val entries = viewModel.entry.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = entries.value?.status == Status.LOADING),
            onRefresh = viewModel::refresh,
            indicatorPadding = contentPaddings,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            val enablePlaceholder =
                entries.value?.status == Status.LOADING && entries.value?.data.isNullOrEmpty()
            when {
                enablePlaceholder
                        || entries.value?.data != null -> {
                    RepositoryFilesScreenContent(
                        contentPaddings = contentPaddings,
                        entries = entries.value?.data.orEmpty(),
                        enablePlaceholder = enablePlaceholder,
                        login = login,
                        repoName = repoName,
                        expression = expression,
                        defaultBranchName = defaultBranchName
                    )
                }
                else -> {
                    EmptyScreenContent(
                        titleId = if (entries.value?.status == Status.ERROR) {
                            R.string.common_error_requesting_data
                        } else {
                            R.string.common_no_data_found
                        },
                        action = viewModel::refresh,
                        throwable = entries.value?.e
                    )
                }
            }
        }

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.files))
            },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            actions = {
                if (expression.endsWith(":")) {
                    val branch = expression.substring(0, expression.length - 1)
                    TextButton(
                        onClick = {
                            Screen.Branches.navigate(
                                navController = navController,
                                login = login,
                                repoName = repoName,
                                refPrefix = refPrefix,
                                defaultBranchName = defaultBranchName,
                                selectedBranchName = branch
                            )
                        }
                    ) {
                        Text(
                            text = branch,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                val path = if (expression.endsWith(":")) {
                    expression.subSequence(0, expression.length - 1)
                } else {
                    expression.replace(":", "/")
                }

                ShareAndOpenInBrowserMenu(
                    showMenuState = remember { mutableStateOf(false) },
                    text = "https://github.com/${login}/${repoName}/tree/${path}"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun RepositoryFilesScreenContent(
    contentPaddings: PaddingValues,
    entries: List<TreeEntry>,
    enablePlaceholder: Boolean,
    login: String,
    repoName: String,
    expression: String,
    defaultBranchName: String
) {
    val entryPlaceholder = remember {
        TreeEntryProvider().values.first()
    }
    LazyColumn(contentPadding = contentPaddings) {
        items(
            count = if (enablePlaceholder) {
                defaultPagingConfig.initialLoadSize
            } else {
                entries.size
            }
        ) { index ->
            ItemTreeEntry(
                treeEntry = if (enablePlaceholder) {
                    entryPlaceholder
                } else {
                    entries[index]
                },
                enablePlaceholder = enablePlaceholder,
                login = login,
                repoName = repoName,
                currentExpression = expression,
                defaultBranchName = defaultBranchName
            )
        }
    }
}

@Composable
private fun ItemTreeEntry(
    treeEntry: TreeEntry,
    enablePlaceholder: Boolean,
    login: String,
    repoName: String,
    currentExpression: String,
    defaultBranchName: String
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    ListItem(
        icon = {
            Icon(
                painter = painterResource(
                    id = when (treeEntry.treeEntryType) {
                        TreeEntryType.TREE -> {
                            R.drawable.ic_folder_filled_24
                        }
                        TreeEntryType.COMMIT -> {
                            R.drawable.ic_folder_open_24
                        }
                        else -> {
                            R.drawable.ic_insert_drive_file_24
                        }
                    }
                ),
                tint = if (treeEntry.treeEntryType != TreeEntryType.BLOB) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.onBackground
                },
                contentDescription = treeEntry.type,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        },
        modifier = Modifier.clickable {
            val currentBranch = currentExpression.split(":").first()
            when (treeEntry.treeEntryType) {
                TreeEntryType.TREE -> {
                    Screen.RepositoryFiles.navigate(
                        navController = navController,
                        login = login,
                        repoName = repoName,
                        expression = if (currentExpression.endsWith(":")) {
                            "${currentExpression}${treeEntry.name}"
                        } else {
                            "${currentExpression}/${treeEntry.name}"
                        },
                        refPrefix = currentBranch,
                        defaultBranchName = defaultBranchName
                    )
                }
                TreeEntryType.BLOB -> {
                    val filename = treeEntry.name
                    val filePath = currentExpression.replace(":", "/")
                    val url =
                        "https://raw.githubusercontent.com/$login/$repoName/${filePath}/${filename}"

                    val isDownloadDirectlyFile =
                        FileUtils.isDownloadDirectlyFile(filename = filename)
                    if (isDownloadDirectlyFile) {
                        navController.navigate(
                            route = Screen.DownloadFileDialog.route
                                .replace("{${Screen.ARG_URL}}", Uri.encode(url))
                        )

                        return@clickable
                    }

                    val isImage = FileUtils.isSupportedImage(filename)
                    val isVideo = FileUtils.isSupportedVideo(filename)

                    if (isImage
                        || isVideo
                    ) {
                        context.startActivity(Intent(context, MediaActivity::class.java).apply {
                            putExtra(MediaActivity.ARG_URL, url)
                            putExtra(MediaActivity.ARG_FILENAME, filename)
                            putExtra(
                                MediaActivity.ARG_MEDIA_TYPE,
                                if (isImage) {
                                    MediaType.Image.name
                                } else {
                                    MediaType.Video.name
                                }
                            )
                        })
                    } else {
                        navController.navigate(
                            route = Screen.File.route
                                .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                .replace("{${Screen.ARG_FILE_PATH}}", filePath)
                                .replace("{${Screen.ARG_FILE_NAME}}", filename)
                                .replace(
                                    "{${Screen.ARG_FILE_EXTENSION}}",
                                    treeEntry.extension.orEmpty()
                                )
                        )
                    }
                }
            }
        }
    ) {
        Text(
            text = treeEntry.name,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@Composable
@Preview(
    name = "ItemTreeEntryPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
private fun ItemTreeEntryPreview(
    @PreviewParameter(
        provider = TreeEntryProvider::class,
        limit = 1
    )
    entry: TreeEntry
) {
    ItemTreeEntry(
        treeEntry = entry,
        enablePlaceholder = false,
        login = "TonnyL",
        repoName = "PaperPlane",
        currentExpression = "master:",
        defaultBranchName = "master"
    )
}
