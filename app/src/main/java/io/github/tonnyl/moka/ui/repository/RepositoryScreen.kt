package io.github.tonnyl.moka.ui.repository

import android.text.format.DateUtils
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Language
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.RepositoryProvider
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.min

private const val MAX_DISPLAY_COUNT_OF_TOPICS = 8

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun RepositoryScreen(
    login: String,
    repoName: String,
    profileType: ProfileType
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val scaffoldState = rememberScaffoldState()

    val viewModel = viewModel<RepositoryViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            login = login,
            repositoryName = repoName,
            profileType = profileType
        )
    )
    val usersRepositoryResource by viewModel.usersRepository.observeAsState()
    val organizationsRepositoryResource by viewModel.organizationsRepository.observeAsState()
    val readmeResource by viewModel.readmeHtml.observeAsState()
    val isFollowing by viewModel.followState.observeAsState()

    val repo = usersRepositoryResource?.data ?: organizationsRepositoryResource?.data

    val starredState by viewModel.starState.observeAsState()
    val followState by viewModel.followState.observeAsState()

    val repositoryPlaceholder = remember {
        RepositoryProvider().values.first()
    }

    Box(
        modifier = Modifier
            .navigationBarsPadding()
    ) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val navController = LocalNavController.current

        val isLoading = (usersRepositoryResource == null && organizationsRepositoryResource == null)
                || usersRepositoryResource?.status == Status.LOADING
                || organizationsRepositoryResource?.status == Status.LOADING

        Scaffold(
            content = {
                when {
                    isLoading
                            || usersRepositoryResource?.data != null
                            || organizationsRepositoryResource?.data != null -> {
                        RepositoryScreenContent(
                            topAppBarSize = topAppBarSize,
                            usersRepository = repositoryPlaceholder.takeIf { isLoading }
                                ?: usersRepositoryResource?.data,
                            organizationsRepository = organizationsRepositoryResource?.data,
                            isFollowing = isFollowing?.data == true,

                            onWatchersClicked = { },
                            onStargazersClicked = { },
                            onForksClicked = { },
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
                                )
                            },
                            onCommitsClicked = {
                                val isOrg = profileType == ProfileType.ORGANIZATION
                                        || organizationsRepositoryResource?.data != null
                                navController.navigate(
                                    route = Screen.Commits.route
                                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                        .replace("{${Screen.ARG_IS_ORG}}", isOrg.toString())
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
                            icon = R.drawable.ic_person_outline_24,
                            title = if (usersRepositoryResource?.status == Status.ERROR) {
                                R.string.user_profile_content_empty_title
                            } else {
                                R.string.common_error_requesting_data
                            },
                            retry = R.string.common_retry,
                            action = R.string.user_profile_content_empty_action
                        )
                    }
                }

                if (followState?.status == Status.ERROR) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        action = viewModel::toggleFollow
                    )
                } else if (starredState?.status == Status.ERROR) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        action = viewModel::toggleStar
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
                        IconButton(onClick = {}) {
                            Icon(
                                contentDescription = stringResource(id = R.string.repository_view_code_image_content_description),
                                painter = painterResource(id = R.drawable.ic_code_24)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                contentDescription = stringResource(id = R.string.repository_watch_image_content_description),
                                painter = painterResource(id = R.drawable.ic_eye_24)
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                contentDescription = stringResource(id = R.string.repository_fork_image_content_description),
                                painter = painterResource(id = R.drawable.ic_code_fork_24)
                            )
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
            },
            isFloatingActionButtonDocked = true,
            floatingActionButtonPosition = FabPosition.End,
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.repository)) },
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

