package io.github.tonnyl.moka.ui

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.ui.about.AboutScreen
import io.github.tonnyl.moka.ui.about.URL_OF_FAQ
import io.github.tonnyl.moka.ui.account.AccountDialogScreen
import io.github.tonnyl.moka.ui.account.manage.LogoutConfirmDialog
import io.github.tonnyl.moka.ui.account.manage.ManageAccountsScreen
import io.github.tonnyl.moka.ui.branches.BranchesScreen
import io.github.tonnyl.moka.ui.commit.CommitScreen
import io.github.tonnyl.moka.ui.commits.CommitsScreen
import io.github.tonnyl.moka.ui.emojis.EmojisScreen
import io.github.tonnyl.moka.ui.emojis.search.SearchEmojiScreen
import io.github.tonnyl.moka.ui.exception.ExceptionDetailsScreen
import io.github.tonnyl.moka.ui.explore.ExploreScreen
import io.github.tonnyl.moka.ui.explore.filters.ExploreFiltersScreen
import io.github.tonnyl.moka.ui.feedback.FeedbackConfirmDialog
import io.github.tonnyl.moka.ui.file.FileScreen
import io.github.tonnyl.moka.ui.file.download.DownloadFileDialog
import io.github.tonnyl.moka.ui.inbox.InboxScreen
import io.github.tonnyl.moka.ui.issue.IssueScreen
import io.github.tonnyl.moka.ui.issues.IssuesScreen
import io.github.tonnyl.moka.ui.issues.create.CreateIssueScreen
import io.github.tonnyl.moka.ui.pr.PullRequestScreen
import io.github.tonnyl.moka.ui.pr.thread.CommentTreadScreen
import io.github.tonnyl.moka.ui.profile.ProfileScreen
import io.github.tonnyl.moka.ui.profile.ProfileViewModel
import io.github.tonnyl.moka.ui.profile.ProfileViewModelExtra
import io.github.tonnyl.moka.ui.profile.edit.EditProfileScreen
import io.github.tonnyl.moka.ui.profile.status.EditStatusScreen
import io.github.tonnyl.moka.ui.profile.status.EditStatusViewModel
import io.github.tonnyl.moka.ui.profile.status.EditStatusViewModelExtra
import io.github.tonnyl.moka.ui.prs.PullRequestsScreen
import io.github.tonnyl.moka.ui.release.ReleaseScreen
import io.github.tonnyl.moka.ui.release.assets.ReleaseAssetsScreen
import io.github.tonnyl.moka.ui.releases.ReleasesScreen
import io.github.tonnyl.moka.ui.repositories.RepositoriesScreen
import io.github.tonnyl.moka.ui.repository.ForkRepoDialog
import io.github.tonnyl.moka.ui.repository.RepositoryScreen
import io.github.tonnyl.moka.ui.repository.files.RepositoryFilesScreen
import io.github.tonnyl.moka.ui.search.SearchScreen
import io.github.tonnyl.moka.ui.settings.SettingScreen
import io.github.tonnyl.moka.ui.status.GitHubStatusScreen
import io.github.tonnyl.moka.ui.status.incident.IncidentScreen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.timeline.TimelineScreen
import io.github.tonnyl.moka.ui.topics.RepositoryTopicsScreen
import io.github.tonnyl.moka.ui.users.UsersScreen
import io.github.tonnyl.moka.util.openInBrowser
import io.tonnyl.moka.common.data.FiltersType
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.data.RepositoryType
import io.tonnyl.moka.common.data.UsersType
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {

    object Timeline : Screen("timeline")

    object Inbox : Screen("inbox")

    object Explore : Screen("explore")

    object Settings : Screen("settings")

    object About : Screen("about")

    object Profile : Screen("profile/{${ARG_PROFILE_LOGIN}}/{${ARG_PROFILE_TYPE}}") {

        fun navigate(
            navController: NavController,
            login: String,
            type: ProfileType
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_PROFILE_TYPE}", type.name)
            )
        }

    }

    object EditProfile :
        Screen("edit_profile?${ARG_EDIT_PROFILE_NAME}={${ARG_EDIT_PROFILE_NAME}}?${ARG_EDIT_PROFILE_BIO}={${ARG_EDIT_PROFILE_BIO}}?${ARG_EDIT_PROFILE_URL}={${ARG_EDIT_PROFILE_URL}}?${ARG_EDIT_PROFILE_COMPANY}={${ARG_EDIT_PROFILE_COMPANY}}?${ARG_EDIT_PROFILE_LOCATION}={${ARG_EDIT_PROFILE_LOCATION}}?${ARG_EDIT_PROFILE_TWITTER}={${ARG_EDIT_PROFILE_TWITTER}}")

    object Repository :
        Screen("repository/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
            )
        }

    }

    object RepositoryFiles :
        Screen("repository_files?${ARG_PROFILE_LOGIN}={${ARG_PROFILE_LOGIN}}&${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}&${ARG_EXPRESSION}={${ARG_EXPRESSION}}&$ARG_REF_PREFIX={${ARG_REF_PREFIX}}&$ARG_DEFAULT_BRANCH_NAME={${ARG_DEFAULT_BRANCH_NAME}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            expression: String,
            refPrefix: String,
            defaultBranchName: String
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_EXPRESSION}", expression)
                    .replace("{$ARG_REF_PREFIX}", refPrefix)
                    .replace("{$ARG_DEFAULT_BRANCH_NAME}", defaultBranchName)
            )
        }

    }

    object File :
        Screen("file?${ARG_PROFILE_LOGIN}={${ARG_PROFILE_LOGIN}}&${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}&${ARG_FILE_PATH}={${ARG_FILE_PATH}}&${ARG_FILE_NAME}={${ARG_FILE_NAME}}&${ARG_FILE_EXTENSION}={${ARG_FILE_EXTENSION}}")

    object Users :
        Screen("users/{${ARG_PROFILE_LOGIN}}/{${ARG_USERS_TYPE}}?${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}") {

        fun navigate(
            navController: NavController,
            login: String,
            type: UsersType,
            repoName: String? = null
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_USERS_TYPE}", type.name)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName.orEmpty())
            )
        }

    }

    object Repositories :
        Screen("repositories/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_TYPE}}?${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            type: RepositoryType
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_TYPE}", type.name)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
            )
        }

    }

    object Issues :
        Screen("issues/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_REPO_ID}}") {

        fun route(
            login: String,
            repoName: String,
            repoId: String
        ): String {
            return route.replace("{$ARG_PROFILE_LOGIN}", login)
                .replace("{$ARG_REPOSITORY_NAME}", repoName)
                .replace("{$ARG_REPO_ID}", repoId)
        }

    }

    object PullRequests : Screen("pull_requests/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object Issue :
        Screen("issue/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_ISSUE_PR_NUMBER}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            number: Int,
            builder: (NavOptionsBuilder.() -> Unit) = { }
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_ISSUE_PR_NUMBER}", number.toString()),
                builder = builder
            )
        }

    }

    object PullRequest :
        Screen("pull_request/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_ISSUE_PR_NUMBER}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            number: Int
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_ISSUE_PR_NUMBER}", number.toString())
            )
        }

    }

    object Emojis : Screen("emojis") {

        const val RESULT_EMOJI = "result_emoji"

    }

    object SearchEmoji : Screen("search_emoji")

    object EditStatus :
        Screen("edit_status?${ARG_EDIT_STATUS_EMOJI}={${ARG_EDIT_STATUS_EMOJI}}?${ARG_EDIT_STATUS_MESSAGE}={${ARG_EDIT_STATUS_MESSAGE}}?${ARG_EDIT_STATUS_LIMIT_AVAILABILITY}={${ARG_EDIT_STATUS_LIMIT_AVAILABILITY}}") {

        const val RESULT_UPDATE_STATUS = "result_update_status"

    }

    object Search : Screen("search?${ARG_INITIAL_SEARCH_KEYWORD}={${ARG_INITIAL_SEARCH_KEYWORD}}") {

        fun navigate(
            navController: NavController,
            keyword: String = ""
        ) {
            navController.navigate(route = route.replace("{$ARG_INITIAL_SEARCH_KEYWORD}", keyword))
        }

    }

    object RepositoryTopics :
        Screen("repository_topics/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object Commits :
        Screen("commits?${ARG_PROFILE_LOGIN}={${ARG_PROFILE_LOGIN}}&${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}&${ARG_DEFAULT_BRANCH_NAME}={${ARG_DEFAULT_BRANCH_NAME}}&${ARG_SELECTED_BRANCH_NAME}={${ARG_SELECTED_BRANCH_NAME}}&${ARG_REF_PREFIX}={${ARG_REF_PREFIX}}")

    object Releases : Screen("releases/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object Commit : Screen("commit/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_REF}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            ref: String
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_REF}", ref)
            )
        }

    }

    object Branches :
        Screen("branches?${ARG_PROFILE_LOGIN}={${ARG_PROFILE_LOGIN}}&${ARG_REPOSITORY_NAME}={${ARG_REPOSITORY_NAME}}&${ARG_DEFAULT_BRANCH_NAME}={${ARG_DEFAULT_BRANCH_NAME}}&${ARG_SELECTED_BRANCH_NAME}={${ARG_SELECTED_BRANCH_NAME}}&${ARG_REF_PREFIX}={${ARG_REF_PREFIX}}") {

        const val RESULT_BRANCH_NAME = "result_branch_name"

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            refPrefix: String,
            defaultBranchName: String,
            selectedBranchName: String
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_REF_PREFIX}", refPrefix)
                    .replace("{$ARG_DEFAULT_BRANCH_NAME}", defaultBranchName)
                    .replace("{$ARG_SELECTED_BRANCH_NAME}", selectedBranchName)
            )
        }

    }

    object Release :
        Screen("release/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_TAG_NAME}}") {

        fun navigate(
            navController: NavController,
            login: String,
            repoName: String,
            tagName: String
        ) {
            navController.navigate(
                route = route
                    .replace("{$ARG_PROFILE_LOGIN}", login)
                    .replace("{$ARG_REPOSITORY_NAME}", repoName)
                    .replace("{$ARG_TAG_NAME}", tagName)
            )
        }

    }

    object ReleaseAssets :
        Screen("release_assets/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_TAG_NAME}}")

    object CommentThread : Screen(route = "comment_thread/{${ARG_NODE_ID}}")

    object FAQ : Screen("faq")

    object Feedback : Screen("feedback")

    object AccountDialog : Screen("account_dialog")

    object ForkRepoDialog :
        Screen("fork_repo_dialog/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object DownloadFileDialog : Screen("download_file_dialog/{${ARG_URL}}")

    object ExploreFilters : Screen("explore_filters/{${ARG_EXPLORE_FILTERS_TYPE}}") {

        fun navigate(
            navController: NavController,
            type: FiltersType
        ) {
            navController.navigate(route = route.replace("{$ARG_EXPLORE_FILTERS_TYPE}", type.name))
        }

    }

    object CreateIssue :
        Screen("create_issue/{${ARG_REPO_ID}}?${ARG_DEFAULT_COMMENT}={${ARG_DEFAULT_COMMENT}}") {

        fun navigate(
            navController: NavController,
            repoId: String,
            defaultComment: String? = null,
            builder: (NavOptionsBuilder.() -> Unit) = { }
        ) {
            navController.navigate(
                route = route.replace("{$ARG_REPO_ID}", repoId)
                    .replace("{${ARG_DEFAULT_COMMENT}}", Uri.encode(defaultComment.orEmpty())),
                builder = builder
            )
        }

    }

    object ManageAccounts : Screen(route = "manage_accounts")

    object LogOutConfirmDialog : Screen(route = "log_out_confirm/{${ARG_ACCOUNT_ID}}")

    object ExceptionDetails : Screen(route = "exception_details/{${ARG_EXCEPTION_DETAILS}}")

    object FeedbackConfirmDialog : Screen(route = "feedback_confirm")

    object GitHubStatus : Screen(route = "github_status")

    object GitHubIncident : Screen(route = "github_incident/{${ARG_INCIDENT_ID}}")

    companion object {

        const val ARG_PROFILE_LOGIN = "arg_profile_login"

        const val ARG_REPOSITORY_NAME = "arg_repository_name"

        /**
         * @see [RepositoryType.name]
         */
        const val ARG_REPOSITORY_TYPE = "arg_repository_type"

        /**
         * @see [UsersType]
         */
        const val ARG_USERS_TYPE = "arg_users_type"

        /**
         * @see [ProfileType]
         */
        const val ARG_PROFILE_TYPE = "arg_profile_type"

        /**
         * Keys for [EditProfile] arguments.
         */
        const val ARG_EDIT_PROFILE_NAME = "arg_edit_profile_name"
        const val ARG_EDIT_PROFILE_BIO = "arg_edit_profile_bio"
        const val ARG_EDIT_PROFILE_URL = "arg_edit_profile_url"
        const val ARG_EDIT_PROFILE_COMPANY = "arg_edit_profile_company"
        const val ARG_EDIT_PROFILE_LOCATION = "arg_edit_profile_location"
        const val ARG_EDIT_PROFILE_TWITTER = "arg_edit_profile_twitter"

        /**
         * Keys for [EditStatus] arguments.
         */
        const val ARG_EDIT_STATUS_EMOJI = "arg_edit_status_emoji"
        const val ARG_EDIT_STATUS_MESSAGE = "arg_edit_status_message"
        const val ARG_EDIT_STATUS_LIMIT_AVAILABILITY = "arg_edit_status_limit_availability"

        const val ARG_ISSUE_PR_NUMBER = "arg_issue_pr_number"

        const val ARG_INITIAL_SEARCH_KEYWORD = "arg_initial_search_keyword"

        const val ARG_REF = "arg_ref"

        const val ARG_EXPRESSION = "arg_expression"

        const val ARG_DEFAULT_BRANCH_NAME = "arg_default_branch_name"
        const val ARG_SELECTED_BRANCH_NAME = "arg_selected_branch_name"
        const val ARG_REF_PREFIX = "arg_ref_prefix"

        const val ARG_TAG_NAME = "arg_tag_name"

        const val ARG_NODE_ID = "arg_node_id"

        const val ARG_FILE_PATH = "arg_file_path"
        const val ARG_FILE_EXTENSION = "arg_file_extension"
        const val ARG_FILE_NAME = "arg_file_name"

        // should be encoded when passing.
        const val ARG_URL = "arg_url"

        const val ARG_EXPLORE_FILTERS_TYPE = "arg_explore_filters_type"

        const val ARG_REPO_ID = "arg_repo_id"

        const val ARG_ACCOUNT_ID = "arg_account_id"

        const val ARG_EXCEPTION_DETAILS = "arg_exception_details"

        const val ARG_DEFAULT_COMMENT = "arg_default_comment"

        const val ARG_INCIDENT_ID = "arg_incident_id"
    }

}

