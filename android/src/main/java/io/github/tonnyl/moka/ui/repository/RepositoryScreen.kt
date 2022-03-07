package io.github.tonnyl.moka.ui.repository

import android.text.format.DateUtils
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.RepositoryType
import io.tonnyl.moka.common.data.UsersType
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.util.RepositoryProvider
import io.tonnyl.moka.graphql.fragment.Language
import io.tonnyl.moka.graphql.fragment.Repository
import io.tonnyl.moka.graphql.type.SubscriptionState
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.min

private const val MAX_DISPLAY_COUNT_OF_TOPICS = 8
private const val MAX_LANGUAGE_DISPLAY_COUNT = 20

@ExperimentalPagingApi
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun RepositoryScreen(
    login: String,
    repoName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val scaffoldState = rememberScaffoldState()

    val viewModel = viewModel(
        initializer = {
            RepositoryViewModel(
                extra = RepositoryViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repositoryName = repoName
                )
            )
        }
    )
    val repositoryResource by viewModel.repository.observeAsState()
    val readmeResource by viewModel.readmeHtml.observeAsState()

    val repo = repositoryResource?.data

    val starredState by viewModel.starState.observeAsState()
    val subscriptionState by viewModel.subscriptionState.observeAsState()

    val forkState by viewModel.forkState.observeAsState()

    val repositoryPlaceholder = remember {
        RepositoryProvider().values.first()
    }

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    var topAppBarSize by remember { mutableStateOf(0) }

    val contentPaddings = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.systemBars,
        applyTop = false,
        additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
    )

    WatchOptionsBottomSheet(
        currentState = subscriptionState?.data,
        state = bottomSheetState,
        bottomPadding = contentPaddings.calculateBottomPadding(),
        updateSubscription = {
            viewModel.updateSubscription(it)
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }
    ) {
        Box(modifier = Modifier.navigationBarsPadding()) {
            val navController = LocalNavController.current

            val isLoading = repositoryResource?.status == Status.LOADING

            Scaffold(
                content = {
                    when {
                        isLoading || repo != null -> {
                            fun navigateToUsersScreen(type: UsersType) {
                                Screen.Users.navigate(
                                    navController = navController,
                                    login = login,
                                    type = type,
                                    repoName = repoName
                                )
                            }

                            RepositoryScreenContent(
                                topAppBarSize = topAppBarSize,
                                repository = repo ?: repositoryPlaceholder,

                                onWatchersClicked = {
                                    navigateToUsersScreen(type = UsersType.REPOSITORY_WATCHERS)
                                },
                                onStargazersClicked = {
                                    navigateToUsersScreen(type = UsersType.REPOSITORY_STARGAZERS)
                                },
                                onForksClicked = {
                                    Screen.Repositories.navigate(
                                        navController = navController,
                                        login = login,
                                        repoName = repoName,
                                        type = RepositoryType.FORKS
                                    )
                                },
                                onPrsClicked = {
                                    navController.navigate(
                                        route = Screen.PullRequests.route
                                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                    )
                                },
                                onIssuesClicked = {
                                    navController.navigate(
                                        route = Screen.Issues.route
                                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                            .replace(
                                                "{${Screen.ARG_REPO_ID}}",
                                                repo?.id ?: return@RepositoryScreenContent
                                            )
                                    )
                                },
                                onCommitsClicked = {
                                    navController.navigate(
                                        route = Screen.Commits.route
                                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                            .replace(
                                                "{${Screen.ARG_SELECTED_BRANCH_NAME}}",
                                                repo?.defaultBranchRef?.ref?.name ?: "master"
                                            )
                                            .replace(
                                                "{${Screen.ARG_REF_PREFIX}}",
                                                repo?.defaultBranchRef?.ref?.prefix ?: "refs/heads/"
                                            )
                                            .replace(
                                                "{${Screen.ARG_DEFAULT_BRANCH_NAME}}",
                                                repo?.defaultBranchRef?.ref?.name ?: "master"
                                            )
                                    )
                                },
                                onReleasesClicked = {
                                    navController.navigate(
                                        route = Screen.Releases.route
                                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                    )
                                },
                                readmeResource = readmeResource,
                                enablePlaceholder = isLoading
                            )
                        }
                        else -> {
                            EmptyScreenContent(
                                titleId = if (repositoryResource?.status == Status.ERROR) {
                                    R.string.common_error_requesting_data
                                } else {
                                    R.string.common_no_data_found
                                },
                                throwable = repositoryResource?.e,
                                action = viewModel::refresh
                            )
                        }
                    }

                    if (starredState?.status == Status.ERROR) {
                        SnackBarErrorMessage(
                            scaffoldState = scaffoldState,
                            action = viewModel::toggleStar,
                            actionId = R.string.common_retry,
                            dismissAction = viewModel::onToggleStarErrorDismissed
                        )
                    } else if (subscriptionState?.status == Status.ERROR) {
                        SnackBarErrorMessage(
                            scaffoldState = scaffoldState,
                            action = null,
                            actionId = null,
                            dismissAction = viewModel::onUpdateSubscriptionErrorDismissed
                        )
                    }
                },
                snackbarHost = {
                    SnackbarHost(hostState = it) { data: SnackbarData ->
                        Snackbar(snackbarData = data)
                    }
                },
                bottomBar = {
                    if (repo != null) {
                        BottomAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            cutoutShape = CircleShape
                        ) {
                            IconButton(onClick = {
                                Screen.RepositoryFiles.navigate(
                                    navController = navController,
                                    login = login,
                                    repoName = repoName,
                                    expression = "${repo.defaultBranchRef?.ref?.name ?: "master"}:",
                                    refPrefix = repo.defaultBranchRef?.ref?.prefix ?: "refs/heads/",
                                    defaultBranchName = repo.defaultBranchRef?.ref?.name ?: "master"
                                )
                            }) {
                                Icon(
                                    contentDescription = stringResource(id = R.string.repository_view_code_image_content_description),
                                    painter = painterResource(id = R.drawable.ic_code_24)
                                )
                            }
                            AnimatedContent(
                                targetState = subscriptionState?.data,
                                contentAlignment = Alignment.Center,
                                transitionSpec = {
                                    scaleIn() with scaleOut()
                                    fadeIn() with fadeOut()
                                }
                            ) {
                                IconButton(onClick = {
                                    if (!bottomSheetState.isVisible) {
                                        coroutineScope.launch {
                                            bottomSheetState.show()
                                        }
                                    }
                                }) {
                                    Icon(
                                        contentDescription = stringResource(
                                            id = R.string.repository_subscription_status,
                                            stringResource(
                                                id = when (subscriptionState?.data) {
                                                    SubscriptionState.IGNORED -> {
                                                        R.string.repository_subscription_ignored
                                                    }
                                                    SubscriptionState.SUBSCRIBED -> {
                                                        R.string.repository_subscription_subscribed
                                                    }
                                                    SubscriptionState.UNSUBSCRIBED -> {
                                                        R.string.repository_subscription_unsubscribed
                                                    }
                                                    else -> {
                                                        R.string.repository_subscription_unknown
                                                    }
                                                }
                                            )
                                        ),
                                        painter = painterResource(
                                            id = if (subscriptionState?.data == SubscriptionState.IGNORED) {
                                                R.drawable.ic_notifications_off
                                            } else {
                                                R.drawable.ic_eye_24
                                            }
                                        )
                                    )
                                }
                            }
                            if (repo.owner.repositoryOwner.login != currentAccount.signedInAccount.account.login) {
                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            route = Screen.ForkRepoDialog.route
                                                .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                                .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                        )
                                    },
                                    enabled = !isLoading
                                ) {
                                    Icon(
                                        contentDescription = stringResource(id = R.string.repository_fork_image_content_description),
                                        painter = painterResource(id = R.drawable.ic_code_fork_24)
                                    )
                                }
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (repo != null) {
                        FloatingActionButton(
                            onClick = { viewModel.toggleStar() },
                            shape = CircleShape
                        ) {
                            AnimatedContent(
                                targetState = starredState?.data,
                                contentAlignment = Alignment.Center,
                                transitionSpec = {
                                    fadeIn() with fadeOut()
                                    scaleIn() with scaleOut()
                                }
                            ) {
                                Icon(
                                    contentDescription = stringResource(
                                        id = if (starredState?.data == true) {
                                            R.string.repository_unstar_image_content_description
                                        } else {
                                            R.string.repository_star_image_content_description
                                        }
                                    ),
                                    painter = painterResource(
                                        id = if (starredState?.data == true) {
                                            R.drawable.ic_star_24
                                        } else {
                                            R.drawable.ic_star_border_24
                                        }
                                    ),
                                    tint = MaterialTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                },
                isFloatingActionButtonDocked = true,
                floatingActionButtonPosition = FabPosition.End,
                scaffoldState = scaffoldState
            )

            InsetAwareTopAppBar(
                title = { Text(text = stringResource(id = R.string.repository)) },
                navigationIcon = {
                    AppBarNavigationIcon()
                },
                actions = {
                    ShareAndOpenInBrowserMenu(
                        showMenuState = remember { mutableStateOf(false) },
                        text = "https://github.com/${login}/${repoName}"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { topAppBarSize = it.height }
            )
        }

        if (forkState?.status == Status.ERROR
            && forkState?.data == true
        ) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = viewModel::fork,
                actionId = R.string.common_retry,
                messageId = R.string.repository_fork_failed,
                dismissAction = viewModel::clearForkState
            )
        } else if (forkState?.status == Status.SUCCESS
            && forkState?.data == true
        ) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                messageId = R.string.repository_fork_succeeded,
                actionId = null,
                duration = SnackbarDuration.Long,
                dismissAction = viewModel::clearForkState
            )
        }
    }

    if (bottomSheetState.isVisible) {
        BackHandler {
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        }
    }

}

