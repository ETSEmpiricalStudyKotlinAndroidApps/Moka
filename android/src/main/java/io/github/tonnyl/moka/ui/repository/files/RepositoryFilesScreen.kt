package io.github.tonnyl.moka.ui.repository.files

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.media.MediaActivity
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.util.FileUtils
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.data.TreeEntryType
import io.tonnyl.moka.common.data.treeEntryType
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.TreeEntryProvider
import io.tonnyl.moka.graphql.fragment.TreeEntry
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun RepositoryFilesScreen(
    login: String,
    repoName: String,
    expression: String,
    refPrefix: String,
    defaultBranchName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<RepositoryFilesViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[RepositoryFilesViewModel.REPOSITORY_FILES_VIEW_MODEL_EXTRA_KEY] =
                RepositoryFilesViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repositoryName = repoName,
                    expression = expression
                )
        },
        key = expression
    )

    val entries = viewModel.entry.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = entries.value?.status == Status.LOADING),
            onRefresh = viewModel::refresh,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            }
        ) {
            when {
                entries.value?.status == Status.ERROR
                        && entries.value?.data.isNullOrEmpty() -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    RepositoryFilesScreenContent(
                        contentTopPadding = contentPadding.calculateTopPadding(),
                        contentBottomPadding = contentPadding.calculateBottomPadding(),
                        entries = entries.value?.data.orEmpty(),
                        enablePlaceholder = entries.value?.status == Status.LOADING && entries.value?.data.isNullOrEmpty(),
                        login = login,
                        repoName = repoName,
                        expression = expression,
                        defaultBranchName = defaultBranchName
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
                if (expression.endsWith(":")) {
                    val branch = expression.substring(0, expression.length - 1)
                    TextButton(
                        onClick = {
                            navController.navigate(
                                route = Screen.Branches.route
                                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                    .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                    .replace("{${Screen.ARG_REF_PREFIX}}", refPrefix)
                                    .replace(
                                        "{${Screen.ARG_DEFAULT_BRANCH_NAME}}",
                                        defaultBranchName
                                    )
                                    .replace("{${Screen.ARG_SELECTED_BRANCH_NAME}}", branch)
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalCoilApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
private fun RepositoryFilesScreenContent(
    contentTopPadding: Dp,
    contentBottomPadding: Dp,
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
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = contentTopPadding))
        }

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

        item {
            Spacer(modifier = Modifier.height(height = contentBottomPadding))
        }
    }
}

@ExperimentalSerializationApi
@ExperimentalCoilApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
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
                    navController.navigate(
                        route = Screen.RepositoryFiles.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                            .replace(
                                "{${Screen.ARG_EXPRESSION}}",
                                if (currentExpression.endsWith(":")) {
                                    "${currentExpression}${treeEntry.name}"
                                } else {
                                    "${currentExpression}/${treeEntry.name}"
                                }
                            )
                            .replace("{${Screen.ARG_REF_PREFIX}}", currentBranch)
                            .replace("{${Screen.ARG_DEFAULT_BRANCH_NAME}}", defaultBranchName)
                    )
                }
                TreeEntryType.BLOB -> {
                    val isImage = FileUtils.isImage(treeEntry.name)

                    val filePath = currentExpression.replace(":", "/")
                    val filename = treeEntry.name

                    if (isImage) {
                        val url =
                            "https://raw.githubusercontent.com/$login/$repoName/${filePath}/${filename}"
                        context.startActivity(Intent(context, MediaActivity::class.java).apply {
                            putExtra(MediaActivity.ARG_URL, url)
                            putExtra(MediaActivity.ARG_FILENAME, filename)
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

@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@ExperimentalMaterialApi
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