@Composable
fun MainScreen(startDestination: Screen) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val currentRoute = remember { mutableStateOf(startDestination.route) }

    val navController = LocalNavController.current

    val context = LocalContext.current

    val navigate: (String) -> Unit = { route ->
        when (route) {
            Screen.FAQ.route -> {
                context.openInBrowser(URL_OF_FAQ)
            }
            Screen.Feedback.route -> {
                navController.navigate(route = Screen.FeedbackConfirmDialog.route)
            }
            else -> {
                navController.navigateUp()
                navController.navigate(route = route) {
                    popUpTo(route = route)
                    launchSingleTop = true
                }
            }
        }

        coroutineScope.launch {
            drawerState.close()
        }
    }

    val configuration = LocalConfiguration.current
    if (configuration.smallestScreenWidthDp < 600) {
        ModalDrawer(
            drawerState = drawerState,
            drawerContent = {
                MainDrawerContent(
                    currentRoute = currentRoute.value,
                    navigate = navigate,
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding(bottom = false)
                )
            }
        ) {
            MainNavHost(
                currentRoute = currentRoute,
                startDestination = startDestination
            ) {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
        }
    } else {
        Row {
            MainNavigationRail(
                currentRoute = currentRoute.value,
                navigate = navigate,
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false)
            )
            MainNavHost(
                currentRoute = currentRoute,
                startDestination = startDestination,
                openDrawer = null
            )
        }
    }
}