@ExperimentalAnimationApi
@Composable
private fun RepositoryScreenContent(
    topAppBarSize: Int,
    usersRepository: Repository?,
    organizationsRepository: Repository?,
    isFollowing: Boolean,

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

    val login = usersRepository?.owner?.login ?: organizationsRepository?.owner?.login

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
        Row(modifier = Modifier.padding(all = ContentPaddingLargeSize)) {
            Image(
                painter = rememberImagePainter(
                    data = usersRepository?.owner?.avatarUrl
                        ?: organizationsRepository?.owner?.avatarUrl,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            Column(modifier = Modifier.weight(weight = 1f)) {
                val ownerName = usersRepository?.ownerName ?: organizationsRepository?.ownerName
                if (!ownerName.isNullOrEmpty()) {
                    Text(
                        text = ownerName,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
                if (!login.isNullOrEmpty()) {
                    if (enablePlaceholder) {
                        Spacer(modifier = Modifier.height(height = ContentPaddingSmallSize))
                    }
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(
                            text = login,
                            style = MaterialTheme.typography.body2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                    }
                }
            }
            if (usersRepository?.viewerCanFollow == true) {
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedButton(
                    onClick = {

                    },
                    enabled = !enablePlaceholder,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                ) {
                    Text(
                        text = stringResource(
                            id = if (isFollowing) {
                                R.string.user_profile_unfollow
                            } else {
                                R.string.user_profile_follow
                            }
                        )
                    )
                }
            }
        }

        val repoName = usersRepository?.name ?: organizationsRepository?.name
        if (!repoName.isNullOrEmpty()) {
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

        val desc = usersRepository?.description
            ?: organizationsRepository?.description
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
                number = usersRepository?.watchersCount
                    ?: organizationsRepository?.watchersCount
                    ?: 0,
                category = stringResource(id = R.string.repository_watchers),
                onClick = onWatchersClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = usersRepository?.stargazersCount
                    ?: organizationsRepository?.stargazersCount
                    ?: 0,
                category = stringResource(id = R.string.repository_stargazers),
                onClick = onStargazersClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = usersRepository?.forksCount
                    ?: organizationsRepository?.forksCount
                    ?: 0,
                category = stringResource(id = R.string.repository_forks),
                onClick = onForksClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
        }

        Row {
            NumberCategoryText(
                number = usersRepository?.issuesCount
                    ?: organizationsRepository?.issuesCount
                    ?: 0,
                category = stringResource(id = R.string.repository_issues),
                onClick = onIssuesClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            NumberCategoryText(
                number = usersRepository?.pullRequestsCount
                    ?: organizationsRepository?.pullRequestsCount
                    ?: 0,
                category = stringResource(id = R.string.repository_pull_requests),
                onClick = onPrsClicked,
                enablePlaceholder = enablePlaceholder,
                modifier = Modifier.weight(weight = 1f)
            )
            val commitsCount = usersRepository?.commitsCount
                ?: organizationsRepository?.commitsCount
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
        val branch = usersRepository?.defaultBranchRef?.name
            ?: organizationsRepository?.defaultBranchRef?.name ?: "master"
        InfoListItem(
            leadingRes = R.string.repository_branches,
            trailing = branch,
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = {
                    navController.navigate(
                        route = Screen.RepositoryFiles.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login ?: return@clickable)
                            .replace(
                                "{${Screen.ARG_REPOSITORY_NAME}}",
                                repoName ?: return@clickable
                            )
                            .replace("{${Screen.ARG_EXPRESSION}}", "${branch}:")
                    )
                }
            )
        )
        InfoListItem(
            leadingRes = R.string.repository_releases,
            trailing = (usersRepository?.releasesCount
                ?: organizationsRepository?.releasesCount
                ?: 0).toString(),
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = onReleasesClicked,
            )
        )
        InfoListItem(
            leadingRes = R.string.repository_language,
            trailing = usersRepository?.primaryLanguage?.name
                ?: organizationsRepository?.primaryLanguage?.name
                ?: stringResource(id = R.string.programming_language_unknown),
            enablePlaceholder = enablePlaceholder,
            modifier = Modifier.clickable(
                enabled = !enablePlaceholder,
                onClick = {
                    languageProgressBarShowState = !languageProgressBarShowState
                }
            )
        )

        val repository = usersRepository ?: organizationsRepository
        if (!repository?.languages.isNullOrEmpty()
            && !repository?.languageEdges.isNullOrEmpty()
        ) {
            AnimatedVisibility(
                visible = languageProgressBarShowState,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                LanguagesProgressBar(
                    languages = repository?.languages.orEmpty(),
                    languageEdges = repository?.languageEdges.orEmpty(),
                    totalSize = repository?.languagesTotalSize ?: 1,
                    remainingPercentage = repository?.otherLanguagePercentage?.toFloat() ?: 0f
                )
            }
        }
        val licenseInfo = usersRepository?.licenseInfo ?: organizationsRepository?.licenseInfo
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

        val topics = usersRepository?.topics ?: organizationsRepository?.topics
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
                            val isOrg = organizationsRepository != null
                            if (!login.isNullOrEmpty()
                                && !repoName.isNullOrEmpty()
                            ) {
                                navController.navigate(
                                    route = Screen.RepositoryTopics.route
                                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                                        .replace("{${Screen.ARG_IS_ORG}}", isOrg.toString())
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
                    topics[index]?.topic?.let { topic ->
                        Chip(
                            text = topic.name,
                            onClick = {
                                navController.navigate(
                                    route = Screen.Search.route
                                        .replace(
                                            "{${Screen.ARG_INITIAL_SEARCH_KEYWORD}}",
                                            topic.name
                                        )
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

        val createdAt = usersRepository?.createdAt ?: organizationsRepository?.createdAt
        CategoryText(
            textRes = R.string.repository_more_info,
            enablePlaceholder = enablePlaceholder
        )
        if (createdAt != null) {
            InfoListItem(
                leadingRes = R.string.repository_created_at,
                trailing = DateUtils.getRelativeTimeSpanString(createdAt.toEpochMilliseconds())
                    .toString(),
                enablePlaceholder = enablePlaceholder
            )
        }

        val updatedAt = usersRepository?.updatedAt ?: organizationsRepository?.updatedAt
        if (updatedAt != null) {
            InfoListItem(
                leadingRes = R.string.repository_updated_at,
                trailing = DateUtils.getRelativeTimeSpanString(updatedAt.toEpochMilliseconds())
                    .toString(),
                enablePlaceholder = enablePlaceholder
            )
        }

        Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize + 56.dp))
    }
}

@Composable
private fun EmptyReadmeText(enablePlaceholder: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.no_description_provided),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        }
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
            if (languages.size > Repository.MAX_LANGUAGE_DISPLAY_COUNT) {
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

            if (languages.size > Repository.MAX_LANGUAGE_DISPLAY_COUNT) {
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
        usersRepository = repository,
        organizationsRepository = null,
        isFollowing = false,

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

@Preview(showBackground = true, name = "LanguageProgressBarPreview")
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