@ExperimentalAnimationApi
@Composable
private fun RepositoryScreenContent(
    topAppBarSize: Int,
    repository: Repository,

    onWatchersClicked: () -> Unit,
    onStargazersClicked: () -> Unit,
    onForksClicked: () -> Unit,
    onIssuesClicked: () -> Unit,
    onPrsClicked: () -> Unit,
    onCommitsClicked: () -> Unit,
    onReleasesClicked: () -> Unit,

    readmeResource: Resource<String>?,

    enablePlaceholder: Boolean
) {
    var languageProgressBarShowState by remember { mutableStateOf(false) }
    val navController = LocalNavController.current

    val login = repository.owner.repositoryOwner.login

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(
                paddingValues = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = false,
                    additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
                )
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
        ) {
            val navigateToProfile = {
                Screen.Profile.navigate(
                    navController = navController,
                    login = login,
                    type = ProfileType.NOT_SPECIFIED
                )
            }

            AvatarImage(
                url = repository.owner.repositoryOwner.avatarUrl,
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
                    .clickable(enabled = !enablePlaceholder) {
                        navigateToProfile.invoke()
                    }
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            if (login.isNotEmpty()) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = login,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clickable(enabled = !enablePlaceholder) {
                                navigateToProfile.invoke()
                            }
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                }
            }
        }

        val repoName = repository.name
        if (repoName.isNotEmpty()) {
            Text(
                text = repoName,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(horizontal = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }

        val desc = repository.description
            ?: stringResource(id = R.string.no_description_provided)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = desc,
                maxLines = if (enablePlaceholder) {
                    1
                } else {
                    Int.MAX_VALUE
                },
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(all = ContentPaddingLargeSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        }

        Row {
            NumberCategoryText(
                number = repository.watchers.totalCount,
                category = stringResource(id = R.string.repository_watchers),
                onClick = onWatchersClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = repository.stargazers.totalCount,
                category = stringResource(id = R.string.repository_stargazers),
                onClick = onStargazersClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = repository.forkCount,
                category = stringResource(id = R.string.repository_forks),
                onClick = onForksClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
        }

        Row {
            NumberCategoryText(
                number = repository.issues.totalCount,
                category = stringResource(id = R.string.repository_issues),
                onClick = onIssuesClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = repository.pullRequests.totalCount,
                category = stringResource(id = R.string.repository_pull_requests),
                onClick = onPrsClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            val commitsCount =
                repository.defaultBranchRef?.ref?.target?.gitObject?.onCommit?.history?.totalCount
                    ?: 0
            NumberCategoryText(
                number = commitsCount,
                category = LocalContext.current.resources.getQuantityString(
                    R.plurals.commit_count_plurals,
                    commitsCount
                ),
                onClick = onCommitsClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
        }

        CategoryText(
            textRes = R.string.repository_basic_info,
            enablePlaceholder = enablePlaceholder
        )
        val defaultBranchRef = repository.defaultBranchRef
        InfoListItem(
            leadingRes = R.string.repository_branches,
            trailing = defaultBranchRef?.ref?.name ?: "master",
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = {
                    Screen.Branches.navigate(
                        navController = navController,
                        login = login,
                        repoName = repoName,
                        refPrefix = defaultBranchRef?.ref?.prefix ?: "refs/heads/",
                        defaultBranchName = defaultBranchRef?.ref?.name ?: return@clickable,
                        selectedBranchName = defaultBranchRef.ref.name
                    )
                }
            )
        )
        InfoListItem(
            leadingRes = R.string.repository_releases,
            trailing = repository.releases.totalCount.toString(),
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = onReleasesClicked,
            )
        )
        InfoListItem(
            leadingRes = R.string.repository_language,
            trailing = repository.primaryLanguage?.language?.name
                ?: stringResource(id = R.string.programming_language_unknown),
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = {
                    languageProgressBarShowState = !languageProgressBarShowState
                }
            )
        )

        if (!repository.languages?.nodes.isNullOrEmpty()
            && !repository.languages?.edges.isNullOrEmpty()
        ) {
            AnimatedVisibility(
                visible = languageProgressBarShowState,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LanguagesProgressBar(
                    languages = repository.languages?.nodes?.mapNotNull { it?.language }.orEmpty(),
                    languageEdges = repository.languages?.edges?.mapNotNull {
                        it?.languageEdge?.size
                    }.orEmpty(),
                    totalSize = repository.languages?.totalSize ?: 1,
                    remainingPercentage = if (repository.languages?.nodes.orEmpty().size > 20) {
                        (repository.languages?.edges?.sumOf {
                            (it?.languageEdge?.size ?: 0).toDouble()
                        } ?: 0).toDouble() / (repository.languages?.totalSize ?: 1).toDouble()
                    } else {
                        null
                    }?.toFloat() ?: 0f
                )
            }
        }
        val licenseInfo = repository.licenseInfo?.license
        InfoListItem(
            leadingRes = R.string.repository_license,
            trailing = licenseInfo?.spdxId.takeIf {
                !it.isNullOrEmpty()
            } ?: licenseInfo?.name ?: "",
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = {}
            )
        )

        val topics = repository.repositoryTopics.nodes?.mapNotNull { it?.repositoryTopic?.topic }
        if (!topics.isNullOrEmpty()) {
            Row {
                CategoryText(
                    textRes = R.string.repository_topics,
                    enablePlaceholder = enablePlaceholder
                )
                Spacer(modifier = Modifier.weight(weight = 1f))
                if (topics.orEmpty().size > MAX_DISPLAY_COUNT_OF_TOPICS) {
                    TextButton(
                        onClick = {
                            if (login.isNotEmpty()
                                && repoName.isNotEmpty()
                            ) {
                                navController.navigate(
                                    route = Screen.RepositoryTopics.route
                                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                )
                            }
                        },
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade(),
                                color = MaterialTheme.colors.primary
                            )
                    ) {
                        Text(text = stringResource(id = R.string.see_all))
                    }
                }
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            }
            LazyRow {
                item {
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                }
                items(count = min(topics.size, MAX_DISPLAY_COUNT_OF_TOPICS)) { index ->
                    topics[index].topic.let { topic ->
                        Chip(
                            text = topic.name,
                            onClick = {
                                Screen.Search.navigate(
                                    navController = navController,
                                    keyword = topic.name
                                )
                            },
                            enablePlaceholder = enablePlaceholder
                        )
                        Spacer(modifier = Modifier.padding(horizontal = ContentPaddingSmallSize))
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                }
            }
        }

        CategoryText(
            textRes = R.string.repository_readme,
            enablePlaceholder = enablePlaceholder
        )
        when (readmeResource?.status) {
            Status.SUCCESS -> {
                val readme = readmeResource.data
                if (readme.isNullOrEmpty()) {
                    EmptyReadmeText(enablePlaceholder = enablePlaceholder)
                } else {
                    var webView by remember { mutableStateOf<ThemedWebView?>(null) }
                    DisposableEffect(key1 = webView) {
                        webView?.loadData(readme)
                        onDispose {
                            webView?.stopLoading()
                        }
                    }
                    AndroidView(
                        factory = { ThemedWebView(it) },
                        modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)
                    ) {
                        webView = it
                    }
                }
            }
            Status.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LottieLoadingComponent(modifier = Modifier.size(size = LottieLoadingAnimationSize))
                }
            }
            // Status.ERROR, null
            else -> {
                EmptyReadmeText(enablePlaceholder = enablePlaceholder)
            }
        }

        CategoryText(
            textRes = R.string.repository_more_info,
            enablePlaceholder = enablePlaceholder
        )
        InfoListItem(
            leadingRes = R.string.repository_created_at,
            trailing = DateUtils.getRelativeTimeSpanString(repository.createdAt.toEpochMilliseconds())
                .toString(),
            enablePlaceholder = enablePlaceholder
        )

        InfoListItem(
            leadingRes = R.string.repository_updated_at,
            trailing = DateUtils.getRelativeTimeSpanString(repository.updatedAt.toEpochMilliseconds())
                .toString(),
            enablePlaceholder = enablePlaceholder
        )

        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize + 56.dp))
    }
}