@Composable
fun MainDrawerContent(
    currentRoute: String,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        item {
            MainDrawerHeader()
            Divider(modifier = Modifier.fillMaxWidth())
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_timeline_24,
                textRes = R.string.navigation_menu_timeline,
                selected = currentRoute == Screen.Timeline.route,
                onClick = {
                    navigate.invoke(Screen.Timeline.route)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_explore_24,
                textRes = R.string.navigation_menu_explore,
                selected = currentRoute == Screen.Explore.route,
                onClick = {
                    navigate.invoke(Screen.Explore.route)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_inbox_24,
                textRes = R.string.navigation_menu_inbox,
                selected = currentRoute == Screen.Inbox.route,
                onClick = {
                    navigate.invoke(Screen.Inbox.route)
                }
            )
            Divider(modifier = Modifier.fillMaxWidth())
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_api,
                textRes = R.string.navigation_menu_github_status,
                selected = currentRoute == Screen.GitHubStatus.route,
                onClick = {
                    navigate.invoke(Screen.GitHubStatus.route)
                }
            )
            Divider(modifier = Modifier.fillMaxWidth())
        }
        item {
            MainDrawerMenuItem(
                vector = Icons.Outlined.Settings,
                textRes = R.string.navigation_menu_settings,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.Settings.route)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vector = Icons.Outlined.Info,
                textRes = R.string.navigation_menu_about,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.About.route)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_help_24,
                textRes = R.string.navigation_menu_faq_help,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.FAQ.route)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_feedback_24,
                textRes = R.string.navigation_menu_feedback,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.Feedback.route)
                }
            )
        }
    }
}

