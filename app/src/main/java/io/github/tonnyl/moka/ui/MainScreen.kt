package io.github.tonnyl.moka.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.ui.about.AboutScreen
import io.github.tonnyl.moka.ui.emojis.EmojisScreen
import io.github.tonnyl.moka.ui.emojis.search.SearchEmojiScreen
import io.github.tonnyl.moka.ui.explore.ExploreScreen
import io.github.tonnyl.moka.ui.inbox.InboxScreen
import io.github.tonnyl.moka.ui.issue.IssueScreen
import io.github.tonnyl.moka.ui.issues.IssuesScreen
import io.github.tonnyl.moka.ui.pr.PullRequestScreen
import io.github.tonnyl.moka.ui.profile.ProfileScreen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.profile.ProfileViewModel
import io.github.tonnyl.moka.ui.profile.edit.EditProfileScreen
import io.github.tonnyl.moka.ui.profile.status.EditStatusScreen
import io.github.tonnyl.moka.ui.profile.status.EditStatusViewModel
import io.github.tonnyl.moka.ui.prs.PullRequestsScreen
import io.github.tonnyl.moka.ui.repositories.RepositoriesScreen
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.repository.RepositoryScreen
import io.github.tonnyl.moka.ui.search.SearchScreen
import io.github.tonnyl.moka.ui.settings.SettingScreen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.timeline.TimelineScreen
import io.github.tonnyl.moka.ui.users.UsersScreen
import io.github.tonnyl.moka.ui.users.UsersType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.github.tonnyl.moka.ui.profile.ViewModelFactory as ProfileViewModelFactory
import io.github.tonnyl.moka.ui.profile.status.ViewModelFactory as EditStatusViewModelFactory

sealed class Screen(val route: String) {

    object Timeline : Screen("timeline")

    object Inbox : Screen("inbox")

    object Explore : Screen("explore")

    object Settings : Screen("settings")

    object About : Screen("about")

    object Profile : Screen("profile/{${ARG_PROFILE_LOGIN}}/{${ARG_PROFILE_TYPE}}")

    object EditProfile :
        Screen("edit_profile?${ARG_EDIT_PROFILE_NAME}={${ARG_EDIT_PROFILE_NAME}}?${ARG_EDIT_PROFILE_BIO}={${ARG_EDIT_PROFILE_BIO}}?${ARG_EDIT_PROFILE_URL}={${ARG_EDIT_PROFILE_URL}}?${ARG_EDIT_PROFILE_COMPANY}={${ARG_EDIT_PROFILE_COMPANY}}?${ARG_EDIT_PROFILE_LOCATION}={${ARG_EDIT_PROFILE_LOCATION}}?${ARG_EDIT_PROFILE_TWITTER}={${ARG_EDIT_PROFILE_TWITTER}}")

    object Repository :
        Screen("repository/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_PROFILE_TYPE}}")

    object Users : Screen("users/{${ARG_PROFILE_LOGIN}}/{${ARG_USERS_TYPE}}")

    object Repositories :
        Screen("repositories/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_TYPE}}/{${ARG_PROFILE_TYPE}}")

    object Issues : Screen("issues/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object PullRequests : Screen("pull_requests/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}")

    object Issue :
        Screen("issue/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_ISSUE_PR_NUMBER}}")

    object PullRequest :
        Screen("pull_request/{${ARG_PROFILE_LOGIN}}/{${ARG_REPOSITORY_NAME}}/{${ARG_ISSUE_PR_NUMBER}}")

    object Emojis : Screen("emojis") {

        const val RESULT_EMOJI = "result_emoji"

    }

    object SearchEmoji : Screen("search_emoji")

    object EditStatus :
        Screen("edit_status?${ARG_EDIT_STATUS_EMOJI}={${ARG_EDIT_STATUS_EMOJI}}?${ARG_EDIT_STATUS_MESSAGE}={${ARG_EDIT_STATUS_MESSAGE}}?${ARG_EDIT_STATUS_LIMIT_AVAILABILITY}={${ARG_EDIT_STATUS_LIMIT_AVAILABILITY}}") {

        const val RESULT_UPDATE_STATUS = "result_update_status"

    }