@Composable
private fun LanguagesProgressBar(
    languages: List<Language>,
    languageEdges: List<Int>,
    totalSize: Int,
    remainingPercentage: Float
) {
    Column(modifier = Modifier.padding(all = ContentPaddingLargeSize)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height = ContentPaddingMediumSize)
                .clip(shape = RoundedCornerShape(percent = 50))
        ) {
            for (i in languageEdges.indices) {
                Box(
                    modifier = Modifier
                        .weight(weight = languageEdges[i] / totalSize.toFloat())
                        .fillMaxHeight()
                        .background(
                            color = languages[i].color
                                ?.toColor()
                                ?.let { Color(it) }
                                ?: MaterialTheme.colors.onBackground
                        )
                )
            }
            if (languages.size > MAX_LANGUAGE_DISPLAY_COUNT) {
                Box(
                    modifier = Modifier
                        .weight(weight = remainingPercentage)
                        .fillMaxHeight()
                        .background(color = MaterialTheme.colors.onBackground)
                )
            }
        }
        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        FlowRow {
            languages.forEachIndexed { index, lang ->
                LanguageLabel(
                    color = lang.color
                        ?.toColor()
                        ?.let { Color(it) }
                        ?: MaterialTheme.colors.onBackground,
                    name = lang.name,
                    percent = (languageEdges[index] / totalSize.toFloat()) * 100
                )
            }

            if (languages.size > MAX_LANGUAGE_DISPLAY_COUNT) {
                LanguageLabel(
                    color = MaterialTheme.colors.onBackground,
                    name = stringResource(id = R.string.repository_language_other),
                    percent = remainingPercentage * 100
                )
            }
        }
    }
}