@Composable
private fun MainDrawerHeader() {
    Text(
        text = stringResource(id = R.string.app_name),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.h6,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ContentPaddingLargeSize)
    )
}

@Composable
private fun MainDrawerMenuItem(
    @DrawableRes vectorRes: Int? = null,
    vector: ImageVector? = null,
    @StringRes textRes: Int,
    selected: Boolean,
    onClick: (() -> Unit)? = null
) {
    Box {
        Box(
            modifier = Modifier
                .height(height = 48.dp)
                .fillMaxWidth()
                .padding(horizontal = ContentPaddingMediumSize, vertical = 2.dp)
                .align(alignment = Alignment.Center)
                .clip(shape = MaterialTheme.shapes.medium)
                .then(
                    other = if (selected) {
                        Modifier.background(color = MaterialTheme.colors.primary.copy(alpha = .12f))
                    } else {
                        Modifier
                    }
                )
                .clickable {
                    onClick?.invoke()
                }
        )
        val textIconColor = if (selected) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onBackground
        }

        ListItem(
            icon = {
                if (vectorRes != null) {
                    Icon(
                        contentDescription = stringResource(textRes),
                        painter = painterResource(id = vectorRes),
                        tint = textIconColor
                    )
                } else if (vector != null) {
                    Icon(
                        contentDescription = stringResource(textRes),
                        imageVector = vector,
                        tint = textIconColor
                    )
                }
            },
            modifier = Modifier.height(height = 48.dp)
        ) {
            Text(
                text = stringResource(textRes),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                color = textIconColor
            )
        }
    }
}