    object Search : Screen("search")

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

    }

}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalPagingApi
@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var currentRoute by remember { mutableStateOf(Screen.Timeline.route) }

    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawerContent(
                currentRoute = currentRoute,
                coroutineScope = coroutineScope,
                drawerState = drawerState,
                navController = navController,
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding(bottom = false)
            )
        }
    ) {
        val openDrawer = {
            coroutineScope.launch {
                drawerState.open()
            }
            Unit
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Timeline.route
        ) {
            composable(route = Screen.Timeline.route) {
                currentRoute = Screen.Timeline.route
                TimelineScreen(
                    openDrawer = openDrawer,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
            composable(route = Screen.Inbox.route) {
                currentRoute = Screen.Inbox.route
                InboxScreen(
                    openDrawer = openDrawer,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
            composable(route = Screen.Explore.route) {
                currentRoute = Screen.Explore.route
                ExploreScreen(
                    openDrawer = openDrawer,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
            composable(route = Screen.Settings.route) {
                currentRoute = Screen.Settings.route
                SettingScreen(navController = navController)
            }
            composable(route = Screen.About.route) {
                currentRoute = Screen.About.route
                AboutScreen(navController = navController)
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
                currentRoute = Screen.Profile.route

                val currentAccount = LocalAccountInstance.current ?: return@composable

                val viewModel = viewModel<ProfileViewModel>(
                    key = currentAccount.signedInAccount.account.login,
                    factory = ProfileViewModelFactory(
                        accountInstance = currentAccount,
                        login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                            ?: return@composable,
                        profileType = ProfileType.valueOf(
                            backStackEntry.arguments?.getString(Screen.ARG_PROFILE_TYPE)
                                ?: ProfileType.NOT_SPECIFIED.name
                        )
                    )
                )

                backStackEntry.savedStateHandle
                    .getLiveData<UserStatus>(Screen.EditStatus.RESULT_UPDATE_STATUS)
                    .value
                    ?.let {
                        viewModel.updateUserStatusIfNeeded(it)
                    }

                ProfileScreen(
                    mainViewModel = mainViewModel,
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(
                route = Screen.Repository.route,
                arguments = listOf(
                    navArgument(Screen.ARG_PROFILE_LOGIN) {
                        type = NavType.StringType
                    },
                    navArgument(Screen.ARG_REPOSITORY_NAME) {
                        type = NavType.StringType
                    },
                    navArgument(Screen.ARG_EDIT_PROFILE_NAME) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                currentRoute = Screen.Repository.route
                RepositoryScreen(
                    navController = navController,
                    login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                        ?: return@composable,
                    repoName = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                        ?: return@composable,
                    profileType = ProfileType.valueOf(
                        backStackEntry.arguments?.getString(Screen.ARG_PROFILE_TYPE)
                            ?: ProfileType.NOT_SPECIFIED.name
                    )
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
                currentRoute = Screen.EditProfile.route
                EditProfileScreen(
                    navController = navController,
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
                currentRoute = Screen.EditStatus.route

                val currentAccount = LocalAccountInstance.current ?: return@composable

                val initialEmoji = backStackEntry.arguments?.getString(Screen.ARG_EDIT_STATUS_EMOJI)
                val initialMessage =
                    backStackEntry.arguments?.getString(Screen.ARG_EDIT_STATUS_MESSAGE)
                val initialIndicatesLimitedAvailability = backStackEntry.arguments?.getBoolean(
                    Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY
                )
                val viewModel = viewModel<EditStatusViewModel>(
                    factory = EditStatusViewModelFactory(
                        accountInstance = currentAccount,
                        emoji = initialEmoji,
                        message = initialMessage,
                        indicatesLimitedAvailability = initialIndicatesLimitedAvailability
                    )
                )

                backStackEntry.savedStateHandle
                    .getLiveData<String>(Screen.Emojis.RESULT_EMOJI)
                    .value
                    ?.let { resultEmoji ->
                        if (resultEmoji.isNotEmpty()) {
                            viewModel.updateEmoji(resultEmoji)
                        }
                    }


                EditStatusScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
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
                    }
                )
            ) { backStackEntry ->
                currentRoute = Screen.Users.route
                UsersScreen(
                    navController = navController,
                    login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                        ?: return@composable,
                    usersType = UsersType.valueOf(
                        backStackEntry.arguments?.getString(Screen.ARG_USERS_TYPE)
                            ?: return@composable
                    )
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
                    navArgument(name = Screen.ARG_PROFILE_TYPE) {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                currentRoute = Screen.Repositories.route
                RepositoriesScreen(
                    navController = navController,
                    login = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                        ?: return@composable,
                    repositoryType = RepositoryType.valueOf(
                        backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_TYPE)
                            ?: return@composable
                    ),
                    profileType = ProfileType.valueOf(
                        backStackEntry.arguments?.getString(Screen.ARG_PROFILE_TYPE)
                            ?: ProfileType.NOT_SPECIFIED.name
                    )
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
                    }
                )
            ) { backStackEntry ->
                currentRoute = Screen.Issues.route
                IssuesScreen(
                    navController = navController,
                    owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                        ?: return@composable,
                    name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
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
                currentRoute = Screen.PullRequests.route
                PullRequestsScreen(
                    navController = navController,
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
                currentRoute = Screen.Issue.route
                IssueScreen(
                    navController = navController,
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
                currentRoute = Screen.PullRequest.route
                PullRequestScreen(
                    navController = navController,
                    owner = backStackEntry.arguments?.getString(Screen.ARG_PROFILE_LOGIN)
                        ?: return@composable,
                    name = backStackEntry.arguments?.getString(Screen.ARG_REPOSITORY_NAME)
                        ?: return@composable,
                    number = backStackEntry.arguments?.getInt(Screen.ARG_ISSUE_PR_NUMBER)
                        ?: return@composable
                )
            }
            composable(route = Screen.Emojis.route) {
                currentRoute = Screen.Emojis.route
                val resultEmoji = navController.currentBackStackEntry?.savedStateHandle
                    ?.getLiveData<String>(Screen.Emojis.RESULT_EMOJI)?.value

                if (!resultEmoji.isNullOrEmpty()) {
                    navController.previousBackStackEntry?.savedStateHandle
                        ?.set(Screen.Emojis.RESULT_EMOJI, resultEmoji)

                    navController.navigateUp()
                } else {
                    EmojisScreen(
                        navController = navController,
                        mainViewModel = mainViewModel
                    )
                }
            }
            composable(route = Screen.SearchEmoji.route) {
                currentRoute = Screen.SearchEmoji.route
                SearchEmojiScreen(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
            composable(route = Screen.Search.route) {
                currentRoute = Screen.Search.route
                SearchScreen(navController = navController)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun MainDrawerContent(
    currentRoute: String,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navigate: (String, Boolean) -> Unit = { route, _ ->
        navController.navigateUp()
        navController.navigate(route = route) {
            popUpTo = navController.graph.startDestination
            launchSingleTop = true
        }

        coroutineScope.launch {
            drawerState.close()
        }
    }

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
                    navigate.invoke(Screen.Timeline.route, true)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_explore_24,
                textRes = R.string.navigation_menu_explore,
                selected = currentRoute == Screen.Explore.route,
                onClick = {
                    navigate.invoke(Screen.Explore.route, true)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_inbox_24,
                textRes = R.string.navigation_menu_inbox,
                selected = currentRoute == Screen.Inbox.route,
                onClick = {
                    navigate.invoke(Screen.Inbox.route, true)
                }
            )
            Divider(modifier = Modifier.fillMaxWidth())
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_menu_settings_24,
                textRes = R.string.navigation_menu_settings,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.Settings.route, false)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_info_24,
                textRes = R.string.navigation_menu_about,
                selected = false,
                onClick = {
                    navigate.invoke(Screen.About.route, false)
                }
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_help_24,
                textRes = R.string.navigation_menu_faq_help,
                selected = false
            )
        }
        item {
            MainDrawerMenuItem(
                vectorRes = R.drawable.ic_feedback_24,
                textRes = R.string.navigation_menu_feedback,
                selected = false
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

@ExperimentalMaterialApi
@Composable
private fun MainDrawerMenuItem(
    @DrawableRes vectorRes: Int,
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
                Icon(
                    contentDescription = stringResource(textRes),
                    painter = painterResource(id = vectorRes),
                    tint = textIconColor
                )
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

@ExperimentalMaterialApi
@Composable
@Preview(name = "MainDrawerContentPreview", showBackground = true)
private fun MainDrawerContentPreview() {
    MainDrawerContent(
        currentRoute = Screen.Timeline.route,
        coroutineScope = rememberCoroutineScope(),
        drawerState = rememberDrawerState(DrawerValue.Open),
        navController = rememberNavController()
    )
}