@Composable
private fun LanguageLabel(
    color: Color,
    name: String,
    percent: Float
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(size = RepositoryCardLanguageDotSize)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        Text(
            text = name,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = if (percent < 1) {
                    stringResource(id = R.string.repository_language_percentage_less_than_0_1)
                } else {
                    "%.1f%%".format(percent)
                },
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        }
        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
    }
}

@ExperimentalMaterialApi
@Composable
private fun WatchOptionsBottomSheet(
    state: ModalBottomSheetState,
    currentState: SubscriptionState?,
    bottomPadding: Dp,
    updateSubscription: (SubscriptionState) -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = MaterialTheme.shapes.small,
        sheetContent = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text(
                        text = stringResource(id = R.string.repository_subscription),
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(
                            start = ContentPaddingLargeSize + ContentPaddingSmallSize,
                            top = ContentPaddingLargeSize,
                            bottom = ContentPaddingLargeSize,
                            end = ContentPaddingLargeSize
                        )
                    )
                }

                item {
                    WatchOptionItem(
                        state = SubscriptionState.SUBSCRIBED,
                        selectedState = currentState,
                        updateSubscription = updateSubscription
                    )
                }

                item {
                    WatchOptionItem(
                        state = SubscriptionState.UNSUBSCRIBED,
                        selectedState = currentState,
                        updateSubscription = updateSubscription
                    )
                }

                item {
                    WatchOptionItem(
                        state = SubscriptionState.IGNORED,
                        selectedState = currentState,
                        updateSubscription = updateSubscription
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(height = bottomPadding))
                }
            }
        },
        content = content
    )
}