private data class MainNavigationRailMenu(
    val screen: Screen,
    val menuText: String,
    val menuIconPainter: Painter? = null,
    val menuIconVector: ImageVector? = null
)

@Composable
private fun MainNavigationRail(
    currentRoute: String,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        MainNavigationRailMenu(
            screen = Screen.Timeline,
            menuText = stringResource(id = R.string.navigation_menu_timeline),
            menuIconPainter = painterResource(id = R.drawable.ic_menu_timeline_24)
        ),
        MainNavigationRailMenu(
            screen = Screen.Explore,
            menuText = stringResource(id = R.string.navigation_menu_explore),
            menuIconPainter = painterResource(id = R.drawable.ic_menu_explore_24)
        ),
        MainNavigationRailMenu(
            screen = Screen.Inbox,
            menuText = stringResource(id = R.string.navigation_menu_inbox),
            menuIconPainter = painterResource(id = R.drawable.ic_menu_inbox_24)
        ),
        MainNavigationRailMenu(
            screen = Screen.GitHubStatus,
            menuText = stringResource(id = R.string.navigation_menu_github_status),
            menuIconPainter = painterResource(id = R.drawable.ic_api)
        ),
        MainNavigationRailMenu(
            screen = Screen.Settings,
            menuText = stringResource(id = R.string.navigation_menu_settings),
            menuIconVector = Icons.Outlined.Settings
        ),
        MainNavigationRailMenu(
            screen = Screen.About,
            menuText = stringResource(id = R.string.navigation_menu_about),
            menuIconVector = Icons.Outlined.Info
        ),
        MainNavigationRailMenu(
            screen = Screen.FAQ,
            menuText = stringResource(id = R.string.navigation_menu_faq_help),
            menuIconPainter = painterResource(id = R.drawable.ic_help_24)
        ),
        MainNavigationRailMenu(
            screen = Screen.Feedback,
            menuText = stringResource(id = R.string.navigation_menu_feedback),
            menuIconPainter = painterResource(id = R.drawable.ic_feedback_24)
        )
    )
    NavigationRail(modifier = modifier) {
        screens.forEach { (screen, menuText, menuIconPainter, menuIconVector) ->
            NavigationRailItem(
                selected = currentRoute == screen.route,
                label = { Text(text = menuText) },
                icon = {
                    if (menuIconPainter != null) {
                        Icon(
                            painter = menuIconPainter,
                            contentDescription = menuText
                        )
                    } else if (menuIconVector != null) {
                        Icon(
                            imageVector = menuIconVector,
                            contentDescription = menuText
                        )
                    }
                },
                onClick = {
                    navigate.invoke(screen.route)
                },
                alwaysShowLabel = false
            )
        }
    }
}

@Composable
private fun MainNavHost(
    currentRoute: MutableState<String>,
    startDestination: Screen,
    openDrawer: (() -> Unit)?
) {
    NavHost(
        navController = LocalNavController.current,
        startDestination = startDestination.route
    ) {
        composable(route = Screen.Timeline.route) {
            currentRoute.value = Screen.Timeline.route
            TimelineScreen(openDrawer = openDrawer)
        }
        composable(route = Screen.Inbox.route) {
            currentRoute.value = Screen.Inbox.route
            InboxScreen(openDrawer = openDrawer)
        }
        composable(route = Screen.Explore.route) {
            currentRoute.value = Screen.Explore.route
            ExploreScreen(openDrawer = openDrawer)
        }
        composable(route = Screen.Settings.route) {
            currentRoute.value = Screen.Settings.route
            SettingScreen()
        }
        composable(route = Screen.About.route) {
            currentRoute.value = Screen.About.route
            AboutScreen()
        }
        composable(
            route = Screen.Profile.route,
            arguments = listOf(
                navArgument(Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(Screen.ARG_PROFILE_TYPE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Profile.route

            val currentAccount = LocalAccountInstance.current ?: return@composable
            val login =
                backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN) ?: return@composable

            val viewModel = viewModel(
                key = currentAccount.signedInAccount.account.login,
                initializer = {
                    ProfileViewModel(
                        extra = ProfileViewModelExtra(
                            accountInstance = currentAccount,
                            login = login,
                            profileType = ProfileType.valueOf(
                                backStackEntry.arguments?.getString(Screen.ARG_PROFILE_TYPE)
                                    ?: ProfileType.NOT_SPECIFIED.name
                            )
                        )
                    )
                }
            )

            if (backStackEntry.savedStateHandle.contains(Screen.EditStatus.RESULT_UPDATE_STATUS)) {
                backStackEntry.savedStateHandle
                    .getLiveData<UserStatus>(Screen.EditStatus.RESULT_UPDATE_STATUS)
                    .value
                    .let {
                        viewModel.updateUserStatusIfNeeded(it)
                    }

                backStackEntry.savedStateHandle
                    .remove<UserStatus>(Screen.EditStatus.RESULT_UPDATE_STATUS)
            }

            ProfileScreen(viewModel = viewModel)
        }
        composable(
            route = Screen.Repository.route,
            arguments = listOf(
                navArgument(Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Repository.route
            RepositoryScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(
                navArgument(Screen.ARG_EDIT_PROFILE_NAME) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Screen.ARG_EDIT_PROFILE_BIO) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Screen.ARG_EDIT_PROFILE_URL) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Screen.ARG_EDIT_PROFILE_COMPANY) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Screen.ARG_EDIT_PROFILE_LOCATION) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(Screen.ARG_EDIT_PROFILE_TWITTER) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.EditProfile.route
            EditProfileScreen(
                initialName = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_NAME),
                initialBio = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_BIO),
                initialUrl = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_URL),
                initialCompany = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_COMPANY),
                initialLocation = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_LOCATION),
                initialTwitter = backStackEntry.arguments?.getString(Screen.ARG_EDIT_PROFILE_TWITTER)
            )
        }
        composable(
            route = Screen.EditStatus.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_EDIT_STATUS_EMOJI) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(name = Screen.ARG_EDIT_STATUS_MESSAGE) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(name = Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY) {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.EditStatus.route

            val currentAccount = LocalAccountInstance.current ?: return@composable

            val initialEmoji =
                backStackEntry.arguments?.getString(Screen.ARG_EDIT_STATUS_EMOJI)
            val initialMessage =
                backStackEntry.arguments?.getString(Screen.ARG_EDIT_STATUS_MESSAGE)
            val initialIndicatesLimitedAvailability = backStackEntry.arguments?.getBoolean(
                Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY
            )
            val viewModel = viewModel(
                initializer = {
                    EditStatusViewModel(
                        extra = EditStatusViewModelExtra(
                            accountInstance = currentAccount,
                            initialEmoji = initialEmoji,
                            initialMessage = initialMessage,
                            initialIndicatesLimitedAvailability = initialIndicatesLimitedAvailability
                        )
                    )
                }
            )

            backStackEntry.savedStateHandle
                .getLiveData<String>(Screen.Emojis.RESULT_EMOJI)
                .value
                .let { resultEmoji ->
                    if (!resultEmoji.isNullOrEmpty()) {
                        viewModel.updateEmoji(resultEmoji)
                    }

                    backStackEntry.savedStateHandle.remove<String>(Screen.Emojis.RESULT_EMOJI)
                }

            EditStatusScreen(
                initialEmoji = initialEmoji,
                initialMessage = initialMessage,
                initialIndicatesLimitedAvailability = initialIndicatesLimitedAvailability,
                viewModel = viewModel
            )
        }
        composable(
            route = Screen.Users.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_USERS_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Users.route
            UsersScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                usersType = UsersType.valueOf(
                    backStackEntry.arguments?.getString(Screen.ARG_USERS_TYPE)
                        ?: return@composable
                ),
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
            )
        }
        composable(
            route = Screen.Repositories.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_TYPE) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Repositories.route
            RepositoriesScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repositoryType = RepositoryType.valueOf(
                    backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_TYPE)
                        ?: return@composable
                ),
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
            )
        }
        composable(
            route = Screen.Issues.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPO_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Issues.route
            IssuesScreen(
                owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                repoId = backStackEntry.arguments?.getString(Screen.ARG_REPO_ID)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.PullRequests.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.PullRequests.route
            PullRequestsScreen(
                owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.Issue.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_ISSUE_PR_NUMBER) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Issue.route
            IssueScreen(
                owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                number = backStackEntry.arguments?.getInt(Screen.ARG_ISSUE_PR_NUMBER)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.PullRequest.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_ISSUE_PR_NUMBER) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.PullRequest.route
            PullRequestScreen(
                owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                number = backStackEntry.arguments?.getInt(Screen.ARG_ISSUE_PR_NUMBER)
                    ?: return@composable
            )
        }
        composable(route = Screen.Emojis.route) {
            val navController = LocalNavController.current
            currentRoute.value = Screen.Emojis.route
            val resultEmoji = navController.currentBackStackEntry?.savedStateHandle
                ?.getLiveData<String>(Screen.Emojis.RESULT_EMOJI)?.value

            if (!resultEmoji.isNullOrEmpty()) {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(Screen.Emojis.RESULT_EMOJI, resultEmoji)

                if (navController.currentDestination?.route == Screen.Emojis.route) {
                    navController.navigateUp()
                }
            } else {
                EmojisScreen()
            }
        }
        composable(route = Screen.SearchEmoji.route) {
            currentRoute.value = Screen.SearchEmoji.route
            SearchEmojiScreen()
        }
        composable(
            route = Screen.Search.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_INITIAL_SEARCH_KEYWORD) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Search.route
            SearchScreen(
                initialSearchKeyword = backStackEntry.arguments?.getString(Screen.ARG_INITIAL_SEARCH_KEYWORD)
                    ?: ""
            )
        }
        composable(
            route = Screen.RepositoryTopics.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.RepositoryTopics.route

            RepositoryTopicsScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.Commits.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_SELECTED_BRANCH_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REF_PREFIX) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_DEFAULT_BRANCH_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Commits.route

            val selectedBranchName = backStackEntry.savedStateHandle
                .getLiveData<String>(Screen.Branches.RESULT_BRANCH_NAME)
                .value

            CommitsScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                refPrefix = backStackEntry.arguments?.getString(Screen.ARG_REF_PREFIX)
                    ?: return@composable,
                defaultBranchName = backStackEntry.arguments?.getString(Screen.ARG_DEFAULT_BRANCH_NAME)
                    ?: return@composable,
                selectedBranchName = if (!selectedBranchName.isNullOrEmpty()) {
                    selectedBranchName
                } else {
                    backStackEntry.arguments?.getString(Screen.ARG_SELECTED_BRANCH_NAME)
                        ?: return@composable
                }
            )
        }
        composable(
            route = Screen.Releases.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Releases.route

            ReleasesScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.Commit.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REF) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Commit.route

            CommitScreen(
                owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repo = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                ref = backStackEntry.arguments?.getString(Screen.ARG_REF)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.RepositoryFiles.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_EXPRESSION) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REF_PREFIX) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_DEFAULT_BRANCH_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.RepositoryFiles.route

            val selectedBranchName = backStackEntry.savedStateHandle
                .getLiveData<String>(Screen.Branches.RESULT_BRANCH_NAME)
                .value

            RepositoryFilesScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                expression = if (!selectedBranchName.isNullOrEmpty()) {
                    "${selectedBranchName}:"
                } else {
                    backStackEntry.arguments?.getString(Screen.ARG_EXPRESSION)
                        ?: return@composable
                },
                refPrefix = backStackEntry.arguments?.getString(Screen.ARG_REF_PREFIX)
                    ?: return@composable,
                defaultBranchName = backStackEntry.arguments?.getString(Screen.ARG_DEFAULT_BRANCH_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.Branches.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_DEFAULT_BRANCH_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_SELECTED_BRANCH_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REF_PREFIX) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Branches.route

            BranchesScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                refPrefix = backStackEntry.arguments?.getString(Screen.ARG_REF_PREFIX)
                    ?: return@composable,
                defaultBranchName = backStackEntry.arguments?.getString(Screen.ARG_DEFAULT_BRANCH_NAME)
                    ?: return@composable,
                selectedBranchName = backStackEntry.arguments?.getString(Screen.ARG_SELECTED_BRANCH_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.Release.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_TAG_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.Release.route

            ReleaseScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                tagName = backStackEntry.arguments?.getString(Screen.ARG_TAG_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.ReleaseAssets.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_TAG_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.ReleaseAssets.route

            ReleaseAssetsScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                tagName = backStackEntry.arguments?.getString(Screen.ARG_TAG_NAME)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.CommentThread.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_NODE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.ReleaseAssets.route

            CommentTreadScreen(
                nodeId = backStackEntry.arguments?.getString(Screen.ARG_NODE_ID)
                    ?: return@composable
            )
        }
        composable(
            route = Screen.File.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_PROFILE_LOGIN) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_REPOSITORY_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_FILE_PATH) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_FILE_NAME) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_FILE_EXTENSION) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.File.route

            FileScreen(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@composable,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@composable,
                filePath = backStackEntry.arguments?.getString(Screen.ARG_FILE_PATH)
                    ?: return@composable,
                filename = backStackEntry.arguments?.getString(Screen.ARG_FILE_NAME)
                    ?: return@composable,
                fileExtension = backStackEntry.arguments?.getString(Screen.ARG_FILE_EXTENSION)
            )
        }
        composable(route = Screen.ExploreFilters.route) { backStackEntry ->
            currentRoute.value = Screen.ExploreFilters.route

            ExploreFiltersScreen(
                filtersType = FiltersType.valueOf(
                    backStackEntry.arguments?.getString(Screen.ARG_EXPLORE_FILTERS_TYPE)
                        ?: return@composable
                )
            )
        }
        dialog(route = Screen.AccountDialog.route) {
            // nav drawer/rail selected item ui not right
            // currentRoute.value = Screen.AccountDialog.route
            AccountDialogScreen()
        }
        dialog(route = Screen.ForkRepoDialog.route) { backStackEntry ->
            currentRoute.value = Screen.ForkRepoDialog.route

            ForkRepoDialog(
                login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                    ?: return@dialog,
                repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                    ?: return@dialog
            )
        }
        dialog(route = Screen.DownloadFileDialog.route) { backStackEntry ->
            currentRoute.value = Screen.DownloadFileDialog.route

            DownloadFileDialog(
                url = Uri.decode(
                    backStackEntry.arguments?.getString(Screen.ARG_URL) ?: return@dialog
                )
            )
        }
        composable(
            route = Screen.CreateIssue.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_REPO_ID) {
                    type = NavType.StringType
                },
                navArgument(name = Screen.ARG_DEFAULT_COMMENT) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.CreateIssue.route

            CreateIssueScreen(
                repoId = backStackEntry.arguments?.getString(Screen.ARG_REPO_ID)
                    ?: return@composable,
                defaultComment = backStackEntry.arguments?.getString(Screen.ARG_DEFAULT_COMMENT)
                    ?.let {
                        Uri.decode(it)
                    }
            )
        }
        composable(route = Screen.ManageAccounts.route) {
            currentRoute.value = Screen.ManageAccounts.route

            ManageAccountsScreen()
        }
        dialog(
            route = Screen.LogOutConfirmDialog.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_ACCOUNT_ID) {
                    type = NavType.LongType
                }
            )
        ) { navBackStackEntry ->
            currentRoute.value = Screen.LogOutConfirmDialog.route

            LogoutConfirmDialog(
                accountId = navBackStackEntry.arguments?.getLong(Screen.ARG_ACCOUNT_ID)
                    ?: return@dialog
            )
        }
        composable(
            route = Screen.ExceptionDetails.route,
            arguments = listOf(
                navArgument(name = Screen.ARG_EXCEPTION_DETAILS) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            currentRoute.value = Screen.ExceptionDetails.route

            ExceptionDetailsScreen(
                details = backStackEntry.arguments?.getString(Screen.ARG_EXCEPTION_DETAILS)?.let {
                    Uri.decode(it)
                } ?: return@composable)
        }
        dialog(route = Screen.FeedbackConfirmDialog.route) {
            currentRoute.value = Screen.FeedbackConfirmDialog.route

            FeedbackConfirmDialog()
        }
        composable(route = Screen.GitHubStatus.route) {
            currentRoute.value = Screen.GitHubStatus.route

            GitHubStatusScreen()
        }
        composable(route = Screen.GitHubIncident.route) { backStackEntry ->
            currentRoute.value = Screen.GitHubIncident.route

            IncidentScreen(
                incidentId = backStackEntry.arguments?.getString(Screen.ARG_INCIDENT_ID) ?: return@composable
            )
        }
    }
}

@Composable
@Preview(
    name = "MainDrawerContentPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
private fun MainDrawerContentPreview() {
    MainDrawerContent(
        currentRoute = Screen.Timeline.route,
        navigate = { }
    )
}

@Preview(
    name = "MainNavigationRailPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun MainNavigationRailPreview() {
    MainNavigationRail(
        currentRoute = Screen.Timeline.route,
        navigate = { },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false)
    )
}