@ExperimentalMaterialApi
@Composable
private fun WatchOptionItem(
    state: SubscriptionState,
    selectedState: SubscriptionState?,
    updateSubscription: (SubscriptionState) -> Unit
) {
    ListItem(
        icon = {
            if (selectedState == state) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = stringResource(id = R.string.repository_subscription_current_selection),
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .size(size = IconSize)
                        .padding(all = ContentPaddingSmallSize)
                )
            }
        },
        secondaryText = {
            Text(
                text = stringResource(
                    id = when (state) {
                        SubscriptionState.IGNORED -> {
                            R.string.repository_subscription_ignore_desc
                        }
                        SubscriptionState.UNSUBSCRIBED -> {
                            R.string.repository_subscription_unsubscribe_desc
                        }
                        else -> {
                            R.string.repository_subscription_subscribe_desc
                        }
                    }
                ),
                style = MaterialTheme.typography.body2
            )
        },
        modifier = Modifier.clickable(enabled = selectedState != null) {
            updateSubscription.invoke(state)
        }
    ) {
        Text(
            text = stringResource(
                id = when (state) {
                    SubscriptionState.IGNORED -> {
                        R.string.repository_subscription_ignore
                    }
                    SubscriptionState.UNSUBSCRIBED -> {
                        R.string.repository_subscription_unsubscribe
                    }
                    else -> {
                        R.string.repository_subscription_subscribe
                    }
                }
            ),
            style = MaterialTheme.typography.body1
        )
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true, name = "RepositoryScreenContentPreview")
@Composable
private fun RepositoryScreenContentPreview(
    @PreviewParameter(
        provider = RepositoryProvider::class,
        limit = 1
    )
    repository: Repository
) {
    RepositoryScreenContent(
        topAppBarSize = 0,

        repository = repository,

        onWatchersClicked = {},
        onStargazersClicked = {},
        onForksClicked = {},
        onIssuesClicked = {},
        onPrsClicked = {},
        onCommitsClicked = {},
        onReleasesClicked = {},

        readmeResource = Resource.success("<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>"),

        enablePlaceholder = false
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF,
    name = "LanguageProgressBarPreview"
)
@Composable
private fun LanguageProgressBarPreview() {
    LanguagesProgressBar(
        languages = listOf(
            Language(
                color = "#F18E33",
                id = "MDg6TGFuZ3VhZ2UyNzI=",
                name = "Kotlin"
            )
        ),
        languageEdges = listOf(25),
        totalSize = 50,
        remainingPercentage = .5f
    )
}

@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFF,
    name = "WatchOptionItemPreview"
)
@Composable
private fun WatchOptionItemPreview() {
    WatchOptionItem(
        state = SubscriptionState.SUBSCRIBED,
        selectedState = SubscriptionState.SUBSCRIBED
    ) {

    }
}